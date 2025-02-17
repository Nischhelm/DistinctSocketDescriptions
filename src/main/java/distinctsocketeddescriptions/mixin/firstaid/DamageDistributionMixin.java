package distinctsocketeddescriptions.mixin.firstaid;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import distinctsocketeddescriptions.compat.FirstAidCompat;
import ichttt.mods.firstaid.api.enums.EnumPlayerPart;
import ichttt.mods.firstaid.common.damagesystem.distribution.DamageDistribution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.TextComponentString;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;

@Mixin(DamageDistribution.class)
public class DamageDistributionMixin {
    @Inject(
            method = "distributeDamage",
            at = @At(value = "INVOKE", target = "Lnet/minecraftforge/common/ForgeHooks;onLivingDamage(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/util/DamageSource;F)F"),
            remap = false
    )
    private void catchAffectedArmorSlots(float damage, EntityPlayer player, DamageSource source, boolean addStat, CallbackInfoReturnable<Float> cir, @Local EntityEquipmentSlot slot){
        //player.sendMessage(new TextComponentString("BaseDmgDistr adding Slot "+slot.getName()));
        FirstAidCompat.addAffectedArmorSlot(slot);
    }
}










