package distinctsocketeddescriptions.effect;

import com.google.gson.annotations.SerializedName;
import distinctsocketeddescriptions.util.DDDDamageTypeHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import socketed.Socketed;
import socketed.common.socket.gem.util.RandomValueRange;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import socketed.common.capabilities.effectscache.CapabilityEffectsCacheHandler;
import socketed.common.capabilities.effectscache.ICapabilityEffectsCache;
import socketed.common.socket.gem.effect.GenericGemEffect;
import socketed.common.socket.gem.effect.slot.ISlotType;
import yeelp.distinctdamagedescriptions.api.DDDDamageType;
import yeelp.distinctdamagedescriptions.api.impl.DDDBuiltInDamageType;
import yeelp.distinctdamagedescriptions.event.classification.DetermineDamageEvent;

public class DDDEffect extends GenericGemEffect {
    public static final String TYPE_NAME = "DDD Damage";
    @SerializedName("DamageTypeName")
    private final String typeName;
    @SerializedName("Amount")
    private final RandomValueRange amountRange;

    private DDDDamageType type;
    private float amount;

    public DDDEffect(ISlotType slotType, String typeName, RandomValueRange amountRange) {
        super(slotType);
        this.typeName = typeName;
        this.amountRange = amountRange;
    }

    public DDDEffect(DDDEffect effect) {
        super(effect.getSlotType());
        this.typeName = effect.typeName;
        this.type = DDDDamageTypeHelper.getFromString(this.typeName);
        this.amountRange = effect.amountRange;
        this.amount = this.amountRange.generateValue();
    }

    @Override
    public String getTooltipString(boolean b) {
        return "Test";
    }

    @Override
    public String getTypeName() {
        return TYPE_NAME;
    }

    public DDDEffect instantiate() {
        return new DDDEffect(this);
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
            nbt.setDouble("Amount", this.amount);
        }

        return nbt;
    }

    public void readFromNBT(NBTTagCompound nbt) {
        this.type = DDDDamageTypeHelper.getFromString(nbt.getString("Type"));
        this.amount = nbt.getFloat("Amount");
    }

    public void performEffect(DetermineDamageEvent event) {
        float currDmg = event.getDamage(type);
        event.setDamage(type, currDmg + amount);
    }

    @Mod.EventBusSubscriber
    public static class EventHandler {
        @SubscribeEvent
        public static void onDDD(DetermineDamageEvent event) {
            if (!(event.getTrueAttacker() instanceof EntityPlayer)) return;
            if (event.getTrueAttacker().world.isRemote) return;

            EntityPlayer player = (EntityPlayer) event.getTrueAttacker();
            ICapabilityEffectsCache cachedEffects = player.getCapability(CapabilityEffectsCacheHandler.CAP_EFFECTS_CACHE, null);
            if (cachedEffects == null) return;

            for (GenericGemEffect effect : cachedEffects.getActiveEffects()) {
                if (!(effect instanceof DDDEffect)) continue;
                ((DDDEffect) effect).performEffect(event);
            }
        }
    }
}
