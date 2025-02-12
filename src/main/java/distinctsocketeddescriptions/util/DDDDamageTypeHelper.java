package distinctsocketeddescriptions.util;

import yeelp.distinctdamagedescriptions.api.DDDDamageType;
import yeelp.distinctdamagedescriptions.api.impl.DDDBuiltInDamageType;

import java.util.HashMap;
import java.util.Map;

public class DDDDamageTypeHelper {
    private static final Map<String, DDDDamageType> damageTypes = new HashMap<>();

    public static void init(){
        registerDamageType("ddd_blunt", DDDBuiltInDamageType.BLUDGEONING);
        registerDamageType("ddd_pierce", DDDBuiltInDamageType.PIERCING);
        registerDamageType("ddd_slash", DDDBuiltInDamageType.SLASHING);
        registerDamageType("ddd_fire", DDDBuiltInDamageType.FIRE);
        registerDamageType("ddd_cold", DDDBuiltInDamageType.COLD);
        registerDamageType("ddd_lightning", DDDBuiltInDamageType.LIGHTNING);
        registerDamageType("ddd_poison", DDDBuiltInDamageType.POISON);
        registerDamageType("ddd_radiant", DDDBuiltInDamageType.RADIANT);
        registerDamageType("ddd_necrotic", DDDBuiltInDamageType.NECROTIC);
        registerDamageType("ddd_force", DDDBuiltInDamageType.FORCE);
        registerDamageType("ddd_acid", DDDBuiltInDamageType.ACID);
    }

    public static void registerDamageType(String name, DDDDamageType type){
        damageTypes.put(name, type);
    }

    public static DDDDamageType getFromString(String name){
        return damageTypes.getOrDefault(name, DDDBuiltInDamageType.UNKNOWN);
    }
}
