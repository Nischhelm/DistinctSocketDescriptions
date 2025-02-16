package distinctsocketeddescriptions.effect;

import com.google.gson.annotations.SerializedName;
import distinctsocketeddescriptions.util.DDDDamageTypeHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import socketed.Socketed;
import socketed.common.socket.gem.effect.activatable.ActivatableGemEffect;
import socketed.common.socket.gem.effect.activatable.activator.GenericActivator;
import socketed.common.socket.gem.effect.slot.ISlotType;
import socketed.common.socket.gem.util.RandomValueRange;
import yeelp.distinctdamagedescriptions.api.DDDDamageType;
import yeelp.distinctdamagedescriptions.api.impl.DDDBuiltInDamageType;
import yeelp.distinctdamagedescriptions.event.classification.GatherDefensesEvent;

public class DDDResistanceEffect extends ActivatableGemEffect {
    public static final String TYPE_NAME = "DDD Resistance";
    @SerializedName("DamageTypeName")
    protected final String typeName;
    @SerializedName("Amount")
    protected final RandomValueRange amountRange;

    protected transient DDDDamageType type;
    protected transient float amount;

    public DDDResistanceEffect(ISlotType slotType, GenericActivator activatorType, String typeName, RandomValueRange amountRange) {
        super(slotType, activatorType);
        this.typeName = typeName;
        this.amountRange = amountRange;
    }

    public DDDResistanceEffect(DDDResistanceEffect effect) {
        super(effect.getSlotType(), effect.activatorType);
        this.typeName = effect.typeName;
        this.amountRange = effect.amountRange;

        this.type = DDDDamageTypeHelper.getFromString(this.typeName);
        this.amount = this.amountRange.generateValue();
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

    public DDDResistanceEffect instantiate() {
        return new DDDResistanceEffect(this);
    }

    @Override
    public void performEffect(EntityPlayer entityPlayer, EntityLivingBase entityLivingBase) {
        //no op
    }

    public void performEffect(GatherDefensesEvent event) {
        float currResistance = event.getResistance(type);
        event.setResistance(type, currResistance + amount);
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
