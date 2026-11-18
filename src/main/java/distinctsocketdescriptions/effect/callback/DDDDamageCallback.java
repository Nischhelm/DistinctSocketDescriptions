package distinctsocketdescriptions.effect.callback;

import distinctsocketdescriptions.effect.DDDDamageEffect;
import net.minecraft.entity.EntityLivingBase;
import yeelp.distinctdamagedescriptions.event.classification.DetermineDamageEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Collects all damage effects activated in one DetermineDamageEvent, so they can be applied together
 */
public class DDDDamageCallback extends DDDEventCallback<DetermineDamageEvent> {
    private final List<DDDDamageEffect> damageEffects = new ArrayList<>();

    public DDDDamageCallback(DetermineDamageEvent event, @Nullable EntityLivingBase attacker) {
        super(event, attacker);
    }

    public void addDamageEffect(DDDDamageEffect effect) {
        this.damageEffects.add(effect);
    }

    public List<DDDDamageEffect> getDamageEffects() {
        return this.damageEffects;
    }
}
