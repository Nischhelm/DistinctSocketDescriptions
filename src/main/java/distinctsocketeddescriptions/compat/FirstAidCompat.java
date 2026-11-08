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

    /**
     * Amount of LivingDamage events FirstAid fired so far in the current damage distribution, per player
     */
    private static final Map<EntityLivingBase, Integer> distributionRounds = Collections.synchronizedMap(new WeakHashMap<>());

    /**
     * @return the previous round count, to be restored afterwards via {@link #endDistribution}
     */
    @Nullable
    public static Integer startDistribution(EntityLivingBase entity) {
        return distributionRounds.put(entity, 0);
    }

    public static void endDistribution(EntityLivingBase entity, @Nullable Integer previous) {
        if(previous == null) distributionRounds.remove(entity);
        else distributionRounds.put(entity, previous);
    }

    public static void startRound(EntityLivingBase entity) {
        distributionRounds.computeIfPresent(entity, (e, rounds) -> rounds + 1);
    }

    /**
     * FirstAid fires one LivingDamage per distribution round. After the first round DDD starts a new calculation,
     * reclassifying the remaining damage, which fires DetermineDamage again for the same hit.
     * @return true if the current damage event is not the first one FirstAid fired for this hit
     */
    public static boolean isFollowUpRound(EntityLivingBase entity) {
        Integer rounds = distributionRounds.get(entity);
        return rounds != null && rounds > 1;
    }
}
