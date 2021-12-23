package com.vladmarica.betterpingdisplay;

import com.vladmarica.betterpingdisplay.client.RenderPingHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fmllegacy.network.FMLNetworkConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(BetterPingDisplayMod.MODID)
public class BetterPingDisplayMod {
    public static final String MODID = "betterpingdisplay";

    private static final Logger LOGGER = LogManager.getLogger();

    public BetterPingDisplayMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupClient);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, BetterPingDisplayConfig.CLIENT_SPEC);

        ModLoadingContext.get().registerExtensionPoint(
                IExtensionPoint.DisplayTest.class,
                () -> new IExtensionPoint.DisplayTest(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));
    }

    private void setupClient(final FMLClientSetupEvent event) {
        //MinecraftForge.EVENT_BUS.register(new RenderPingHandler());
    }

    public static Logger logger() {
        return LOGGER;
    }
}
