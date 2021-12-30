package me.dev.legacy.api.util;

import java.awt.*;

public class RainbowUtil {
    public static Color getColour() {
        return Colour.fromHSB((float) (System.currentTimeMillis() % 11520L) / 11520.0F, 1.0F, 1.0F);
    }

    public static Color getFurtherColour(int offset) {
        return Colour.fromHSB((float) ((System.currentTimeMillis() + (long) offset) % 11520L) / 11520.0F, 1.0F, 1.0F);
    }
}
