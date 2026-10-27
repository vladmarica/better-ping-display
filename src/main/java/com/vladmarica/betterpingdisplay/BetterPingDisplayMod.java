package com.vladmarica.betterpingdisplay;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.vladmarica.betterpingdisplay.client.RenderPingHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.relauncher.Side;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

@Mod(modid = BetterPingDisplayMod.MODID, version = "1.1", acceptableRemoteVersions="*")
public class BetterPingDisplayMod {

    static final String MODID = "BetterPing";
    
    static Logger logger = LogManager.getLogger("Better Ping Display");

    private static final String DEFAULT_PING_TEXT_COLOR = "#A0A0A0";
    private static final String DEFAULT_PING_TEXT_FORMAT = "%dms";
    
    public static boolean autoColorText, renderPingBars;
    public static String textFormatString;
    public static int textColor;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        Configuration config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();
        
        this.autoColorText = config.getBoolean("autoColorText", Configuration.CATEGORY_GENERAL, true,
        		"Whether to color a player's ping based on their latency.\n"
        		+ "Example: low latency = green, high latency = red\n"
        		+ "If this setting is true, then the 'textColor' setting is ignored");
        
        this.renderPingBars = config.getBoolean("renderPingBars", Configuration.CATEGORY_GENERAL, false,
        		"Whether to also draw the default Minecraft ping bars");
        
        String textColorString = config.getString("textColor", Configuration.CATEGORY_GENERAL, DEFAULT_PING_TEXT_COLOR,
        		"The color of the ping display text, written in hex format. Default: #A0A0A0\n"
        		+ "Has no effect if 'autoColorText' is set to true");
        
        String textFormatStringRaw = config.getString("textFormatString", Configuration.CATEGORY_GENERAL, DEFAULT_PING_TEXT_FORMAT,
        		"Customize the display text of the ping display\n"
        		+ "Must contain a '%d', which will be replaced with the ping number\n"
        		+ "Example: '%dms' will transform into '123ms' if the player's ping is 123\n"
        		+ "Default: %dms");
        
        // Validating textColor
        try {
        	if(!textColorString.startsWith("#")) throw new NumberFormatException();
        	
    		this.textColor = Integer.parseInt(textColorString.substring(1), 16);
        } catch (NumberFormatException ex) {
        	this.logger.error("Config option 'textColor' is invalid - it must be a hex color code with a leading #");
        	this.textColor = Integer.parseInt(DEFAULT_PING_TEXT_COLOR.substring(1), 16);
        }
        
        // Validating textFormatString
        if(textFormatStringRaw.contains("%d")) {
        	this.textFormatString = textFormatStringRaw;
        } else {
        	this.logger.error("Config option 'textFormatString' is invalid - it needs to contain %d");
        	this.textFormatString = DEFAULT_PING_TEXT_FORMAT;
        }
        
        config.save();
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event) 
    {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
            MinecraftForge.EVENT_BUS.register(new RenderPingHandler());
        }
    }
}
