package com.vladmarica.betterpingdisplay;

import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static net.minecraftforge.fml.IExtensionPoint.DisplayTest.IGNORESERVERONLY;

@Mod(BetterPingDisplayMod.MODID)
public class BetterPingDisplayMod {
  public static final String MODID = "betterpingdisplay";
  private static final Logger LOGGER = LogManager.getLogger();

  public BetterPingDisplayMod() {
    ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_SPEC);

    ModLoadingContext.get()
        .registerExtensionPoint(
            IExtensionPoint.DisplayTest.class,
            () ->
                new IExtensionPoint.DisplayTest(
                    () -> IGNORESERVERONLY, (a, b) -> true));
  }

  public static Logger logger() {
    return LOGGER;
  }
}
