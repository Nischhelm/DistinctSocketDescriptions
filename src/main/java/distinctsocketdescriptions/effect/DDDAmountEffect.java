package distinctsocketdescriptions.effect;

import com.google.gson.annotations.SerializedName;
import net.minecraft.nbt.NBTTagCompound;
import socketed.Socketed;
import socketed.api.socket.gem.effect.activatable.activator.GenericActivator;
import socketed.api.socket.gem.effect.activatable.target.GenericTarget;
import socketed.api.socket.gem.effect.slot.ISlotType;
import socketed.api.socket.gem.util.RandomValueRange;

import javax.annotation.Nullable;
import java.util.List;

public abstract class DDDAmountEffect extends DDDEffect {
    @SerializedName("Amount")
    protected final RandomValueRange amountRange;

    protected transient float amount;

    public DDDAmountEffect(ISlotType slotType, GenericActivator activator, List<GenericTarget> targets, @Nullable String tooltipKey, String damageTypeName, RandomValueRange amountRange) {
        super(slotType, activator, targets, tooltipKey, damageTypeName);
        this.amountRange = amountRange;
    }

    public DDDAmountEffect(DDDAmountEffect effect) {
        super(effect);
        this.amountRange = effect.amountRange;

        this.amount = this.amountRange.generateValue();
    }

    @Override
    public boolean validate() {
        if (super.validate()) {
            if (this.amountRange == null) Socketed.LOGGER.warn("Invalid " + this.getTypeName() + " Effect, amount range invalid");
            else return true;
        }

        return false;
    }

    @Override
    public NBTTagCompound writeToNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setFloat("Amount", this.amount);
        return nbt;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        this.amount = nbt.getFloat("Amount");
    }
}
