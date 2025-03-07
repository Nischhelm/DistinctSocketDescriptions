package distinctsocketeddescriptions.effect;

import socketed.api.socket.gem.effect.activatable.callback.GenericEventCallback;
import socketed.api.socket.gem.effect.activatable.callback.IEffectCallback;
import socketed.api.socket.gem.effect.slot.ISlotType;
import socketed.api.socket.gem.util.RandomValueRange;
import yeelp.distinctdamagedescriptions.event.classification.DetermineDamageEvent;

import javax.annotation.Nullable;

public class DDDDamageEffect extends DDDAmountEffect {
    public static final String TYPE_NAME = "DDD Damage";

    public DDDDamageEffect(ISlotType slotType, String typeName, RandomValueRange amountRange, boolean directlyActivated) {
        super(slotType, typeName, amountRange, directlyActivated);
    }

    public DDDDamageEffect(DDDDamageEffect effect) {
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

    public DDDDamageEffect instantiate() {
        return new DDDDamageEffect(this);
    }

    @Override
    public void performEffect(@Nullable IEffectCallback callback, boolean directlyActivated) {
        if(this.directlyActivated != directlyActivated) return;
        if(!(callback instanceof GenericEventCallback)) return;
        if(!(((GenericEventCallback<?>) callback).getEvent() instanceof DetermineDamageEvent)) return;
        DetermineDamageEvent event = (DetermineDamageEvent) ((GenericEventCallback<?>) callback).getEvent();
        
        float currDmg = event.getDamage(damageType);
        event.setDamage(damageType, currDmg + amount);
    }
}
