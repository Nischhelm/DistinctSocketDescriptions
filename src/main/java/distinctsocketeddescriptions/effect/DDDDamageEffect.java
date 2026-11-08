package distinctsocketeddescriptions.effect;

import net.minecraft.client.resources.I18n;
import socketed.Socketed;
import socketed.api.socket.gem.effect.activatable.callback.GenericEventCallback;
import socketed.api.socket.gem.effect.activatable.callback.IEffectCallback;
import socketed.api.socket.gem.effect.slot.ISlotType;
import socketed.api.socket.gem.util.RandomValueRange;
import yeelp.distinctdamagedescriptions.event.classification.DetermineDamageEvent;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class DDDDamageEffect extends DDDAmountEffect {
    public static final String TYPE_NAME = "DDD Damage";

    public DDDDamageEffect(ISlotType slotType, String typeName, RandomValueRange amountRange, boolean directlyActivated) {
        super(slotType, typeName, amountRange, directlyActivated);
    }

    public DDDDamageEffect(DDDDamageEffect effect) {
        super(effect);
        //Instantiates amount in DDDAmountEffect constructor
    }

    @Override
    public String getTooltip(boolean b) {
        return I18n.format("dsd.tooltip.effect.damage", this.amountRange.getTooltip(0), this.damageType.getDisplayName());
    }

    @Override
    protected Activation getDefaultActivation() {
        return Activation.ATTACKING;
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
        applyAll((DetermineDamageEvent) ((GenericEventCallback<?>) callback).getEvent(), Collections.singletonList(this));
    }

    /**
     * Applies all damage effects active for one DetermineDamageEvent together, the caller has to check activation and direct activation
     */
    public static void applyAll(DetermineDamageEvent event, List<DDDDamageEffect> effects) {
        for(DDDDamageEffect effect : effects) {
            float currDmg = event.getDamage(effect.damageType);
            //DDD throws on negative damage, negative amounts can only reduce the damage of a type to 0
            event.setDamage(effect.damageType, Math.max(0, currDmg + effect.amount));
        }
    }
}
