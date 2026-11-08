package distinctsocketeddescriptions.mixin.firstaid;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import distinctsocketeddescriptions.compat.FirstAidCompat;
import ichttt.mods.firstaid.common.damagesystem.distribution.EqualDamageDistribution;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.EnumSet;
import java.util.Set;

@Mixin(EqualDamageDistribution.class)
public class EqualDamageDistributionMixin {
    /**
     * We have to assume that all body parts get damaged here because the actual calculation which body parts are affected will only happen AFTER LivingDamage,
     * but we need the info before, specifically in LivingDamage -> GatherDefenses (DDD)
     */
    @WrapOperation(
            method = "reduceDamage",
            at = @At(value = "INVOKE", target = "Lnet/minecraftforge/common/ForgeHooks;onLivingDamage(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/util/DamageSource;F)F"),
            remap = false
    )
    private float dsd_trackHitSlots(EntityLivingBase entity, DamageSource source, float amount, Operation<Float> original){
        Set<EntityEquipmentSlot> previous = FirstAidCompat.setHitSlots(entity, EnumSet.of(EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET));
        try {
            return original.call(entity, source, amount);
        } finally {
            FirstAidCompat.restoreHitSlots(entity, previous);
        }
    }
}
