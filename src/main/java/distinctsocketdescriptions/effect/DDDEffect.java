package distinctsocketdescriptions.effect;

import com.google.gson.annotations.SerializedName;
import distinctsocketdescriptions.effect.activator.DDDActivator;
import distinctsocketdescriptions.effect.activator.DDDAttackedActivator;
import distinctsocketdescriptions.effect.activator.DDDAttackingActivator;
import distinctsocketdescriptions.effect.callback.DDDEventCallback;
import net.minecraft.entity.EntityLivingBase;
import socketed.Socketed;
import socketed.api.socket.gem.effect.activatable.ActivatableGemEffect;
import socketed.api.socket.gem.effect.activatable.activator.GenericActivator;
import socketed.api.socket.gem.effect.activatable.callback.IEffectCallback;
import socketed.api.socket.gem.effect.activatable.target.GenericTarget;
import socketed.api.socket.gem.effect.slot.ISlotType;
import socketed.common.socket.gem.effect.activatable.target.OtherTarget;
import socketed.common.socket.gem.effect.activatable.target.SelfTarget;
import yeelp.distinctdamagedescriptions.api.DDDDamageType;
import yeelp.distinctdamagedescriptions.event.classification.GatherDefensesEvent;
import yeelp.distinctdamagedescriptions.registries.DDDRegistries;

import javax.annotation.Nullable;
import java.util.List;

public abstract class DDDEffect extends ActivatableGemEffect {
    @SerializedName("Damage Type")
    private final String damageTypeName;

    protected transient DDDDamageType damageType;

    public DDDEffect(ISlotType slotType, GenericActivator activator, List<GenericTarget> targets, @Nullable String tooltipKey, String damageTypeName) {
        super(slotType, activator, targets, tooltipKey);
        this.damageTypeName = damageTypeName;
    }

    public DDDEffect(DDDEffect effect) {
        super(effect.slotType, effect.activator, effect.targets, effect.tooltipKey);
        this.damageTypeName = effect.damageTypeName;
        this.damageType = effect.damageType;
    }

    /**
     * @return true if this effect modifies the damage of the attacker, false if it modifies the defenses of the defender
     */
    protected abstract boolean affectsAttacker();

    /**
     * @return the GatherDefensesEvent of the callback if effectTarget is its defender, otherwise null
     */
    @Nullable
    protected static GatherDefensesEvent getDefensesEvent(@Nullable IEffectCallback callback, EntityLivingBase effectTarget) {
        if(!(callback instanceof DDDEventCallback)) return null;
        if(!(((DDDEventCallback<?>) callback).getEvent() instanceof GatherDefensesEvent)) return null;
        GatherDefensesEvent event = (GatherDefensesEvent) ((DDDEventCallback<?>) callback).getEvent();
        return event.getDefender() == effectTarget ? event : null;
    }

    /**
     * Activator: Required, DDD Attacking or DDD Attacked
     * Damage Type: Required, any registered DDD damage type (for example ddd_fire)
     */
    @Override
    public boolean validate() {
        if(super.validate()) {
            if(!(this.activator instanceof DDDActivator)) Socketed.LOGGER.warn("Invalid " + this.getTypeName() + " Effect, activator must be " + DDDAttackingActivator.TYPE_NAME + " or " + DDDAttackedActivator.TYPE_NAME);
            else if(this.damageTypeName == null || this.damageTypeName.isEmpty()) Socketed.LOGGER.warn("Invalid " + this.getTypeName() + " Effect, damage type null or empty");
            else {
                this.damageType = DDDRegistries.damageTypes.get(this.damageTypeName);
                if(this.damageType == null) Socketed.LOGGER.warn("Invalid " + this.getTypeName() + " Effect, damage type " + this.damageTypeName + " does not exist");
                else {
                    this.warnUnaffectedTargets();
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Only the attacker or the defender of the damage event can be affected, depending on activator and effect
     */
    private void warnUnaffectedTargets() {
        boolean affectsSelf = (this.activator instanceof DDDAttackingActivator) == this.affectsAttacker();
        String expected = affectsSelf ? SelfTarget.TYPE_NAME : OtherTarget.TYPE_NAME;
        for(GenericTarget target : this.targets) {
            boolean valid = affectsSelf ? target instanceof SelfTarget : target instanceof OtherTarget;
            if(!valid) Socketed.LOGGER.warn("Possible invalid " + this.getTypeName() + " Effect, " + target.getTypeName() + " target never gets affected with the " + this.activator.getTypeName() + " activator, use " + expected);
        }
    }
}
