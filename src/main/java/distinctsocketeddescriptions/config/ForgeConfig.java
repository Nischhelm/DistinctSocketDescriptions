package distinctsocketeddescriptions.config;

import distinctsocketeddescriptions.DistinctSocketedDescriptions;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = DistinctSocketedDescriptions.MODID, name = DistinctSocketedDescriptions.MODNAME, category = "")
public class ForgeConfig {

    @Config.Name("Common")
    public static Common COMMON = new Common();

    @Config.Name("Client")
    public static Client CLIENT = new Client();

    public static class Common {
        //Unused currently
    }

    public static class Client {
        //Unused currently
    }
    
    public static void reset() {

    }

    @Mod.EventBusSubscriber(modid = socketed.Socketed.MODID)
    private static class EventHandler {
        
        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
            if(event.getModID().equals(socketed.Socketed.MODID)) {
                ConfigManager.sync(socketed.Socketed.MODID, Config.Type.INSTANCE);
                ForgeConfig.reset();
            }
        }
    }
}