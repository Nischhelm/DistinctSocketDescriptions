package distinctsocketeddescriptions.mixin.firstaid;

import distinctsocketeddescriptions.compat.FirstAidCompat;
import ichttt.mods.firstaid.api.enums.EnumPlayerPart;
import ichttt.mods.firstaid.common.damagesystem.distribution.DirectDamageDistribution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DirectDamageDistribution.class)
public class DirectDamageDistributionMixin {
    @Shadow(remap = false) @Final private EnumPlayerPart part;

    @Inject(
            method = "distributeDamage",
            at = @At("HEAD"),
            remap = false
    )
    private void catchAffectedArmorSlots(float damage, EntityPlayer player, DamageSource source, boolean addStat, CallbackInfoReturnable<Float> cir){
        //player.sendMessage(new TextComponentString("DirectDmgDistr adding Slot "+this.part.slot.getName()+ " for part "+this.part.name()));
        FirstAidCompat.addAffectedArmorSlot(this.part.slot);
    }
}
