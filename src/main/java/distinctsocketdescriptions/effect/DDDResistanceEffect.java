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
import socketed.api.socket.gem.util.RandomValueRange;
import yeelp.distinctdamagedescriptions.event.classification.GatherDefensesEvent;

import javax.annotation.Nullable;
import java.util.List;

public class DDDResistanceEffect extends DDDAmountEffect {
    public static final String TYPE_NAME = "DDD Resistance";

    public DDDResistanceEffect(ISlotType slotType, GenericActivator activator, List<GenericTarget> targets, @Nullable String tooltipKey, String damageTypeName, RandomValueRange amountRange) {
        super(slotType, activator, targets, tooltipKey, damageTypeName, amountRange);
    }

    public DDDResistanceEffect(DDDResistanceEffect effect) {
        super(effect);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public String getTooltipString() {
        return I18n.format("dsd.tooltip.effect.resistance", this.amountRange.getTooltip(1), this.damageType.getDisplayName());
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
    public DDDResistanceEffect instantiate() {
        return new DDDResistanceEffect(this);
    }

    @Override
    public void performEffect(@Nullable IEffectCallback callback, EntityPlayer playerSource, EntityLivingBase effectTarget) {
        GatherDefensesEvent event = getDefensesEvent(callback, effectTarget);
        if(event == null) return;
        event.setResistance(this.damageType, event.getResistance(this.damageType) + this.amount);
    }
}
