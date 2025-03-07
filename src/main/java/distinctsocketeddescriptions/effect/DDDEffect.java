package distinctsocketeddescriptions.effect;

import com.google.gson.annotations.SerializedName;
import socketed.Socketed;
import socketed.api.socket.gem.effect.GenericGemEffect;
import socketed.api.socket.gem.effect.activatable.callback.IEffectCallback;
import socketed.api.socket.gem.effect.slot.ISlotType;
import yeelp.distinctdamagedescriptions.api.DDDDamageType;
import yeelp.distinctdamagedescriptions.registries.DDDRegistries;

import javax.annotation.Nullable;

public abstract class DDDEffect extends GenericGemEffect {
    @SerializedName("Damage Type")
    private final String damageTypeName;
    @SerializedName("Directly Activated")
    protected Boolean directlyActivated;

    protected transient DDDDamageType damageType;

    public DDDEffect(ISlotType slotType, String damageTypeName, boolean directlyActivated) {
        super(slotType);
        this.damageTypeName = damageTypeName;
        this.directlyActivated = directlyActivated;
    }

    public DDDEffect(DDDEffect effect) {
        super(effect.slotType);
        this.damageTypeName = effect.damageTypeName;
        //Mainly need to copy dmg type object
        this.damageType = effect.damageType;
        this.directlyActivated = effect.directlyActivated;
    }

    public abstract void performEffect(@Nullable IEffectCallback callback, boolean directlyActivated);

    /**
     * Damage Type: required, any registered DDD damage type (for example ddd_fire)
     * Directly Activated: optional, default false
     */
    public boolean validate() {
        if (super.validate()) {
            if(this.directlyActivated == null) this.directlyActivated = false;

            if (this.damageTypeName == null || this.damageTypeName.isEmpty()) Socketed.LOGGER.warn("Invalid " + this.getTypeName() + " Effect, damage type null or empty");
            else{
                this.damageType = DDDRegistries.damageTypes.get(this.damageTypeName);
                if(this.damageType == null) Socketed.LOGGER.warn("Invalid " + this.getTypeName() + " Effect, damage type, " +this.damageTypeName+" does not exist");
                else return true;
            }
        }

        return false;
    }
}
