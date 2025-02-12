package distinctsocketeddescriptions.util;

import yeelp.distinctdamagedescriptions.api.DDDDamageType;
import yeelp.distinctdamagedescriptions.api.impl.DDDBuiltInDamageType;

import java.util.HashMap;
import java.util.Map;

public class DDDDamageTypeHelper {
    private static final Map<String, DDDDamageType> damageTypes = new HashMap<>();

    public static void init(){
        registerDamageType("BLUNT", DDDBuiltInDamageType.BLUDGEONING);
        registerDamageType("PIERCE", DDDBuiltInDamageType.PIERCING);
        registerDamageType("SLASH", DDDBuiltInDamageType.SLASHING);
        registerDamageType("FIRE", DDDBuiltInDamageType.FIRE);
        registerDamageType("COLD", DDDBuiltInDamageType.COLD);
        registerDamageType("LIGHTNING", DDDBuiltInDamageType.LIGHTNING);
        registerDamageType("POISON", DDDBuiltInDamageType.POISON);
        registerDamageType("RADIANT", DDDBuiltInDamageType.RADIANT);
        registerDamageType("NECROTIC", DDDBuiltInDamageType.NECROTIC);
        registerDamageType("FORCE", DDDBuiltInDamageType.FORCE);
        registerDamageType("ACID", DDDBuiltInDamageType.ACID);
    }

    public static void registerDamageType(String name, DDDDamageType type){
        damageTypes.put(name, type);
    }

    public static DDDDamageType getFromString(String name){
        return damageTypes.getOrDefault(name, DDDBuiltInDamageType.UNKNOWN);
    }
}
