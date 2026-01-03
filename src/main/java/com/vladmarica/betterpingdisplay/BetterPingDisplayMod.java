package com.vladmarica.betterpingdisplay;

import com.vladmarica.betterpingdisplay.integ.YaclConfigScreenFactory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.neoforged.fml.common.Mod;

import java.util.function.Supplier;

@Mod(value = BetterPingDisplayMod.MODID, dist = Dist.CLIENT)
public class BetterPingDisplayMod {
  public static final Logger logger = LogManager.getLogger("BetterPingDisplay");

  public static final String MODID = "betterpingdisplay";

  public BetterPingDisplayMod(ModContainer container) {
    container.registerConfig(ModConfig.Type.CLIENT, Config.SPEC);

    Supplier<IConfigScreenFactory> configScreenFactorySupplier = () -> {
      if (ModList.get().isLoaded("yet_another_config_lib_v3")) {
        return new YaclConfigScreenFactory();
      } else {
        return ConfigurationScreen::new;
      }
    };
    container.registerExtensionPoint(IConfigScreenFactory.class, configScreenFactorySupplier);
  }
}
