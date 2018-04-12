package com.vladmarica.betterping;

import com.vladmarica.betterping.client.RenderPingHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.relauncher.Side;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = BetterPing.MODID, version = "1.0", acceptableRemoteVersions="*")
public class BetterPing {

    static final String MODID = "BetterPing";

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) 
    {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
            MinecraftForge.EVENT_BUS.register(new RenderPingHandler());
        }
    }
}
