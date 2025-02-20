package distinctsocketeddescriptions.activator;

import distinctsocketeddescriptions.compat.FirstAidCompat;
import distinctsocketeddescriptions.compat.ModLoaded;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import socketed.common.capabilities.effectscache.CapabilityEffectsCacheHandler;
import socketed.common.capabilities.effectscache.ICapabilityEffectsCache;
import socketed.common.capabilities.socketable.CapabilitySocketableHandler;
import socketed.common.capabilities.socketable.ICapabilitySocketable;
import socketed.common.socket.gem.effect.activatable.activator.AttackActivator;
import socketed.common.socket.gem.effect.activatable.activator.GenericActivator;
import socketed.common.socket.gem.effect.activatable.callback.GenericEventCallback;
import socketed.common.socket.gem.effect.activatable.condition.DamageSourceCondition;
import socketed.common.socket.gem.effect.activatable.condition.GenericCondition;
import socketed.common.socket.gem.effect.slot.SocketedSlotTypes;
import yeelp.distinctdamagedescriptions.api.DDDAPI;
import yeelp.distinctdamagedescriptions.capability.IDDDCombatTracker;
import yeelp.distinctdamagedescriptions.event.classification.DDDClassificationEvent;
import yeelp.distinctdamagedescriptions.event.classification.DetermineDamageEvent;
import yeelp.distinctdamagedescriptions.event.classification.GatherDefensesEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class DDDAttackedActivator extends AttackActivator {
    public static final String TYPE_NAME = "DDD Attacked";

    public DDDAttackedActivator(@Nullable GenericCondition condition, boolean directlyActivated) {
        super(condition, directlyActivated);
    }

    //TODO add tooltip
    @Override
    public String getTooltipString() {
        return "";
    }

    @Override
    public String getTypeName() {
        return TYPE_NAME;
    }

    @Mod.EventBusSubscriber
    public static class EventHandler {
        /**
         * Modify players GatherDefensesEvent, add resistances or immunities to the player
         * runs after DDDAttackingActivator.onGatherDefenses, so in pvp the defenders gems always activate after the attackers gems
         */
        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void onGatherDefenses(GatherDefensesEvent event) {
            handleAttacked(event);
        }

        /**
         * Modify attackers DetermineDamageEvent, reduce specific damage types from their dmg map or modify their damage map
         * runs after DDDAttackingActivator.onDetermineDamage, so in pvp the defenders gems always activate after the attackers gems
         * using this is NOT RECOMMENDED, rather add resistances or immunities to the player using GatherDefensesEvent
         */
        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void onDetermineDamage(DetermineDamageEvent event) {
            //DetermineDamage doesn't guarantee that there is a nonnull attacker
            if(event.getTrueAttacker() == null && event.getImmediateAttacker() == null) return;
            handleAttacked(event);
        }

        //DDDAttackedActivator handling
        private static void handleAttacked(DDDClassificationEvent event){
            //Guaranteed serverside since the event is fired in LivingDamageEvent
            //Defender, Source and Player are @Nonnull, attacker can be null

            //Prioritize checking the direct attacker, prevent ranged pet attacks from triggering attacker effects such as lycanites
            DamageSource source = event.getSource();
            boolean isMelee = DamageSourceCondition.isDamageSourceMelee(source);
            boolean isRanged = !isMelee && DamageSourceCondition.isDamageSourceRanged(source);

            EntityLivingBase target = event.getDefender();
            EntityLivingBase attacker;
            if(isMelee) attacker = (EntityLivingBase)event.getImmediateAttacker();
            else if(isRanged) attacker = (EntityLivingBase)event.getTrueAttacker();
            else return;

            //Don't trigger on self damage if that manages to happen
            if(target == attacker) return;

            if (!(target instanceof EntityPlayer)) return;
            EntityPlayer player = (EntityPlayer) target;

            GenericEventCallback<? extends DDDClassificationEvent> eventCallback = new GenericEventCallback<>(event);

            //Handle cached effects
            ICapabilityEffectsCache cachedEffects = player.getCapability(CapabilityEffectsCacheHandler.CAP_EFFECTS_CACHE, null);
            if (cachedEffects != null)
                GenericActivator.filterForActivator(cachedEffects.getActiveEffects(), DDDAttackedActivator.class)
                        .forEach(effect -> ((DDDAttackedActivator) effect.getActivator()).attemptAttackActivation(effect, eventCallback, player, attacker, false));

            //Handle direct activation effects
            List<ItemStack> directActivationStacks = new ArrayList<>();

            //Get all hit body slots from DDD combat tracker -> armor classification -> armor map
            //This will usually be all armored body slots
            if(!ModLoaded.isFirstAidLoaded()) {
                DDDAPI.accessor.getDDDCombatTracker(player).flatMap(IDDDCombatTracker::getArmorClassification).ifPresent(
                        ac -> ac.forEachArmorMap(
                                (entityEquipmentSlot, armorMap) ->
                                        directActivationStacks.add(player.getItemStackFromSlot(entityEquipmentSlot))
                        )
                );
            } else {
                List<EntityEquipmentSlot> affectedSlots = FirstAidCompat.getAndClearAffectedBodySlots();
                affectedSlots.forEach(slot -> directActivationStacks.add(player.getItemStackFromSlot(slot)));
            }

            //DistinctSocketedDescriptions.LOGGER.info("Hit body parts: {}",directActivationStacks.size());
            //directActivationStacks.forEach(stack -> player.sendMessage(new TextComponentString(stack.getItem().getRegistryName().toString() )));

            for(ItemStack stack : directActivationStacks) {
                ICapabilitySocketable sockets = stack.getCapability(CapabilitySocketableHandler.CAP_SOCKETABLE, null);
                if (sockets != null)
                    GenericActivator.filterForActivator(sockets.getAllActiveEffects(SocketedSlotTypes.BODY), DDDAttackedActivator.class)
                            .forEach(effect -> ((DDDAttackedActivator) effect.getActivator()).attemptAttackActivation(effect, eventCallback, player, attacker, true));
            }
        }
    }
}