package distinctsocketdescriptions.compat;

import fermiumbooter.util.FermiumJarScanner;

public class ModLoaded {
    private static Boolean isFirstAidLoaded = null;

    public static boolean isFirstAidLoaded(){
        if(isFirstAidLoaded == null) isFirstAidLoaded = FermiumJarScanner.isModPresent("firstaid");
        return isFirstAidLoaded;
    }
}
