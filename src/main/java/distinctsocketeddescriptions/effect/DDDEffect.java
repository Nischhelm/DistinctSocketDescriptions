package distinctsocketeddescriptions.effect;

import com.google.gson.annotations.SerializedName;
import socketed.Socketed;
import socketed.common.socket.gem.effect.activatable.ActivatableGemEffect;
import socketed.common.socket.gem.effect.activatable.activator.GenericActivator;
import socketed.common.socket.gem.effect.activatable.target.GenericTarget;
import socketed.common.socket.gem.effect.slot.ISlotType;
import yeelp.distinctdamagedescriptions.api.DDDDamageType;
import yeelp.distinctdamagedescriptions.registries.DDDRegistries;

import java.util.List;

public abstract class DDDEffect extends ActivatableGemEffect {
    @SerializedName("Damage Type")
    private final String damageTypeName;

    protected transient DDDDamageType damageType;

    public DDDEffect(ISlotType slotType, GenericActivator activatorType, List<GenericTarget> targets, String damageTypeName) {
        super(slotType, activatorType, targets);
        this.damageTypeName = damageTypeName;
    }

    public boolean validate() {
        if (super.validate()) {
            if (this.damageTypeName == null || this.damageTypeName.isEmpty())
                Socketed.LOGGER.warn("Invalid " + this.getTypeName() + " Effect, damage type null or empty");
            else {
                this.damageType = DDDRegistries.damageTypes.get(this.damageTypeName);
                if(this.damageType == null) Socketed.LOGGER.warn("Invalid " + this.getTypeName() + " Effect, damage type, " +this.damageTypeName+" does not exist");
                else return true;
            }
        }

        return false;
    }
}
