package distinctsocketeddescriptions.config;

import distinctsocketeddescriptions.DistinctSocketedDescriptions;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

//@Config(modid = DistinctSocketedDescriptions.MODID)
public class ConfigHandler {

    public static void reset() {}

//    @Mod.EventBusSubscriber(modid = DistinctSocketedDescriptions.MODID)
    public static class EventHandler {
        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
            if(event.getModID().equals(DistinctSocketedDescriptions.MODID)) {
                ConfigManager.sync(DistinctSocketedDescriptions.MODID, Config.Type.INSTANCE);
                ConfigHandler.reset();
            }
        }
    }
}
