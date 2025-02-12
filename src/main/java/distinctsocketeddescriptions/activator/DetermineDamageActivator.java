package distinctsocketeddescriptions.activator;

import distinctsocketeddescriptions.effect.DetermineDamageEffect;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import socketed.common.capabilities.effectscache.CapabilityEffectsCacheHandler;
import socketed.common.capabilities.effectscache.ICapabilityEffectsCache;
import socketed.common.socket.gem.effect.GenericGemEffect;
import socketed.common.socket.gem.effect.activatable.activator.GenericActivator;
import yeelp.distinctdamagedescriptions.event.classification.DetermineDamageEvent;

public class DetermineDamageActivator extends GenericActivator {
    public static final String TYPE_NAME = "DDD Activator";

    @Override
    public String getTooltipString() {
        return ""; //No tooltip
    }

    @Override
    public String getTypeName() {
        return TYPE_NAME;
    }

    @Override
    public boolean validate() {
        return true;
    }

    public void attemptActivation(DetermineDamageEffect dddEffect, EntityPlayer player) {

    }

    @Mod.EventBusSubscriber
    public static class EventHandler {
        @SubscribeEvent
        public static void onDDD(DetermineDamageEvent event) {
            if (!(event.getTrueAttacker() instanceof EntityPlayer)) return;
            if (event.getTrueAttacker().world.isRemote) return;

            EntityPlayer player = (EntityPlayer) event.getTrueAttacker();
            ICapabilityEffectsCache cachedEffects = player.getCapability(CapabilityEffectsCacheHandler.CAP_EFFECTS_CACHE, null);
            if (cachedEffects == null) return;

            for (GenericGemEffect effect : cachedEffects.getActiveEffects()) {
                if (!(effect instanceof DetermineDamageEffect)) continue;
                DetermineDamageEffect dddEffect = (DetermineDamageEffect) effect;
                if (!(dddEffect.getActivatorType() instanceof DetermineDamageActivator)) continue;

                DetermineDamageActivator dddActivator = (DetermineDamageActivator) dddEffect.getActivatorType();
                dddActivator.attemptActivation(dddEffect, player);
            }
        }
    }
}

