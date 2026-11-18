package distinctsocketdescriptions;

import distinctsocketdescriptions.config.DefaultJsonAddons;
import distinctsocketdescriptions.effect.DDDApplyImmunityEffect;
import distinctsocketdescriptions.effect.DDDDamageEffect;
import distinctsocketdescriptions.effect.DDDRemoveImmunityEffect;
import distinctsocketdescriptions.effect.DDDResistanceEffect;
import distinctsocketdescriptions.effect.activator.DDDAttackedActivator;
import distinctsocketdescriptions.effect.activator.DDDAttackingActivator;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import socketed.api.util.SocketedUtil;

@Mod(
        modid = DistinctSocketDescriptions.MODID,
        name = DistinctSocketDescriptions.MODNAME,
        version = DistinctSocketDescriptions.VERSION,
        dependencies = "required-after:socketed;required-after:distinctdamagedescriptions"
)
public class DistinctSocketDescriptions {
    public static final String MODID = "distinctsocketdescriptions";
    public static final String MODNAME = "Distinct Socket Descriptions";
    public static final String VERSION = "1.0.0";
    public static Logger LOGGER = LogManager.getLogger();

    @Mod.EventHandler
    public static void init(FMLInitializationEvent event) {
        DefaultJsonAddons.initializeBuiltinEntries();

        SocketedUtil.registerActivator(DDDAttackingActivator.TYPE_NAME, DDDAttackingActivator.class, DistinctSocketDescriptions.MODID);
        SocketedUtil.registerActivator(DDDAttackedActivator.TYPE_NAME, DDDAttackedActivator.class, DistinctSocketDescriptions.MODID);

        SocketedUtil.registerEffectType(DDDDamageEffect.TYPE_NAME, DDDDamageEffect.class, DistinctSocketDescriptions.MODID);
        SocketedUtil.registerEffectType(DDDResistanceEffect.TYPE_NAME, DDDResistanceEffect.class, DistinctSocketDescriptions.MODID);
        SocketedUtil.registerEffectType(DDDApplyImmunityEffect.TYPE_NAME, DDDApplyImmunityEffect.class, DistinctSocketDescriptions.MODID);
        SocketedUtil.registerEffectType(DDDRemoveImmunityEffect.TYPE_NAME, DDDRemoveImmunityEffect.class, DistinctSocketDescriptions.MODID);
    }
}
