package distinctsocketeddescriptions.activator;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
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
import yeelp.distinctdamagedescriptions.event.classification.DDDClassificationEvent;
import yeelp.distinctdamagedescriptions.event.classification.DetermineDamageEvent;
import yeelp.distinctdamagedescriptions.event.classification.GatherDefensesEvent;

import javax.annotation.Nullable;

public class DDDAttackingActivator extends AttackActivator {
    public static final String TYPE_NAME = "DDD Attacking";

    public DDDAttackingActivator(@Nullable GenericCondition condition, boolean directlyActivated) {
        super(condition, directlyActivated);
    }

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
         * Modify players DetermineDamageEvent, in order to add damages of certain types or modify the damage type map
         * runs before DDDAttackingActivator.onDetermineDamage, so in pvp the defenders gems always activate after the attackers gems
         */
        @SubscribeEvent(priority = EventPriority.LOW)
        public static void onDetermineDamage(DetermineDamageEvent event) {
            //DetermineDamage doesn't guarantee that there is a nonnull attacker
            if(event.getTrueAttacker() == null && event.getImmediateAttacker() == null) return;
            handleAttacking(event);
        }

        /**
         * Modify targets GatherDefensesEvent, to remove their resistances or ignore their immunities
         * runs before DDDAttackedActivator.onGatherDefenses, so in pvp the defenders gems always activate after the attackers gems
         */
        @SubscribeEvent(priority = EventPriority.LOW)
        public static void onGatherDefenses(GatherDefensesEvent event) {
            handleAttacking(event);
        }

        //DDDAttackingActivator handling
        private static void handleAttacking(DDDClassificationEvent event){
            //Guaranteed serverside, since both DDD Events run on LivingDamageEvent
            //Source, attackers, defender are guaranteed @Nonnull

            DamageSource source = event.getSource();

            //Prioritize checking the direct attacker, prevent ranged pet attacks from triggering attacker effects such as lycanites
            //This also auto checks whether the attacker is null
            boolean isMelee = DamageSourceCondition.isDamageSourceMelee(source);
            boolean isRanged = !isMelee && DamageSourceCondition.isDamageSourceRanged(source);

            EntityLivingBase target = event.getDefender();
            EntityLivingBase attacker;
            if (isMelee) attacker = (EntityLivingBase) source.getImmediateSource();
            else if (isRanged) attacker = (EntityLivingBase) source.getTrueSource();
            //TODO: what kind of attacks do we cancel here?
            // i guess only the ones where both immediate and true source are null / not entityLivingBase?
            else return;

            //Don't trigger on self damage if that manages to happen
            if (target == attacker) return;

            if (!(attacker instanceof EntityPlayer)) return;
            EntityPlayer player = (EntityPlayer) attacker;

            GenericEventCallback<? extends DDDClassificationEvent> callback = new GenericEventCallback<>(event);

            //Handle cached effects
            ICapabilityEffectsCache cachedEffects = player.getCapability(CapabilityEffectsCacheHandler.CAP_EFFECTS_CACHE, null);
            if (cachedEffects != null)
                GenericActivator.filterForActivator(cachedEffects.getActiveEffects(), DDDAttackingActivator.class)
                        .forEach(effect -> ((DDDAttackingActivator) effect.getActivator()).attemptAttackActivation(effect, callback, player, target, false));

            //Handle direct activation effects
            //RLCombat swaps offhand into mainhand before posting LivingAttack for offhand attacks, which is still the case during this event
            ItemStack weaponStack = player.getHeldItemMainhand();
            if (weaponStack.isEmpty()) return;

            ICapabilitySocketable sockets = weaponStack.getCapability(CapabilitySocketableHandler.CAP_SOCKETABLE, null);
            if (sockets != null)
                GenericActivator.filterForActivator(sockets.getAllActiveEffects(SocketedSlotTypes.HAND), DDDAttackingActivator.class)
                        .forEach(effect -> ((DDDAttackingActivator) effect.getActivator()).attemptAttackActivation(effect, callback, player, target, true));
        }
    }
}