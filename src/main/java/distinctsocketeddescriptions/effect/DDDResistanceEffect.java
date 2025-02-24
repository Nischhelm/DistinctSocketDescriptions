package distinctsocketeddescriptions.effect;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import socketed.common.socket.gem.effect.activatable.activator.GenericActivator;
import socketed.common.socket.gem.effect.activatable.callback.GenericEventCallback;
import socketed.common.socket.gem.effect.activatable.callback.IEffectCallback;
import socketed.common.socket.gem.effect.activatable.target.GenericTarget;
import socketed.common.socket.gem.effect.slot.ISlotType;
import socketed.common.socket.gem.util.RandomValueRange;
import yeelp.distinctdamagedescriptions.event.classification.GatherDefensesEvent;

import javax.annotation.Nullable;
import java.util.List;

public class DDDResistanceEffect extends DDDAmountEffect {
    public static final String TYPE_NAME = "DDD Resistance";

    public DDDResistanceEffect(ISlotType slotType, GenericActivator activatorType, List<GenericTarget> targets, String damageTypeName, RandomValueRange amountRange) {
        super(slotType, activatorType, targets, damageTypeName, amountRange);
    }

    public DDDResistanceEffect(DDDResistanceEffect effect) {
        super(effect);
        //Instantiates Amount in DDDAmountEffect constructor
    }

    //TODO
    @Override
    public String getTooltipString(boolean b) {
        return "";
    }

    @Override
    public String getTypeName() {
        return TYPE_NAME;
    }

    public DDDResistanceEffect instantiate() {
        return new DDDResistanceEffect(this);
    }

    @Override
    public void performEffect(@Nullable IEffectCallback callback, EntityPlayer entityPlayer, EntityLivingBase effectTarget) {
        if(!(callback instanceof GenericEventCallback)) return;
        if(!(((GenericEventCallback<?>) callback).getEvent() instanceof GatherDefensesEvent)) return;
        GatherDefensesEvent event = (GatherDefensesEvent) ((GenericEventCallback<?>) callback).getEvent();

        float currResistance = event.getResistance(damageType);
        event.setResistance(damageType, currResistance + amount);
    }
}
