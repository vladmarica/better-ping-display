package com.vladmarica.betterpingdisplay.client;

import net.minecraft.util.*;

// taken from https://github.com/vladmarica/better-ping-display/blob/1.17.x/src/main/java/com/vladmarica/betterpingdisplay/client/ColorUtil.java
public class ColorUtil {
  public static final int PING_START = 0;
  public static final int PING_MID = 150;
  public static final int PING_END = 300;

  public static final int COLOR_GREY = 0x535353;
  public static final int COLOR_START = 0x00E676;
  public static final int COLOR_MID = 0xD6CD30;
  public static final int COLOR_END = 0xE53935;

  public static int getColor(int ping) {
    if (ping < PING_START) 
      return COLOR_GREY;
    if (ping < PING_MID) 
      return interpolate(COLOR_START, COLOR_MID, computeOffset(PING_START, PING_MID, ping));
    return interpolate(COLOR_MID, COLOR_END, computeOffset(PING_MID, PING_END, Math.min(ping, PING_END)));
  }

  static float computeOffset(int start, int end, int value) {
    float offset = (value - start) / (float) (end - start);
    return MathHelper.clamp_float(offset, 0.0F, 1.0F);
  }

  public static int interpolate(int colorStart, int colorEnd, float offset) {
    if (offset < 0 || offset > 1)
      throw new IllegalArgumentException("Interpolation offset must be between 0.0 and 1.0.");

    int redDiff = getRed(colorEnd) - getRed(colorStart);
    int greenDiff = getGreen(colorEnd) - getGreen(colorStart);
    int blueDiff = getBlue(colorEnd) - getBlue(colorStart);

    int newRed = Math.round(getRed(colorStart) + (redDiff * offset));
    int newGreen = Math.round(getGreen(colorStart) + (greenDiff * offset));
    int newBlue = Math.round(getBlue(colorStart) + (blueDiff * offset));

    return (newRed << 16) | (newGreen << 8) | newBlue;
  }

  static int getRed(int color) {
    return (color >> 16) & 0xFF;
  }

  static int getGreen(int color) {
    return (color >> 8) & 0xFF;
  }

  static int getBlue(int color) {
    return color & 0xFF;
  }
}
