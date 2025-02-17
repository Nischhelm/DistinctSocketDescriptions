package distinctsocketeddescriptions.compat;

import net.minecraft.inventory.EntityEquipmentSlot;

import java.util.ArrayList;
import java.util.List;

public class FirstAidCompat {
    private static final ThreadLocal<List<EntityEquipmentSlot>> affectedArmorSlots = ThreadLocal.withInitial(ArrayList::new);

    public static void addAffectedArmorSlot(EntityEquipmentSlot slot){
        if(!affectedArmorSlots.get().contains(slot))
            affectedArmorSlots.get().add(slot);
    }

    public static List<EntityEquipmentSlot> getAndClearAffectedBodySlots(){
        List<EntityEquipmentSlot> returnList =  new ArrayList<>(affectedArmorSlots.get());
        affectedArmorSlots.get().clear();
        return returnList;
    }
}
