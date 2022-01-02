package com.vladmarica.betterpingdisplay.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.vladmarica.betterpingdisplay.Config;
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

  public static void render(
      Minecraft mc,
      PlayerTabOverlay overlay,
      PoseStack stack,
      int width,
      int x,
      int y,
      PlayerInfo player) {

    Config config = Config.instance();

    String pingString = String.format(config.getTextFormatString(), player.getLatency());
    int pingStringWidth = mc.font.width(pingString);
    int pingTextColor = config.getTextColor();

    int textX = width + x - pingStringWidth;
    if (config.shouldRenderPingBars()) {
      textX += PING_TEXT_RENDER_OFFSET;
    }

    // Draw the ping text
    mc.font.drawShadow(stack, new TextComponent(pingString), textX, y, pingTextColor);

    // Draw the ping bars
    if (config.shouldRenderPingBars()) {
      ((PlayerTabOverlayInvoker) overlay).invokeRenderPingIcon(stack, width, x, y, player);
    }
  }
}
