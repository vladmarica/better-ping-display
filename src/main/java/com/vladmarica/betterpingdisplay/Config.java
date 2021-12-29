package com.vladmarica.betterpingdisplay;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.apache.commons.lang3.tuple.Pair;


@Mod.EventBusSubscriber(modid = BetterPingDisplayMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class Config {
    public static final ForgeConfigSpec CLIENT_SPEC;

    private static Config instance = new Config();
    private static final ClientConfig CLIENT;
    private static final int DEFAULT_PING_TEXT_COLOR = 0xA0A0A0;
    private static final String DEFAULT_PING_TEXT_FORMAT = "%dms";

    static {
        final Pair<ClientConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
        CLIENT_SPEC = specPair.getRight();
        CLIENT = specPair.getLeft();
    }

    private int textColor = DEFAULT_PING_TEXT_COLOR;
    private String textFormatString = DEFAULT_PING_TEXT_FORMAT;

    private static Config from(String textColor, String textFormatString) {
        Config config = new Config();

        if (textColor.startsWith("#")) {
            try {
                config.textColor = Integer.parseInt(textColor.substring(1), 16);
            }
            catch (NumberFormatException ex) {
                BetterPingDisplayMod
                        .logger()
                        .error("Config option 'textColor' is invalid - it must be a hex color code");
                config.textColor = DEFAULT_PING_TEXT_COLOR;
            }
        }

        if (textFormatString.contains("%d")) {
            config.textFormatString = textFormatString;
        }
        else {
            config.textFormatString = DEFAULT_PING_TEXT_FORMAT;
            BetterPingDisplayMod
                    .logger()
                    .error(
                            "Config option 'textFormatString' is invalid - it needs to contain %d");
        }

        return config;
    }

    public int getTextColor() {
        return textColor;
    }

    public String getTextFormatString() {
        return textFormatString;
    }

    public static Config instance() {
        return instance;
    }

    @SubscribeEvent
    public static void onModConfigEvent(final ModConfigEvent configEvent) {
        if (configEvent.getConfig().getSpec() == CLIENT_SPEC) {
            instance = from(CLIENT.textColor.get(), CLIENT.textFormatString.get());
        }
    }

    private static class ClientConfig {
        public final ForgeConfigSpec.ConfigValue<String> textColor;
        public final ForgeConfigSpec.ConfigValue<String> textFormatString;

        public ClientConfig(ForgeConfigSpec.Builder builder) {
            textColor = builder
                    .comment("The color of the ping display text, written in hex format. Default: #A0A0A0")
                    .define("textColor", "#A0A0A0");

            textFormatString = builder
                    .comment("Customize the display text of the ping display",
                            "Must contain a '%d', which will be replaced with the ping number",
                            "Example: '%dms' will transform into '123ms' if the player's ping is 123",
                            "Default: %dms")
                    .define("textFormatString", "%dms");

        }
    }

    private Config() {}
}
