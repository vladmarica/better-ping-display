package com.vladmarica.betterpingdisplay;

import com.vladmarica.betterpingdisplay.client.PingColors;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = BetterPingDisplayMod.MODID)
public class Config {
    private static final String DEFAULT_PING_TEXT_COLOR = "#A0A0A0";
    private static final String DEFAULT_PING_TEXT_FORMAT = "%dms";

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.ConfigValue<String> SPEC_TEXT_COLOR = BUILDER
            .comment(String.format(
                    "The color of the ping display text, written in hex format. Default: %s\n", DEFAULT_PING_TEXT_COLOR),
                    "Has no effect if 'autoColorText' is set to true")
            .define("textColor", DEFAULT_PING_TEXT_COLOR, o -> {
                if (!(o instanceof String)) {
                    return false;
                }

                try {
                    Integer.parseInt(((String) o).substring(1), 16);
                    return true;
                } catch (NumberFormatException e) {
                    return false;
                }
            });

    private static final ModConfigSpec.ConfigValue<String> SPEC_PING_TEXT_FORMAT = BUILDER
            .comment(
                    "Customize the display text of the ping display",
                    "Must contain a '%d', which will be replaced with the ping number",
                    "Example: '%dms' will transform into '123ms' if the player's ping is 123",
                    String.format("Default: %s", DEFAULT_PING_TEXT_FORMAT))
            .define("textFormatString", DEFAULT_PING_TEXT_FORMAT);

    private static final ModConfigSpec.ConfigValue<Boolean> SPEC_RENDER_PING_BARS = BUILDER
            .comment("Whether to also draw the default Minecraft ping bars")
            .define("renderPingBars", false);

    private static final ModConfigSpec.ConfigValue<Boolean> SPEC_AUTO_COLOR_TEXT = BUILDER
            .comment(
                    "Whether to color a player's ping based on their latency.",
                    "Example: low latency = green, high latency = red",
                    "If this setting is true, then the 'textColor' setting is ignored")
            .define("autoColorText", true);

    static final ModConfigSpec SPEC = BUILDER.build();

    private static int textColor;
    private static String textFormatString;
    private static boolean renderPingBars;
    private static boolean autoColorText;

    public static int getTextColor() {
        return textColor;
    }

    public static String getTextFormatString() {
        return textFormatString;
    }

    public static boolean shouldRenderPingBars() {
        return renderPingBars;
    }

    public static boolean shouldAutoColorText() {
        return autoColorText;
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent builder) {
        textColor = Integer.parseInt(SPEC_TEXT_COLOR.get().substring(1), 16) | PingColors.ALPHA_MASK;
        textFormatString = SPEC_PING_TEXT_FORMAT.get();
        renderPingBars = SPEC_RENDER_PING_BARS.get();
        autoColorText = SPEC_AUTO_COLOR_TEXT.get();

        BetterPingDisplayMod.logger.atInfo().log("Config loaded!");
    }
}
