package distinctsocketeddescriptions.compat;

import net.minecraftforge.fml.common.Loader;

public class ModLoaded {
    private static Boolean isFirstAidLoaded = null;

    public static boolean isFirstAidLoaded(){
        if(isFirstAidLoaded == null) isFirstAidLoaded = Loader.isModLoaded("firstaid");
        return isFirstAidLoaded;
    }
}
