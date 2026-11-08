package distinctsocketeddescriptions.effect;

import net.minecraft.client.resources.I18n;
import socketed.api.socket.gem.effect.activatable.callback.GenericEventCallback;
import socketed.api.socket.gem.effect.activatable.callback.IEffectCallback;
import socketed.api.socket.gem.effect.slot.ISlotType;
import socketed.api.socket.gem.util.RandomValueRange;
import yeelp.distinctdamagedescriptions.event.classification.GatherDefensesEvent;

import javax.annotation.Nullable;

public class DDDResistanceEffect extends DDDAmountEffect {
    public static final String TYPE_NAME = "DDD Resistance";

    public DDDResistanceEffect(ISlotType slotType, String damageTypeName, RandomValueRange amountRange, boolean directlyActivated) {
        super(slotType, damageTypeName, amountRange, directlyActivated);
    }

    public DDDResistanceEffect(DDDResistanceEffect effect) {
        super(effect);
        //Instantiates Amount in DDDAmountEffect constructor
    }

    //TODO
    @Override
    public String getTooltip(boolean b) {
        return I18n.format("dsd.tooltip.effect.resistance", this.amountRange.getTooltip(1), this.damageType.getDisplayName());
    }

    @Override
    protected Activation getDefaultActivation() {
        return Activation.ATTACKED;
    }

    @Override
    public String getTypeName() {
        return TYPE_NAME;
    }

    public DDDResistanceEffect instantiate() {
        return new DDDResistanceEffect(this);
    }

    @Override
    public void performEffect(@Nullable IEffectCallback callback, boolean directlyActivated) {
        if(this.directlyActivated != directlyActivated) return;
        if(!(callback instanceof GenericEventCallback)) return;
        if(!(((GenericEventCallback<?>) callback).getEvent() instanceof GatherDefensesEvent)) return;
        GatherDefensesEvent event = (GatherDefensesEvent) ((GenericEventCallback<?>) callback).getEvent();

        float currResistance = event.getResistance(damageType);
        event.setResistance(damageType, currResistance + amount);
    }
}
