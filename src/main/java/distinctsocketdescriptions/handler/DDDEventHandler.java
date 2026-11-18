package distinctsocketdescriptions.handler;

import distinctsocketdescriptions.compat.FirstAidCompat;
import distinctsocketdescriptions.compat.ModLoaded;
import distinctsocketdescriptions.effect.DDDDamageEffect;
import distinctsocketdescriptions.effect.DDDEffect;
import distinctsocketdescriptions.effect.activator.DDDActivator;
import distinctsocketdescriptions.effect.activator.DDDAttackedActivator;
import distinctsocketdescriptions.effect.activator.DDDAttackingActivator;
import distinctsocketdescriptions.effect.callback.DDDDamageCallback;
import distinctsocketdescriptions.effect.callback.DDDEventCallback;
import distinctsocketdescriptions.mixin.ddd.CombatContextAccessor;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import socketed.api.common.capabilities.effectscache.CapabilityEffectsCacheHandler;
import socketed.api.common.capabilities.effectscache.ICapabilityEffectsCache;
import socketed.api.common.capabilities.socketable.CapabilitySocketableHandler;
import socketed.api.common.capabilities.socketable.ICapabilitySocketable;
import socketed.api.socket.gem.effect.GenericGemEffect;
import socketed.api.socket.gem.effect.activatable.ActivatableGemEffect;
import socketed.api.socket.gem.effect.activatable.callback.IEffectCallback;
import socketed.api.socket.gem.effect.slot.SocketedSlotTypes;
import socketed.api.util.SocketedUtil;
import yeelp.distinctdamagedescriptions.api.DDDAPI;
import yeelp.distinctdamagedescriptions.capability.IDDDCombatTracker;
import yeelp.distinctdamagedescriptions.event.classification.DDDClassificationEvent;
import yeelp.distinctdamagedescriptions.event.classification.DetermineDamageEvent;
import yeelp.distinctdamagedescriptions.event.classification.GatherDefensesEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Mod.EventBusSubscriber
public class DDDEventHandler {
    private static final Predicate<GenericGemEffect> IS_DAMAGE_EFFECT = effect -> effect instanceof DDDDamageEffect;
    private static final Predicate<GenericGemEffect> IS_DEFENSE_EFFECT = effect -> effect instanceof DDDEffect && !(effect instanceof DDDDamageEffect);

    /**
     * Modify the DetermineDamageEvent, in order to add damages of certain types or modify the damage type map.
     * Attacking players gems activate here, as well as cached gems of attacked players.
     * Direct activation of attacked players can't happen here, since this runs on LivingAttack, so it's too early to know which body parts are hit
     */
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onDetermineDamage(DetermineDamageEvent event) {
        EntityLivingBase attacker = getAttacker(event);
        //Damage effects only modify the damage of an attacker
        if (attacker == null) return;
        //Prevent adding flat damage again when DDD reclassifies the remaining damage of a FirstAid distribution
        if (ModLoaded.isFirstAidLoaded() && FirstAidCompat.isFollowUpRound(event.getDefender())) return;

        //Damage effects are collected first and applied together, so their operations always run in the same order
        DDDDamageCallback callback = new DDDDamageCallback(event, attacker);
        handleAttacking(callback, attacker, event.getDefender(), IS_DAMAGE_EFFECT);
        handleAttacked(callback, attacker, event.getDefender(), false, IS_DAMAGE_EFFECT);
        if (!callback.getDamageEffects().isEmpty()) DDDDamageEffect.applyAll(event, callback.getDamageEffects());
    }

    /**
     * Modify targets GatherDefensesEvent, to remove their resistances or ignore their immunities
     * Modify players GatherDefensesEvent, add resistances or immunities to the player
     * In pvp the attackers gems activate before the defenders gems
     */
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onGatherDefenses(GatherDefensesEvent event) {
        EntityLivingBase attacker = getAttacker(event);
        DDDEventCallback<GatherDefensesEvent> callback = new DDDEventCallback<>(event, attacker);
        handleAttacking(callback, attacker, event.getDefender(), IS_DEFENSE_EFFECT);
        handleAttacked(callback, attacker, event.getDefender(), true, IS_DEFENSE_EFFECT);
    }

    /**
     * Same as Socketed's attack activators: the immediate source for melee, the true source for ranged attacks.
     * This prevents ranged pet attacks from triggering attacker effects such as lycanites
     * @return the living attacker, null if there is none or if it is the defender itself
     */
    @Nullable
    private static EntityLivingBase getAttacker(DDDClassificationEvent event) {
        //Guaranteed serverside, source and defender are guaranteed @Nonnull
        DamageSource source = event.getSource();
        EntityLivingBase attacker = null;
        if (SocketedUtil.isDamageSourceMelee(source)) attacker = (EntityLivingBase) source.getImmediateSource();
        else if (SocketedUtil.isDamageSourceRanged(source)) attacker = (EntityLivingBase) source.getTrueSource();
        return attacker == event.getDefender() ? null : attacker;
    }

    private static void handleAttacking(IEffectCallback callback, @Nullable EntityLivingBase attacker, EntityLivingBase defender, Predicate<GenericGemEffect> effectFilter) {
        if (!(attacker instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) attacker;

        //Handle cached effects
        ICapabilityEffectsCache cachedEffects = player.getCapability(CapabilityEffectsCacheHandler.CAP_EFFECTS_CACHE, null);
        if (cachedEffects != null)
            activate(cachedEffects.getEffectsForActivator(DDDAttackingActivator.class).stream(), effectFilter, callback, player, defender, false);

        //Handle direct activation effects
        //RLCombat swaps offhand into mainhand before posting LivingAttack for offhand attacks, which is still the case during this event
        ItemStack weaponStack = player.getHeldItemMainhand();
        if (weaponStack.isEmpty()) return;

        ICapabilitySocketable sockets = weaponStack.getCapability(CapabilitySocketableHandler.CAP_SOCKETABLE, null);
        if (sockets != null)
            activate(SocketedUtil.filterForActivator(sockets.getAllActiveEffects(SocketedSlotTypes.HAND), DDDAttackingActivator.class), effectFilter, callback, player, defender, true);
    }

    private static void handleAttacked(IEffectCallback callback, @Nullable EntityLivingBase attacker, EntityLivingBase defender, boolean handleDirectActivation, Predicate<GenericGemEffect> effectFilter) {
        if (!(defender instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) defender;
        //Damage without a living attacker (for example lava) still activates defensive effects, the player is the other entity then
        EntityLivingBase other = attacker != null ? attacker : player;

        //Handle cached effects
        ICapabilityEffectsCache cachedEffects = player.getCapability(CapabilityEffectsCacheHandler.CAP_EFFECTS_CACHE, null);
        if (cachedEffects != null)
            activate(cachedEffects.getEffectsForActivator(DDDAttackedActivator.class).stream(), effectFilter, callback, player, other, false);

        if (!handleDirectActivation) return;

        //Handle direct activation effects
        List<ItemStack> directActivationStacks = new ArrayList<>();

        //Get all hit body slots from the valid armor slots of the current DDD calculation
        //This will usually be all body slots, also includes non-ItemArmor stacks like elytras or pumpkins
        if (!ModLoaded.isFirstAidLoaded())
            directActivationStacks.addAll(getDDDHitStacks(player));
        else {
            //Only set while FirstAid is firing LivingDamage for a damage distribution round
            Set<EntityEquipmentSlot> hitSlots = FirstAidCompat.getHitSlots(player);
            if (hitSlots != null)
                hitSlots.forEach(slot -> directActivationStacks.add(player.getItemStackFromSlot(slot)));
            else
                //Damage that doesn't go through a FirstAid distribution (fake players, infinite damage, other mods posting LivingDamage directly)
                directActivationStacks.addAll(getDDDHitStacks(player));
        }

        for (ItemStack stack : directActivationStacks) {
            if (stack.isEmpty()) continue;
            ICapabilitySocketable sockets = stack.getCapability(CapabilitySocketableHandler.CAP_SOCKETABLE, null);
            if (sockets != null)
                activate(SocketedUtil.filterForActivator(sockets.getAllActiveEffects(SocketedSlotTypes.BODY), DDDAttackedActivator.class), effectFilter, callback, player, other, true);
        }
    }

    private static void activate(Stream<ActivatableGemEffect> effects, Predicate<GenericGemEffect> effectFilter, IEffectCallback callback, EntityPlayer player, EntityLivingBase other, boolean directlyActivated) {
        effects.filter(effectFilter).forEach(effect -> ((DDDActivator) effect.getActivator()).attemptActivation(effect, callback, player, other, directlyActivated));
    }

    private static List<ItemStack> getDDDHitStacks(EntityPlayer player) {
        List<ItemStack> stacks = new ArrayList<>();
        DDDAPI.accessor.getDDDCombatTracker(player).flatMap(IDDDCombatTracker::getCurrentCalculation).ifPresent(dc -> {
            for (EntityEquipmentSlot slot : ((CombatContextAccessor) (Object) dc.getContext()).dsd$getValidArmorSlots())
                stacks.add(player.getItemStackFromSlot(slot));
        });
        return stacks;
    }

}
