package distinctsocketeddescriptions.activator;

import distinctsocketeddescriptions.effect.DDDDamageEffect;
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
import socketed.common.socket.gem.effect.GenericGemEffect;
import socketed.common.socket.gem.effect.activatable.ActivatableGemEffect;
import socketed.common.socket.gem.effect.activatable.activator.GenericActivator;
import socketed.common.socket.gem.effect.activatable.activator.attack.AttackingActivator;
import socketed.common.socket.gem.effect.slot.SocketedSlotTypes;
import yeelp.distinctdamagedescriptions.event.classification.DetermineDamageEvent;

public class DDDAttackingActivator extends AttackingActivator {
    public static final String TYPE_NAME = "DDD Attacking";

    public DDDAttackingActivator(boolean affectsSelf, boolean affectsTarget, boolean allowsMelee, boolean allowsRanged, boolean directlyActivated) {
        super(affectsSelf, affectsTarget, allowsMelee, allowsRanged, directlyActivated);
    }

    @Override
    public void attemptAttackActivation(ActivatableGemEffect effect, EntityPlayer player, EntityLivingBase target, EntityLivingBase attacker, boolean isMelee, boolean isRanged, boolean directlyActivated, DamageSource source) {
        //no op, only gets called from Socketed via LivingAttackEvent
    }

    public void attemptAttackActivation(DDDDamageEffect effect, DetermineDamageEvent event, boolean isMelee, boolean isRanged, boolean directlyActivated) {
        //Check if direct activation is required
        if (directlyActivated != this.getDirectlyActivated()) return;
        if ((this.getAllowsMelee() && isMelee) || (this.getAllowsRanged() && isRanged))
            effect.performEffect(event);
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
        @SubscribeEvent(priority = EventPriority.LOW)
        public static void onDetermineDamage(DetermineDamageEvent event) {
            //if(event.getDefender() == null) return;
            if (event.getDefender().world.isRemote) return;
            //if(event.getSource() == null) return;
            DamageSource source = event.getSource();

            //Prioritize checking the direct attacker, prevent ranged pet attacks from triggering attacker effects such as lycanites
            boolean isMelee = isDamageSourceMelee(source);
            boolean isRanged = !isMelee && isDamageSourceRanged(source);

            EntityLivingBase target = event.getDefender();
            EntityLivingBase attacker;
            if (isMelee) attacker = (EntityLivingBase) source.getImmediateSource();
            else if (isRanged) attacker = (EntityLivingBase) source.getTrueSource();
            else return;

            //Dont trigger on self damage if that manages to happen
            if (target == attacker) return;

            //AttackingActivator handling
            if (attacker instanceof EntityPlayer) {
                handleAttacking((EntityPlayer) attacker, event, isMelee, isRanged);
            }
        }

        private static void handleAttacking(EntityPlayer player, DetermineDamageEvent event, boolean isMelee, boolean isRanged) {
            ICapabilityEffectsCache cachedEffects = player.getCapability(CapabilityEffectsCacheHandler.CAP_EFFECTS_CACHE, null);
            if (cachedEffects == null) return;

            //Handle cached effects
            for (GenericGemEffect effect : cachedEffects.getActiveEffects()) {
                if (effect instanceof DDDDamageEffect) {
                    DDDDamageEffect dddDamageEffect = (DDDDamageEffect) effect;
                    GenericActivator activator = dddDamageEffect.getActivatorType();
                    if (activator instanceof DDDAttackingActivator) {
                        DDDAttackingActivator dddAttackActivator = ((DDDAttackingActivator) activator);
                        dddAttackActivator.attemptAttackActivation(dddDamageEffect, event, isMelee, isRanged, false);
                    }
                }
            }

            //Handle direct activation effects
            //RLCombat swaps offhand into mainhand before posting LivingAttack for offhand attacks
            ItemStack weaponStack = player.getHeldItemMainhand();
            if (weaponStack.isEmpty()) return;

            ICapabilitySocketable sockets = weaponStack.getCapability(CapabilitySocketableHandler.CAP_SOCKETABLE, null);
            if (sockets == null) return;

            for (GenericGemEffect effect : sockets.getAllActiveEffects(SocketedSlotTypes.HAND)) {
                if (effect instanceof DDDDamageEffect) {
                    DDDDamageEffect dddDamageEffect = (DDDDamageEffect) effect;
                    GenericActivator activator = dddDamageEffect.getActivatorType();
                    if (activator instanceof DDDAttackingActivator) {
                        DDDAttackingActivator attackActivator = ((DDDAttackingActivator) activator);
                        attackActivator.attemptAttackActivation(dddDamageEffect, event, isMelee, isRanged, true);
                    }
                }
            }
        }

        private static boolean isDamageSourceMelee(DamageSource source) {
            return source.getImmediateSource() instanceof EntityLivingBase;
        }

        private static boolean isDamageSourceRanged(DamageSource source) {
            return !(source.getImmediateSource() instanceof EntityLivingBase) && source.getTrueSource() instanceof EntityLivingBase;
        }
    }
}