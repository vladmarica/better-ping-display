package com.vladmarica.betterpingdisplay;

import net.minecraftforge.common.config.Config;

@Config(modid = BetterPingDisplayMod.MODID)
public class BetterPingDisplayConfig {

    @Config.Comment({"The color of the ping display text, written in hex format. Default: #A0A0A0"})
    public static String textColor = "#A0A0A0";

    @Config.Comment({
        "Customize the display text of the ping display",
        "Must contain a '%d', which will be replaced with the ping number",
        "Example: '%dms' will transform into '123ms' if the player's ping is 123",
        "Default: %dms"
    })
    public static String textFormatString = "%dms";
}
