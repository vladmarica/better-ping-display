package com.vladmarica.betterpingdisplay.client;

import com.vladmarica.betterpingdisplay.Config;
import com.vladmarica.betterpingdisplay.mixin.PlayerTabOverlayInvoker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;

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

    String pingString = String.format(Config.getTextFormatString(), player.getLatency());
    int pingStringWidth = mc.font.width(pingString);
    int pingTextColor =
        Config.shouldAutoColorText()
            ? PingColors.getColor(player.getLatency())
            : Config.getTextColor();

    boolean renderPingBars = Config.shouldRenderPingBars();

    int textX = width + x - pingStringWidth;
    if (renderPingBars) {
        textX += PING_TEXT_RENDER_OFFSET;
    }

    boolean suppressedByWATUT = WATUTCompat.shouldRenderIdleState(overlay, graphics, width, x, y, player);

    // Draw ping text when either bars are enabled or WATUT doesn't suppress it
    if (renderPingBars || !suppressedByWATUT) {
        graphics.drawString(mc.font, pingString, textX, y, pingTextColor);
    }

    // Draw ping icon only when bars are enabled and not suppressed
    if (renderPingBars && !suppressedByWATUT) {
        ((PlayerTabOverlayInvoker) overlay).invokeRenderPingIcon(graphics, width, x, y, player);
    }
  }
}
