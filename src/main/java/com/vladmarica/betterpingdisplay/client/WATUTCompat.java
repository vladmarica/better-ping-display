package com.vladmarica.betterpingdisplay.client;

import com.corosus.watut.WatutMod;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.neoforged.fml.ModList;

public class WATUTCompat {
  public static final String WATUT_MOD_ID = "watut";

  public static boolean shouldRenderIdleState(PlayerTabOverlay overlay, GuiGraphics graphics, int width, int x, int y, PlayerInfo player) {
    if (!isWATUTModLoaded()) return false;

    // Check if WATUT's "ZZ" ping replacement is enabled
    return WatutMod.getPlayerStatusManagerClient().renderPingIconHook(overlay, graphics, width, x, y, player);
  }

  private static boolean isWATUTModLoaded() {
    return ModList.get().isLoaded(WATUT_MOD_ID);
  }
}
