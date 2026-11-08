package distinctsocketeddescriptions.effect;

import com.google.gson.annotations.SerializedName;
import socketed.Socketed;
import socketed.api.socket.gem.effect.GenericGemEffect;
import socketed.api.socket.gem.effect.activatable.callback.IEffectCallback;
import socketed.api.socket.gem.effect.slot.ISlotType;
import socketed.api.socket.gem.effect.slot.SocketedSlotTypes;
import yeelp.distinctdamagedescriptions.api.DDDDamageType;
import yeelp.distinctdamagedescriptions.registries.DDDRegistries;

import javax.annotation.Nullable;

public abstract class DDDEffect extends GenericGemEffect {
    @SerializedName("Damage Type")
    private final String damageTypeName;
    @SerializedName("Directly Activated")
    protected Boolean directlyActivated;
    @SerializedName("Activation")
    protected Activation activation;

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
        this.activation = effect.activation;
    }

    /**
     * ATTACKING: applies when the player attacks, to the players damage or to the targets defenses
     * ATTACKED: applies when the player gets attacked, to the attackers damage or to the players defenses
     */
    public enum Activation {
        @SerializedName("ATTACKING") ATTACKING,
        @SerializedName("ATTACKED") ATTACKED
    }

    /**
     * Activation if set, otherwise depending on the slot type: hand slots are ATTACKING, body slots are ATTACKED.
     * Any other slot type (for example ALL) uses the default of the effect
     */
    public Activation getActivation() {
        if (this.activation != null) return this.activation;
        if (this.slotType == SocketedSlotTypes.HAND || this.slotType == SocketedSlotTypes.MAINHAND || this.slotType == SocketedSlotTypes.OFFHAND)
            return Activation.ATTACKING;
        if (this.slotType == SocketedSlotTypes.BODY || this.slotType == SocketedSlotTypes.HEAD || this.slotType == SocketedSlotTypes.CHEST || this.slotType == SocketedSlotTypes.LEGS || this.slotType == SocketedSlotTypes.FEET)
            return Activation.ATTACKED;
        return this.getDefaultActivation();
    }

    public boolean isDirectlyActivated() {
        return this.directlyActivated;
    }

    /**
     * Activation used if neither Activation is set nor the slot type implies one
     */
    protected abstract Activation getDefaultActivation();

    public abstract void performEffect(@Nullable IEffectCallback callback, boolean directlyActivated);

    /**
     * Damage Type: required, any registered DDD damage type (for example ddd_fire)
     * Directly Activated: optional, default false
     * Activation: optional, ATTACKING or ATTACKED, default depends on slot type, see getActivation
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
