package distinctsocketeddescriptions.mixin.firstaid;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import distinctsocketeddescriptions.compat.FirstAidCompat;
import ichttt.mods.firstaid.common.damagesystem.distribution.DamageDistribution;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.EnumSet;
import java.util.Set;

@Mixin(DamageDistribution.class)
public class DamageDistributionMixin {
    /**
     * Each distribution round fires its own LivingDamageEvent for exactly one armor slot
     */
    @WrapOperation(
            method = "distributeDamage",
            at = @At(value = "INVOKE", target = "Lnet/minecraftforge/common/ForgeHooks;onLivingDamage(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/util/DamageSource;F)F"),
            remap = false
    )
    private float dsd_trackHitSlot(EntityLivingBase entity, DamageSource source, float amount, Operation<Float> original, @Local EntityEquipmentSlot slot){
        Set<EntityEquipmentSlot> previous = FirstAidCompat.setHitSlots(entity, EnumSet.of(slot));
        try {
            return original.call(entity, source, amount);
        } finally {
            FirstAidCompat.restoreHitSlots(entity, previous);
        }
    }
}
