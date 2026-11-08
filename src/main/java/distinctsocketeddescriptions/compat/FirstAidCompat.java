package distinctsocketeddescriptions.compat;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public class FirstAidCompat {
    /**
     * Armor slots FirstAid is currently distributing damage to, per player.
     * Only set while FirstAid fires LivingDamageEvent for a distribution round, so it can never leak into later damage events
     */
    private static final Map<EntityLivingBase, Set<EntityEquipmentSlot>> hitSlots = Collections.synchronizedMap(new WeakHashMap<>());

    /**
     * @return the previously set slots, to be restored afterwards via {@link #restoreHitSlots}
     */
    @Nullable
    public static Set<EntityEquipmentSlot> setHitSlots(EntityLivingBase entity, Set<EntityEquipmentSlot> slots) {
        return hitSlots.put(entity, slots);
    }

    public static void restoreHitSlots(EntityLivingBase entity, @Nullable Set<EntityEquipmentSlot> previous) {
        if(previous == null) hitSlots.remove(entity);
        else hitSlots.put(entity, previous);
    }

    /**
     * @return the armor slots hit in the current FirstAid distribution round, or null if the current damage event wasn't fired by a FirstAid distribution
     */
    @Nullable
    public static Set<EntityEquipmentSlot> getHitSlots(EntityLivingBase entity) {
        return hitSlots.get(entity);
    }
}
