package distinctsocketeddescriptions.effect;

import com.google.gson.annotations.SerializedName;
import net.minecraft.nbt.NBTTagCompound;
import socketed.Socketed;
import socketed.api.socket.gem.effect.slot.ISlotType;
import socketed.api.socket.gem.util.RandomValueRange;

public abstract class DDDAmountEffect extends DDDEffect {
    @SerializedName("Amount")
    private final RandomValueRange amountRange;

    protected transient float amount;

    public DDDAmountEffect(ISlotType slotType, String typeName, RandomValueRange amountRange, boolean directlyActivated) {
        super(slotType, typeName, directlyActivated);
        this.amountRange = amountRange;
    }

    public DDDAmountEffect(DDDAmountEffect effect) {
        super(effect);
        this.amountRange = effect.amountRange;

        this.amount = this.amountRange.generateValue();
    }

    public boolean validate() {
        if (super.validate()) {
            if (this.amountRange == null) Socketed.LOGGER.warn("Invalid " + this.getTypeName() + " Effect, amount range invalid");
            else return true;
        }

        return false;
    }

    public NBTTagCompound writeToNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setFloat("Amount", this.amount);
        return nbt;
    }

    public void readFromNBT(NBTTagCompound nbt) {
        this.amount = nbt.getFloat("Amount");
    }
}
