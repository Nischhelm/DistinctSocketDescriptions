package distinctsocketeddescriptions.effect;

import socketed.api.socket.gem.effect.activatable.callback.GenericEventCallback;
import socketed.api.socket.gem.effect.activatable.callback.IEffectCallback;
import socketed.api.socket.gem.effect.slot.ISlotType;
import yeelp.distinctdamagedescriptions.event.classification.GatherDefensesEvent;

import javax.annotation.Nullable;

//Same as DDDResistanceEffect just adding Immunities instead of Resistance on GatherDefensesEvent of player
public class DDDApplyImmunityEffect extends DDDEffect {
    public static final String TYPE_NAME = "DDD Apply Immunity";

    public DDDApplyImmunityEffect(ISlotType slotType, String typeName, boolean directlyActivated) {
        super(slotType, typeName, directlyActivated);
    }

    public DDDApplyImmunityEffect(DDDApplyImmunityEffect effect) {
        super(effect);
        //Instantiates amount in DDDAmountEffect constructor
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

    public DDDApplyImmunityEffect instantiate() {
        return new DDDApplyImmunityEffect(this);
    }

    @Override
    public void performEffect(@Nullable IEffectCallback callback, boolean directlyActivated) {
        if(this.directlyActivated != directlyActivated) return;
        if(!(callback instanceof GenericEventCallback)) return;
        if(!(((GenericEventCallback<?>) callback).getEvent() instanceof GatherDefensesEvent)) return;
        GatherDefensesEvent event = (GatherDefensesEvent) ((GenericEventCallback<?>) callback).getEvent();

        if(!event.hasImmunity(damageType))
            event.addImmunity(damageType);
    }
}
