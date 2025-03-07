package distinctsocketeddescriptions.effect;

import socketed.api.socket.gem.effect.activatable.callback.GenericEventCallback;
import socketed.api.socket.gem.effect.activatable.callback.IEffectCallback;
import socketed.api.socket.gem.effect.slot.ISlotType;
import yeelp.distinctdamagedescriptions.event.classification.GatherDefensesEvent;

import javax.annotation.Nullable;

public class DDDRemoveImmunityEffect extends DDDEffect {
    public static final String TYPE_NAME = "DDD Remove Immunity";

    public DDDRemoveImmunityEffect(ISlotType slotType, String typeName, boolean directlyActivated) {
        super(slotType, typeName, directlyActivated);
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

    @Override
    public void performEffect(@Nullable IEffectCallback callback, boolean directlyActivated) {
        if(this.directlyActivated != directlyActivated) return;
        if(!(callback instanceof GenericEventCallback)) return;
        if(!(((GenericEventCallback<?>) callback).getEvent() instanceof GatherDefensesEvent)) return;
        GatherDefensesEvent event = (GatherDefensesEvent) ((GenericEventCallback<?>) callback).getEvent();
        
        if(event.hasImmunity(this.damageType))
            event.removeImmunity(this.damageType);
    }
}
