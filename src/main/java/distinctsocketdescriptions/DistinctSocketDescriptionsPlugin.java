package distinctsocketdescriptions;

import distinctsocketdescriptions.compat.ModLoaded;
import fermiumbooter.FermiumRegistryAPI;
import fermiumbooter.util.FermiumJarScanner;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.MixinBootstrap;

import java.util.Map;

@IFMLLoadingPlugin.MCVersion("1.12.2")
public class DistinctSocketDescriptionsPlugin implements IFMLLoadingPlugin {

    public DistinctSocketDescriptionsPlugin() {
        //Gated so a missing DDD shows the missing dependency screen instead of failing the mixin
        FermiumRegistryAPI.enqueueMixin("mixins.dsd.ddd.json", () -> FermiumJarScanner.isModPresent("distinctdamagedescriptions"));
        FermiumRegistryAPI.enqueueMixin("mixins.dsd.firstaid.json", ModLoaded::isFirstAidLoaded);
    }

    @Override
    public String[] getASMTransformerClass()
    {
        return new String[0];
    }

    @Override
    public String getModContainerClass()
    {
        return null;
    }

    @Override
    public String getSetupClass()
    {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) { }

    @Override
    public String getAccessTransformerClass()
    {
        return null;
    }
}