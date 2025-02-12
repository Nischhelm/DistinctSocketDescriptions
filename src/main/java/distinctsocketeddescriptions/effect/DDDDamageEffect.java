package distinctsocketeddescriptions.effect;

import com.google.gson.annotations.SerializedName;
import distinctsocketeddescriptions.util.DDDDamageTypeHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import socketed.Socketed;
import socketed.common.socket.gem.effect.activatable.ActivatableGemEffect;
import socketed.common.socket.gem.effect.activatable.activator.GenericActivator;
import socketed.common.socket.gem.util.RandomValueRange;
import socketed.common.socket.gem.effect.slot.ISlotType;
import yeelp.distinctdamagedescriptions.api.DDDDamageType;
import yeelp.distinctdamagedescriptions.api.impl.DDDBuiltInDamageType;
import yeelp.distinctdamagedescriptions.event.classification.DetermineDamageEvent;

public class DDDDamageEffect extends ActivatableGemEffect {
    public static final String TYPE_NAME = "DDD Damage";
    @SerializedName("DamageTypeName")
    private final String typeName;
    @SerializedName("Amount")
    private final RandomValueRange amountRange;

    private transient DDDDamageType type;
    private transient float amount;

    public DDDDamageEffect(ISlotType slotType, GenericActivator activatorType, String typeName, RandomValueRange amountRange) {
        super(slotType, activatorType);
        this.typeName = typeName;
        this.amountRange = amountRange;
    }

    public DDDDamageEffect(DDDDamageEffect effect) {
        super(effect.getSlotType(), effect.activatorType);
        this.typeName = effect.typeName;
        this.amountRange = effect.amountRange;

        this.type = DDDDamageTypeHelper.getFromString(this.typeName);
        this.amount = this.amountRange.generateValue();
    }

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
    public void performEffect(EntityPlayer entityPlayer, EntityLivingBase entityLivingBase) {
        //no op
    }

    public void performEffect(DetermineDamageEvent event) {
        float currDmg = event.getDamage(type);
        event.setDamage(type, currDmg + amount);
    }

    public boolean validate() {
        if (super.validate()) {
            if (this.typeName == null || this.typeName.isEmpty())
                Socketed.LOGGER.warn("Invalid " + this.typeName + " Effect, damage type null or empty");
            else if (this.amountRange == null)
                Socketed.LOGGER.warn("Invalid " + this.typeName + " Effect, " + this.typeName + ", amount range invalid");
            else
                return true;
        }

        return false;
    }

    public NBTTagCompound writeToNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        if (this.type != null && this.type != DDDBuiltInDamageType.UNKNOWN) {
            nbt.setString("Type", this.type.getTypeName());
            nbt.setFloat("Amount", this.amount);
        }

        return nbt;
    }

    public void readFromNBT(NBTTagCompound nbt) {
        this.type = DDDDamageTypeHelper.getFromString(nbt.getString("Type"));
        this.amount = nbt.getFloat("Amount");
    }
}
