package com.vladmarica.betterpingdisplay;

import com.vladmarica.betterpingdisplay.client.RenderPingHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.Logger;

@Mod(modid = BetterPingDisplayMod.MODID, version = "0.1", acceptableRemoteVersions="*")
public class BetterPingDisplayMod {

    static final String MODID = "betterpingdisplay";

    private Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        this.logger = event.getModLog();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
            logger.info("Registering render ping handler");
            MinecraftForge.EVENT_BUS.register(new RenderPingHandler());
        }
    }
}
