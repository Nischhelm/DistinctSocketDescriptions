package distinctsocketeddescriptions;

import distinctsocketeddescriptions.activator.DDDAttackedActivator;
import distinctsocketeddescriptions.activator.DDDAttackingActivator;
import distinctsocketeddescriptions.config.DefaultJsonAddons;
import distinctsocketeddescriptions.effect.DDDApplyImmunityEffect;
import distinctsocketeddescriptions.effect.determinedamage.DDDDamageEffect;
import distinctsocketeddescriptions.effect.DDDRemoveImmunityEffect;
import distinctsocketeddescriptions.effect.DDDResistanceEffect;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import socketed.common.util.SocketedUtil;

@Mod(
        modid = DistinctSocketedDescriptions.MODID,
        name = DistinctSocketedDescriptions.MODNAME,
        version = DistinctSocketedDescriptions.VERSION,
        dependencies = "required-after:socketed"
)
public class DistinctSocketedDescriptions {
    public static final String MODID = "distinctsocketeddescriptions";
    public static final String MODNAME = "DistinctSocketedDescriptions";
    public static final String VERSION = "1.0.0";
    public static Logger LOGGER = LogManager.getLogger();

    @Mod.Instance(MODID)
    public static DistinctSocketedDescriptions instance;

    @Mod.EventHandler
    public static void init(FMLInitializationEvent event) {
        if(Loader.isModLoaded("distinctdamagedescriptions")){
            DefaultJsonAddons.initializeBuiltinEntries();
            SocketedUtil.registerActivator(DDDAttackingActivator.TYPE_NAME, DDDAttackingActivator.class, DistinctSocketedDescriptions.MODID);
            SocketedUtil.registerActivator(DDDAttackedActivator.TYPE_NAME, DDDAttackedActivator.class, DistinctSocketedDescriptions.MODID);

            SocketedUtil.registerEffectType(DDDDamageEffect.TYPE_NAME, DDDDamageEffect.class, DistinctSocketedDescriptions.MODID);
            SocketedUtil.registerEffectType(DDDResistanceEffect.TYPE_NAME, DDDResistanceEffect.class, DistinctSocketedDescriptions.MODID);
            SocketedUtil.registerEffectType(DDDApplyImmunityEffect.TYPE_NAME, DDDApplyImmunityEffect.class, DistinctSocketedDescriptions.MODID);
            SocketedUtil.registerEffectType(DDDRemoveImmunityEffect.TYPE_NAME, DDDRemoveImmunityEffect.class, DistinctSocketedDescriptions.MODID);
        }
    }
}