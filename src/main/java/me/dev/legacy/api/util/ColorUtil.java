package me.dev.legacy.api.util;

import me.dev.legacy.modules.client.ClickGui;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class ColorUtil {
    public ColorUtil(int i, int i1, int i2, int i3) {
    }

    public static void color(int color) {
        GL11.glColor4f((float) (color >> 16 & 255) / 255.0F, (float) (color >> 8 & 255) / 255.0F, (float) (color & 255) / 255.0F, (float) (color >> 24 & 255) / 255.0F);
    }

    public static int shadeColour(int color, int precent) {
        int r = ((color & 16711680) >> 16) * (100 + precent) / 100;
        int g = ((color & '\uff00') >> 8) * (100 + precent) / 100;
        int b = (color & 255) * (100 + precent) / 100;
        return (new Color(r, g, b)).hashCode();
    }

    public static int getRainbow(int speed, float s) {
        float hue = (float) (System.currentTimeMillis() % (long) speed);
        return Color.getHSBColor(hue / (float) speed, s, 1.0F).getRGB();
    }

    public static int getRainbow(int speed, int offset, float s) {
        float hue = (float) (System.currentTimeMillis() % (long) speed + (long) offset * 15L);
        return Color.getHSBColor(hue / (float) speed, s, 1.0F).getRGB();
    }

    public static int toRGBA(int r, int g, int b) {
        return toRGBA(r, g, b, 255);
    }

    public static int toRGBA(int r, int g, int b, int a) {
        return (r << 16) + (g << 8) + b + (a << 24);
    }

    public static int toARGB(int r, int g, int b, int a) {
        return (new Color(r, g, b, a)).getRGB();
    }

    public static int toRGBA(float r, float g, float b, float a) {
        return toRGBA((int) (r * 255.0F), (int) (g * 255.0F), (int) (b * 255.0F), (int) (a * 255.0F));
    }

    public static Color rainbow(int delay) {
        double rainbowState = Math.ceil((double) (System.currentTimeMillis() + (long) delay) / 20.0D);
        return Color.getHSBColor((float) ((rainbowState %= 360.0D) / 360.0D), ((Float) ClickGui.getInstance().rainbowSaturation.getValue()).floatValue() / 255.0F, ((Float) ClickGui.getInstance().rainbowBrightness.getValue()).floatValue() / 255.0F);
    }

    public static int toRGBA(float[] colors) {
        if (colors.length != 4) {
            throw new IllegalArgumentException("colors[] must have a length of 4!");
        } else {
            return toRGBA(colors[0], colors[1], colors[2], colors[3]);
        }
    }

    public static int toRGBA(double[] colors) {
        if (colors.length != 4) {
            throw new IllegalArgumentException("colors[] must have a length of 4!");
        } else {
            return toRGBA((float) colors[0], (float) colors[1], (float) colors[2], (float) colors[3]);
        }
    }

    public static int toRGBA(Color color) {
        return toRGBA(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    public static class ColorName {
        public int r;
        public int g;
        public int b;
        public String name;

        public ColorName(String name, int r, int g, int b) {
            this.r = r;
            this.g = g;
            this.b = b;
            this.name = name;
        }

        public int computeMSE(int pixR, int pixG, int pixB) {
            return ((pixR - this.r) * (pixR - this.r) + (pixG - this.g) * (pixG - this.g) + (pixB - this.b) * (pixB - this.b)) / 3;
        }

        public int getR() {
            return this.r;
        }

        public int getG() {
            return this.g;
        }

        public int getB() {
            return this.b;
        }

        public String getName() {
            return this.name;
        }
    }

    public static class Colors {
        public static final int WHITE = ColorUtil.toRGBA(255, 255, 255, 255);
        public static final int BLACK = ColorUtil.toRGBA(0, 0, 0, 255);
        public static final int RED = ColorUtil.toRGBA(255, 0, 0, 255);
        public static final int GREEN = ColorUtil.toRGBA(0, 255, 0, 255);
        public static final int BLUE = ColorUtil.toRGBA(0, 0, 255, 255);
        public static final int ORANGE = ColorUtil.toRGBA(255, 128, 0, 255);
        public static final int PURPLE = ColorUtil.toRGBA(163, 73, 163, 255);
        public static final int GRAY = ColorUtil.toRGBA(127, 127, 127, 255);
        public static final int DARK_RED = ColorUtil.toRGBA(64, 0, 0, 255);
        public static final int YELLOW = ColorUtil.toRGBA(255, 255, 0, 255);
        public static final int RAINBOW = Integer.MIN_VALUE;
    }
}
