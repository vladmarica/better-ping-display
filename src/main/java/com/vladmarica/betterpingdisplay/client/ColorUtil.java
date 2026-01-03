package com.vladmarica.betterpingdisplay.client;

import com.google.common.annotations.VisibleForTesting;

import java.awt.*;

public final class ColorUtil {
  public static int interpolate(int colorStart, int colorEnd, float offset) {
    if (offset < 0 || offset > 1) {
      throw new IllegalArgumentException("Offset must be between 0.0 and 1.0");
    }

    int redDiff = getRed(colorEnd) - getRed(colorStart);
    int greenDiff = getGreen(colorEnd) - getGreen(colorStart);
    int blueDiff = getBlue(colorEnd) - getBlue(colorStart);

    int newRed = Math.round(getRed(colorStart) + (redDiff * offset));
    int newGreen = Math.round(getGreen(colorStart) + (greenDiff * offset));
    int newBlue = Math.round(getBlue(colorStart) + (blueDiff * offset));

    return (newRed << 16) | (newGreen << 8) | newBlue;
  }

  public static Color parseColor(String s) {
    return new Color(Integer.parseInt(s.substring(1), 16));
  }

  public static String colorToString(Color c) {
    return String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
  }

  @VisibleForTesting
  static int getRed(int color) {
    return (color >> 16) & 0xFF;
  }

  @VisibleForTesting
  static int getGreen(int color) {
    return (color >> 8) & 0xFF;
  }

  @VisibleForTesting
  static int getBlue(int color) {
    return color & 0xFF;
  }

  private ColorUtil() {}
}
