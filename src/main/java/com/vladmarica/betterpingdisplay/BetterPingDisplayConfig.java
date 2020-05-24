package com.vladmarica.betterpingdisplay;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

@Mod.EventBusSubscriber(modid = BetterPingDisplayMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BetterPingDisplayConfig {

    public static String textColor;
    public static String textFormatString;

    public static final ClientConfig CLIENT;
    public static final ForgeConfigSpec CLIENT_SPEC;
    static {
        final Pair<ClientConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
        CLIENT_SPEC = specPair.getRight();
        CLIENT = specPair.getLeft();
    }

    public static void bake() {
        textColor = CLIENT.textColor.get();
        textFormatString = CLIENT.textFormatString.get();
    }

    @SubscribeEvent
    public static void onModConfigEvent(final ModConfig.ModConfigEvent configEvent) {
        if (configEvent.getConfig().getSpec() == CLIENT_SPEC) {
            bake();
        }
    }

    public static class ClientConfig {
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
}