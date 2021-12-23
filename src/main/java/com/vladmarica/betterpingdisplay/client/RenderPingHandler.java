package com.vladmarica.betterpingdisplay.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.vladmarica.betterpingdisplay.BetterPingDisplayConfig;
import com.vladmarica.betterpingdisplay.BetterPingDisplayMod;
import com.google.common.collect.Ordering;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;
import com.vladmarica.betterpingdisplay.mixin.PlayerTabOverlayInvoker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.level.GameType;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Iterator;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class RenderPingHandler {
    private static final int DEFAULT_PING_TEXT_COLOR = 0xA0A0A0;
    private static final String DEFAULT_PING_TEXT_FORMAT = "%dms";
    private static final int PING_TEXT_RENDER_OFFSET = -13;
    private static final int PLAYER_SLOT_EXTRA_WIDTH = 45;
    private static final int PLAYER_ICON_WIDTH = 9;

    private boolean configLoaded = false;
    private int pingTextColor;
    private String pingTextFormat;

    public void loadConfig() {
        if (BetterPingDisplayConfig.textColor.startsWith("#")) {
            try {
                pingTextColor = Integer.parseInt(BetterPingDisplayConfig.textColor.substring(1), 16);
            }
            catch (NumberFormatException ex) {
                BetterPingDisplayMod.logger().error("Config option 'pingTextColor' is invalid - it must be a hex color code");
                pingTextColor = DEFAULT_PING_TEXT_COLOR;
            }
        }
        else {
            pingTextColor = DEFAULT_PING_TEXT_COLOR;
        }

        if (BetterPingDisplayConfig.textFormatString.contains("%d")) {
            pingTextFormat = BetterPingDisplayConfig.textFormatString;
        }
        else {
            pingTextFormat = DEFAULT_PING_TEXT_FORMAT;
            BetterPingDisplayMod.logger().error("Config option 'textFormatString' is invalid - it needs to contain %d");
        }
    }

    public static void renderPingDisplay(Minecraft mc, PlayerTabOverlay overlay, PoseStack stack, int width, int x, int y, PlayerInfo player) {
        // Here is the magic, rendering the ping text
        String pingString = String.format(pingTextFormat, player.getLatency());
        int pingStringWidth = mc.font.width(pingString);
        mc.font.drawShadow(
                overlay,
                new TextComponent(pingString),
                (float) i1 + k2 - pingStringWidth + PING_TEXT_RENDER_OFFSET - (displayPlayerIcons ? PLAYER_ICON_WIDTH : 0),
                (float) l2,
                pingTextColor);

        ((PlayerTabOverlayInvoker) overlay).invokeRenderPingIcon(stack, width, x - (PLAYER_ICON_WIDTH), y, player);
        //ov.renderPingIcon(matrixStack, i1, k2 - (displayPlayerIcons ? PLAYER_ICON_WIDTH : 0), l2, player); /* drawPing */
    }
}
