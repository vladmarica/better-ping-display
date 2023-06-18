package com.vladmarica.betterpingdisplay.client;

import com.vladmarica.betterpingdisplay.Config;
import com.vladmarica.betterpingdisplay.mixin.PlayerTabOverlayInvoker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class RenderPingHandler {
  private static final int PING_TEXT_RENDER_OFFSET = -13;

  public static void render(
      Minecraft mc,
      PlayerTabOverlay overlay,
      GuiGraphics graphics,
      int width,
      int x,
      int y,
      PlayerInfo player) {

    Config config = Config.instance();

    String pingString = String.format(config.getTextFormatString(), player.getLatency());
    int pingStringWidth = mc.font.width(pingString);
    int pingTextColor =
        config.shouldAutoColorText()
            ? PingColors.getColor(player.getLatency())
            : config.getTextColor();

    int textX = width + x - pingStringWidth;
    if (config.shouldRenderPingBars()) {
      textX += PING_TEXT_RENDER_OFFSET;
    }

    // Draw the ping text
    graphics.drawString(mc.font, pingString, textX, y, pingTextColor);

    // Draw the ping bars
    if (config.shouldRenderPingBars()) {
      ((PlayerTabOverlayInvoker) overlay).invokeRenderPingIcon(graphics, width, x, y, player);
    }
  }
}
