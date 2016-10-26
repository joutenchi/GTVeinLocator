package pers.gwyog.gtveinlocator.proxies;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import pers.gwyog.gtveinlocator.CommonProxy;
import pers.gwyog.gtveinlocator.compat.LoadedModHelper;

public class ClientProxy extends CommonProxy{
    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);
		LoadedModHelper.init();
    }

    @Override
    public void init(FMLInitializationEvent e) {
        super.init(e);
    }

    @Override
    public void postInit(FMLPostInitializationEvent e) {
        super.postInit(e);
    }
    
}