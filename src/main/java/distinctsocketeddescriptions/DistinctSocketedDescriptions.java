package distinctsocketeddescriptions;

import distinctsocketeddescriptions.config.DefaultJsonAddons;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
        DefaultJsonAddons.initializeBuiltinEntries();
    }
}