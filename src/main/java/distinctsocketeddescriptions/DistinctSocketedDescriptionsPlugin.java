package distinctsocketeddescriptions;

import distinctsocketeddescriptions.compat.ModLoaded;
import fermiumbooter.FermiumRegistryAPI;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.MixinBootstrap;

import java.util.Map;

@IFMLLoadingPlugin.MCVersion("1.12.2")
public class DistinctSocketedDescriptionsPlugin implements IFMLLoadingPlugin {

    public DistinctSocketedDescriptionsPlugin() {
        MixinBootstrap.init();

        FermiumRegistryAPI.enqueueMixin(true, "mixins.dsd.firstaid.json", ModLoaded::isFirstAidLoaded);
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