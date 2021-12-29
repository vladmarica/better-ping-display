package com.vladmarica.betterpingdisplay.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.vladmarica.betterpingdisplay.BetterPingDisplayConfig;
import com.vladmarica.betterpingdisplay.mixin.PlayerTabOverlayInvoker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
@OnlyIn(Dist.CLIENT)
public final class RenderPingHandler {
    private static final int PING_TEXT_RENDER_OFFSET = -13;
    private static final int PLAYER_ICON_WIDTH = 9;

    public static void renderPingDisplay(
            Minecraft mc, PlayerTabOverlay overlay, PoseStack stack, int width, int x, int y, PlayerInfo player) {
        // Here is the magic, rendering the ping text
        String pingString = String.format(BetterPingDisplayConfig.instance().getTextFormatString(), player.getLatency());
        int pingStringWidth = mc.font.width(pingString);
        int pingTextColor = BetterPingDisplayConfig.instance().getTextColor();
        int textX = width + x - pingStringWidth + PING_TEXT_RENDER_OFFSET;

        // Draw the ping text
        mc.font.drawShadow(
                stack,
                new TextComponent(pingString),
                textX,
                y,
                pingTextColor);

        // Draw the ping bars
        ((PlayerTabOverlayInvoker) overlay).invokeRenderPingIcon(stack, width, x, y, player);
    }
}
