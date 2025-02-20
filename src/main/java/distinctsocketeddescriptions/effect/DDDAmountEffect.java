package distinctsocketeddescriptions.effect;

import com.google.gson.annotations.SerializedName;
import net.minecraft.nbt.NBTTagCompound;
import socketed.Socketed;
import socketed.common.socket.gem.effect.activatable.activator.GenericActivator;
import socketed.common.socket.gem.effect.activatable.target.GenericTarget;
import socketed.common.socket.gem.effect.slot.ISlotType;
import socketed.common.socket.gem.util.RandomValueRange;

import java.util.List;

public abstract class DDDAmountEffect extends DDDEffect {
    @SerializedName("Amount")
    private final RandomValueRange amountRange;

    protected transient float amount;

    public DDDAmountEffect(ISlotType slotType, GenericActivator activatorType, List<GenericTarget> targets, String typeName, RandomValueRange amountRange) {
        super(slotType, activatorType, targets, typeName);
        this.amountRange = amountRange;
    }

    public DDDAmountEffect(DDDAmountEffect effect) {
        super(effect.getSlotType(), effect.activator, effect.targets, effect.getTypeName());
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
