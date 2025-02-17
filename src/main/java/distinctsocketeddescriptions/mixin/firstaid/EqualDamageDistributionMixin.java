package distinctsocketeddescriptions.mixin.firstaid;

import distinctsocketeddescriptions.compat.FirstAidCompat;
import ichttt.mods.firstaid.common.damagesystem.distribution.EqualDamageDistribution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EqualDamageDistribution.class)
public class EqualDamageDistributionMixin {
    @Inject(
            method = "reduceDamage",
            at = @At(value = "INVOKE", target = "Lnet/minecraftforge/common/ForgeHooks;onLivingDamage(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/util/DamageSource;F)F"),
            remap = false
    )
    private void catchAffectedArmorSlots(float originalDamage, EntityPlayer player, DamageSource source, CallbackInfoReturnable<Float> cir){
        //We have to assume that all bodyparts get damaged here because the actual calculation which bodyparts are affected will only happen AFTER livingdamage,
        // but we need the info before, specifically in LivingDamage -> GatherDefenses (DDD)
        for(EntityEquipmentSlot slot : EntityEquipmentSlot.values())
            if(slot.getSlotType() == EntityEquipmentSlot.Type.ARMOR)
                FirstAidCompat.addAffectedArmorSlot(slot);
    }
}
