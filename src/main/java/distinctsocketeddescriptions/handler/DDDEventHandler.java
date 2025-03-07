package distinctsocketeddescriptions.handler;

import distinctsocketeddescriptions.compat.FirstAidCompat;
import distinctsocketeddescriptions.compat.ModLoaded;
import distinctsocketeddescriptions.effect.DDDEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
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
import socketed.api.socket.gem.effect.activatable.callback.GenericEventCallback;
import socketed.api.socket.gem.effect.slot.SocketedSlotTypes;
import yeelp.distinctdamagedescriptions.api.DDDAPI;
import yeelp.distinctdamagedescriptions.capability.IDDDCombatTracker;
import yeelp.distinctdamagedescriptions.event.classification.DDDClassificationEvent;
import yeelp.distinctdamagedescriptions.event.classification.DetermineDamageEvent;
import yeelp.distinctdamagedescriptions.event.classification.GatherDefensesEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Mod.EventBusSubscriber
public class DDDEventHandler {
    /**
     * Modify attackers DetermineDamageEvent, reduce specific damage types from their dmg map or modify their damage map, using this is NOT RECOMMENDED, rather add resistances or immunities to the player using GatherDefensesEvent
     * Modify players DetermineDamageEvent, in order to add damages of certain types or modify the damage type map
     * So in pvp the defenders gems always activate after the attackers gems
     */
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onDetermineDamage(DetermineDamageEvent event) {
        //DetermineDamage doesn't guarantee that there is a nonnull attacker
        if (event.getTrueAttacker() == null && event.getImmediateAttacker() == null) return;

        handleAttacking(event);
        handleAttacked(event);
    }

    /**
     * Modify targets GatherDefensesEvent, to remove their resistances or ignore their immunities
     * Modify players GatherDefensesEvent, add resistances or immunities to the player
     * So in pvp the defenders gems always activate after the attackers gems
     */
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onGatherDefenses(GatherDefensesEvent event) {
        handleAttacking(event);
        handleAttacked(event);
    }

    private static void handleAttacking(DDDClassificationEvent event) {
        //Guaranteed serverside, since both DDD Events run on LivingDamageEvent
        //Source, attacker, defender are guaranteed @Nonnull

        DamageSource source = event.getSource();

        //Prioritize checking the direct attacker, prevent ranged pet attacks from triggering attacker effects such as lycanites
        //This also auto checks whether the attacker is null

        Entity attacker = source.getImmediateSource();
        if (!(attacker instanceof EntityPlayer)) attacker = source.getTrueSource();
        if (!(attacker instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) attacker;

        GenericEventCallback<? extends DDDClassificationEvent> callback = new GenericEventCallback<>(event);

        //Handle cached effects
        ICapabilityEffectsCache cachedEffects = player.getCapability(CapabilityEffectsCacheHandler.CAP_EFFECTS_CACHE, null);
        if (cachedEffects != null)
            filterEffects(cachedEffects.getActiveEffects())
                    .forEach(effect -> effect.performEffect(callback, false));

        //Handle direct activation effects
        //RLCombat swaps offhand into mainhand before posting LivingAttack for offhand attacks, which is still the case during this event
        ItemStack weaponStack = player.getHeldItemMainhand();
        if (weaponStack.isEmpty()) return;

        ICapabilitySocketable sockets = weaponStack.getCapability(CapabilitySocketableHandler.CAP_SOCKETABLE, null);
        if (sockets != null)
            filterEffects(sockets.getAllActiveEffects(SocketedSlotTypes.HAND))
                    .forEach(effect -> effect.performEffect(callback, true));
    }

    private static void handleAttacked(DDDClassificationEvent event) {
        //Guaranteed serverside since the event is fired in LivingDamageEvent
        //Defender, Source and Player are @Nonnull, attacker can be null but is unused

        EntityLivingBase target = event.getDefender();

        if (!(target instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) target;

        GenericEventCallback<? extends DDDClassificationEvent> callback = new GenericEventCallback<>(event);

        //Handle cached effects
        ICapabilityEffectsCache cachedEffects = player.getCapability(CapabilityEffectsCacheHandler.CAP_EFFECTS_CACHE, null);
        if (cachedEffects != null)
            filterEffects(cachedEffects.getActiveEffects())
                    .forEach(effect -> effect.performEffect(callback, false));

        //Handle direct activation effects
        List<ItemStack> directActivationStacks = new ArrayList<>();

        //Get all hit body slots from DDD combat tracker -> armor classification -> armor map
        //This will usually be all armored body slots
        if (!ModLoaded.isFirstAidLoaded())
            DDDAPI.accessor.getDDDCombatTracker(player).flatMap(IDDDCombatTracker::getArmorClassification).ifPresent(
                    ac -> ac.forEachArmorMap(
                            (entityEquipmentSlot, armorMap) -> directActivationStacks.add(player.getItemStackFromSlot(entityEquipmentSlot)))
            );
        else
            FirstAidCompat.getAndClearAffectedBodySlots()
                    .forEach(slot -> directActivationStacks.add(player.getItemStackFromSlot(slot)));

        //DistinctSocketedDescriptions.LOGGER.info("Hit body parts: {}",directActivationStacks.size());
        //directActivationStacks.forEach(stack -> player.sendMessage(new TextComponentString(stack.getItem().getRegistryName().toString() )));

        for (ItemStack stack : directActivationStacks) {
            ICapabilitySocketable sockets = stack.getCapability(CapabilitySocketableHandler.CAP_SOCKETABLE, null);
            if (sockets != null)
                filterEffects(sockets.getAllActiveEffects(SocketedSlotTypes.BODY))
                        .forEach(effect -> effect.performEffect(callback, true));
        }
    }

    private static Stream<DDDEffect> filterEffects(List<GenericGemEffect> effects) {
        return effects.stream()
                .filter(effect -> effect instanceof DDDEffect)
                .map(DDDEffect.class::cast);
    }
}
