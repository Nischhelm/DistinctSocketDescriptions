package distinctsocketdescriptions.config;

import distinctsocketdescriptions.DistinctSocketDescriptions;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

//@Config(modid = DistinctSocketDescriptions.MODID)
public class ConfigHandler {

    public static void reset() {}

//    @Mod.EventBusSubscriber(modid = DistinctSocketDescriptions.MODID)
    public static class EventHandler {
        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
            if(event.getModID().equals(DistinctSocketDescriptions.MODID)) {
                ConfigManager.sync(DistinctSocketDescriptions.MODID, Config.Type.INSTANCE);
                ConfigHandler.reset();
            }
        }
    }
}
