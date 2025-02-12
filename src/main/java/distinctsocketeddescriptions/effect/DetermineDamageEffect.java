package distinctsocketeddescriptions.effect;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import socketed.common.socket.gem.effect.activatable.ActivatableGemEffect;
import socketed.common.socket.gem.effect.activatable.activator.GenericActivator;
import socketed.common.socket.gem.effect.slot.ISlotType;

public class DetermineDamageEffect extends ActivatableGemEffect {
    public static final String TYPE_NAME = "DDD Damage";

    protected DetermineDamageEffect(ISlotType slotType, GenericActivator activatorType) {
        super(slotType, activatorType);
    }

    @Override
    public String getTooltipString(boolean b) {
        return "";
    }

    @Override
    public String getTypeName() {
        return TYPE_NAME;
    }

    @Override
    public void performEffect(EntityPlayer entityPlayer, EntityLivingBase entityLivingBase) {

    }
}
