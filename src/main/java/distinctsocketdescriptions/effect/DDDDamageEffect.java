package distinctsocketdescriptions.effect;

import com.google.gson.annotations.SerializedName;
import distinctsocketdescriptions.effect.activator.DDDActivator;
import distinctsocketdescriptions.effect.activator.DDDAttackedActivator;
import distinctsocketdescriptions.effect.callback.DDDDamageCallback;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import socketed.Socketed;
import socketed.api.socket.gem.effect.activatable.activator.GenericActivator;
import socketed.api.socket.gem.effect.activatable.callback.IEffectCallback;
import socketed.api.socket.gem.effect.activatable.target.GenericTarget;
import socketed.api.socket.gem.effect.slot.ISlotType;
import socketed.api.socket.gem.util.RandomValueRange;
import yeelp.distinctdamagedescriptions.api.DDDDamageType;
import yeelp.distinctdamagedescriptions.event.classification.DetermineDamageEvent;
import yeelp.distinctdamagedescriptions.registries.DDDRegistries;

import javax.annotation.Nullable;
import java.util.*;

public class DDDDamageEffect extends DDDAmountEffect {
    public static final String TYPE_NAME = "DDD Damage";

    @SerializedName("Operation")
    protected Operation operation;

    public DDDDamageEffect(ISlotType slotType, GenericActivator activator, List<GenericTarget> targets, @Nullable String tooltipKey, String damageTypeName, RandomValueRange amountRange, Operation operation) {
        super(slotType, activator, targets, tooltipKey, damageTypeName, amountRange);
        this.operation = operation;
    }

    public DDDDamageEffect(DDDDamageEffect effect) {
        super(effect);
        this.operation = effect.operation;
    }

    /**
     * Applied in this order: CONVERT, then ADD and MULTIPLY_EXTRA, then MULTIPLY_TYPE
     * ADD: adds Amount to the damage of the type
     * MULTIPLY_EXTRA: adds Amount times the total damage as damage of the type
     * CONVERT: turns Amount of the damage of all other types into the type
     * MULTIPLY_TYPE: multiplies the damage of the type by Amount
     */
    public enum Operation {
        @SerializedName("ADD") ADD,
        @SerializedName("MULTIPLY_EXTRA") MULTIPLY_EXTRA,
        @SerializedName("CONVERT") CONVERT,
        @SerializedName("MULTIPLY_TYPE") MULTIPLY_TYPE
    }

    public Operation getOperation() {
        return this.operation;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public String getTooltipString() {
        String typeName = this.damageType.getDisplayName();
        switch (this.operation) {
            case MULTIPLY_EXTRA: return I18n.format("dsd.tooltip.effect.damage.multiply_extra", this.amountRange.getTooltip(1), typeName);
            case CONVERT: return I18n.format("dsd.tooltip.effect.damage.convert", formatRange(100), typeName);
            case MULTIPLY_TYPE: return I18n.format("dsd.tooltip.effect.damage.multiply_type", formatRange(1), typeName);
            default: return I18n.format("dsd.tooltip.effect.damage", this.amountRange.getTooltip(0), typeName);
        }
    }

    private String formatRange(float scale) {
        String min = ItemStack.DECIMALFORMAT.format(this.amountRange.getMin() * scale);
        if (this.amountRange.getMin() == this.amountRange.getMax()) return min;
        return min + "-" + ItemStack.DECIMALFORMAT.format(this.amountRange.getMax() * scale);
    }

    @Override
    protected boolean affectsAttacker() {
        return true;
    }

    @Override
    public String getTypeName() {
        return TYPE_NAME;
    }

    @Override
    public DDDDamageEffect instantiate() {
        return new DDDDamageEffect(this);
    }

    /**
     * Operation: optional, default ADD
     * CONVERT and MULTIPLY_TYPE don't allow negative amounts
     */
    @Override
    public boolean validate() {
        if (super.validate()) {
            if (this.operation == null) this.operation = Operation.ADD;
            if ((this.operation == Operation.CONVERT || this.operation == Operation.MULTIPLY_TYPE) && this.amountRange.getMin() < 0)
                Socketed.LOGGER.warn("Invalid " + this.getTypeName() + " Effect, " + this.operation + " doesn't allow negative amounts");
            else {
                if (this.activator instanceof DDDAttackedActivator && ((DDDActivator) this.activator).isDirectlyActivated())
                    Socketed.LOGGER.warn("Possible invalid " + this.getTypeName() + " Effect, directly activated " + DDDAttackedActivator.TYPE_NAME + " damage effects never activate, since the damage is determined before the hit armor is known");
                return true;
            }
        }
        return false;
    }

    /**
     * Only collects this effect if effectTarget is the attacker, all collected effects get applied together via applyAll
     */
    @Override
    public void performEffect(@Nullable IEffectCallback callback, EntityPlayer playerSource, EntityLivingBase effectTarget) {
        if(!(callback instanceof DDDDamageCallback)) return;
        DDDDamageCallback damageCallback = (DDDDamageCallback) callback;
        if(damageCallback.getAttacker() == effectTarget) damageCallback.addDamageEffect(this);
    }

    /**
     * Applies all damage effects active for one DetermineDamageEvent together
     * Order of operations is CONVERT, then ADD and MULTIPLY_EXTRA, then MULTIPLY_TYPE, so the result doesn't depend on the order of the effects
     */
    public static void applyAll(DetermineDamageEvent event, List<DDDDamageEffect> effects) {
        Map<DDDDamageType, Float> damages = new HashMap<>();
        for (DDDDamageType type : DDDRegistries.damageTypes) {
            float dmg = event.getDamage(type);
            if (dmg > 0) damages.put(type, dmg);
        }
        Map<DDDDamageType, Float> original = new HashMap<>(damages);

        Map<DDDDamageType, Float> converts = new HashMap<>();
        Map<DDDDamageType, Float> multipliers = new HashMap<>();
        for (DDDDamageEffect effect : effects) {
            if (effect.operation == Operation.CONVERT) converts.merge(effect.damageType, effect.amount, Float::sum);
            //Multipliers of the same type add up, x1.5 and x1.5 is x2
            else if (effect.operation == Operation.MULTIPLY_TYPE) multipliers.merge(effect.damageType, effect.amount - 1, Float::sum);
        }

        //CONVERT: every source type gives up the summed convert percentage of all other target types from its damage before conversion
        //If that is more than 100%, all targets get scaled down proportionally, so a type can't give up more than all its damage
        if (!converts.isEmpty()) {
            Map<DDDDamageType, Float> preConversion = new HashMap<>(damages);
            preConversion.forEach((source, sourceDmg) -> {
                float total = 0;
                for (Map.Entry<DDDDamageType, Float> convert : converts.entrySet())
                    if (convert.getKey() != source) total += convert.getValue();
                if (total <= 0) return;
                float scale = total > 1 ? 1 / total : 1;
                for (Map.Entry<DDDDamageType, Float> convert : converts.entrySet()) {
                    if (convert.getKey() == source) continue;
                    float moved = sourceDmg * convert.getValue() * scale;
                    damages.merge(source, -moved, Float::sum);
                    damages.merge(convert.getKey(), moved, Float::sum);
                }
            });
        }

        //ADD and MULTIPLY_EXTRA: MULTIPLY_EXTRA uses the total damage before any ADD or MULTIPLY_EXTRA, so multiple MULTIPLY_EXTRA effects don't feed into each other
        float totalDmg = 0;
        for (float dmg : damages.values()) totalDmg += dmg;
        for (DDDDamageEffect effect : effects) {
            if (effect.operation == Operation.ADD) damages.merge(effect.damageType, effect.amount, Float::sum);
            else if (effect.operation == Operation.MULTIPLY_EXTRA) damages.merge(effect.damageType, totalDmg * effect.amount, Float::sum);
        }
        //Negative amounts can only reduce the damage of a type to 0
        damages.replaceAll((type, dmg) -> Math.max(0, dmg));

        //MULTIPLY_TYPE
        multipliers.forEach((type, multiplier) -> damages.computeIfPresent(type, (t, dmg) -> Math.max(0, dmg * (1 + multiplier))));

        //DDD throws on negative damage, only write back types that changed, don't add new types with 0 damage to the map
        damages.forEach((type, dmg) -> {
            float originalDmg = original.getOrDefault(type, 0F);
            if (dmg != originalDmg) event.setDamage(type, Math.max(0, dmg));
        });
    }
}
