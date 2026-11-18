package distinctsocketdescriptions.effect;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import socketed.api.socket.gem.effect.activatable.activator.GenericActivator;
import socketed.api.socket.gem.effect.activatable.callback.IEffectCallback;
import socketed.api.socket.gem.effect.activatable.target.GenericTarget;
import socketed.api.socket.gem.effect.slot.ISlotType;
import yeelp.distinctdamagedescriptions.event.classification.GatherDefensesEvent;

import javax.annotation.Nullable;
import java.util.List;

public class DDDApplyImmunityEffect extends DDDEffect {
    public static final String TYPE_NAME = "DDD Apply Immunity";

    public DDDApplyImmunityEffect(ISlotType slotType, GenericActivator activator, List<GenericTarget> targets, @Nullable String tooltipKey, String damageTypeName) {
        super(slotType, activator, targets, tooltipKey, damageTypeName);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public String getTooltipString() {
        return I18n.format("dsd.tooltip.effect.immunity", this.damageType.getDisplayName());
    }

    @Override
    protected boolean affectsAttacker() {
        return false;
    }

    @Override
    public String getTypeName() {
        return TYPE_NAME;
    }

    @Override
    public void performEffect(@Nullable IEffectCallback callback, EntityPlayer playerSource, EntityLivingBase effectTarget) {
        GatherDefensesEvent event = getDefensesEvent(callback, effectTarget);
        if(event == null) return;
        if(!event.hasImmunity(this.damageType)) event.addImmunity(this.damageType);
    }
}
