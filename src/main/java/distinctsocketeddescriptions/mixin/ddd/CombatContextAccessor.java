package distinctsocketeddescriptions.mixin.ddd;

import net.minecraft.inventory.EntityEquipmentSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import yeelp.distinctdamagedescriptions.util.lib.damagecalculation.CombatContext;

@Mixin(value = CombatContext.class, remap = false)
public interface CombatContextAccessor {
    /**
     * Armor slots DDD considers hit for this calculation (all armor slots, narrowed down by DDD's slot injectors, for example for projectiles)
     */
    @Accessor(value = "validArmorSlots", remap = false)
    Iterable<EntityEquipmentSlot> dsd$getValidArmorSlots();
}
