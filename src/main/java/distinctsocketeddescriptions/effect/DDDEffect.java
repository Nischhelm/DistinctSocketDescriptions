package distinctsocketeddescriptions.effect;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import socketed.common.capabilities.effectscache.CapabilityEffectsCacheHandler;
import socketed.common.capabilities.effectscache.ICapabilityEffectsCache;
import socketed.common.socket.gem.effect.GenericGemEffect;
import socketed.common.socket.gem.effect.activatable.activator.GenericActivator;
import socketed.common.socket.gem.effect.slot.ISlotType;
import yeelp.distinctdamagedescriptions.event.classification.DetermineDamageEvent;

public class DDDEffect extends GenericGemEffect {
    public static final String TYPE_NAME = "DDD Damage";

    protected DDDEffect(ISlotType slotType, GenericActivator activatorType) {
        super(slotType);
    }

    @Override
    public String getTooltipString(boolean b) {
        return "";
    }

    @Override
    public String getTypeName() {
        return TYPE_NAME;
    }

    public void performEffect(DetermineDamageEvent event) {

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
                if (!(effect instanceof DDDEffect)) continue;
                ((DDDEffect) effect).performEffect(event);
            }
        }
    }
}
