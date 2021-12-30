package me.dev.legacy.api.util;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.dev.legacy.Legacy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class HudUtil implements Util {
    public static String getWelcomerLine() {
        int time = TimeUtil.get_hour();
        String line;
        if (time >= 0 && time < 12) {
            line = " nice femboy cock, " + mc.field_71439_g.func_70005_c_() + ChatFormatting.RESET + " :)";
        } else if (time >= 12 && time < 16) {
            line = " nice femboy cock, " + mc.field_71439_g.func_70005_c_() + ChatFormatting.RESET + " :)";
        } else if (time >= 16 && time < 24) {
            line = " nice femboy cock, " + mc.field_71439_g.func_70005_c_() + ChatFormatting.RESET + " :)";
        } else {
            line = " nice femboy cock, " + mc.field_71439_g.func_70005_c_() + ChatFormatting.RESET + " :)";
        }

        return line;
    }

    public static void drawHudString(String string, int x, int y, int colour) {
        mc.field_71466_p.func_175063_a(string, (float) x, (float) y, colour);
    }

    public static int getHudStringWidth(String string) {
        return mc.field_71466_p.func_78256_a(string);
    }

    public static int getHudStringHeight(String string) {
        return mc.field_71466_p.field_78288_b;
    }

    public static String getTotems() {
        String totems = "";
        int totemCount = mc.field_71439_g.field_71071_by.field_70462_a.stream().filter((stack) -> {
            return stack.func_77973_b() == Items.field_190929_cY;
        }).mapToInt(ItemStack::func_190916_E).sum() + (mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_190929_cY ? 1 : 0);
        if (totemCount > 2) {
            totems = totems + ChatFormatting.GREEN;
        } else {
            totems = totems + ChatFormatting.RED;
        }

        return totems + totemCount;
    }

    public static String getPingLine() {
        String line = "";
        int ping = Legacy.serverManager.getPing();
        if (ping > 150) {
            line = line + ChatFormatting.RED;
        } else if (ping > 100) {
            line = line + ChatFormatting.YELLOW;
        } else {
            line = line + ChatFormatting.GREEN;
        }

        return line + " " + ping;
    }

    public static String getTpsLine() {
        String line = "";
        double tps = (double) MathUtil.round(Legacy.serverManager.getTPS(), 1);
        if (tps > 16.0D) {
            line = line + ChatFormatting.GREEN;
        } else if (tps > 10.0D) {
            line = line + ChatFormatting.YELLOW;
        } else {
            line = line + ChatFormatting.RED;
        }

        return line + " " + tps;
    }

    public static String getFpsLine() {
        String line = "";
        int fps = Minecraft.func_175610_ah();
        if (fps > 120) {
            line = line + ChatFormatting.GREEN;
        } else if (fps > 60) {
            line = line + ChatFormatting.YELLOW;
        } else {
            line = line + ChatFormatting.RED;
        }

        return line + " " + fps;
    }

    public static String getAnaTimeLine() {
        String line = "";
        line = line + (TimeUtil.get_hour() < 10 ? "0" + TimeUtil.get_hour() : TimeUtil.get_hour());
        line = line + ":";
        line = line + (TimeUtil.get_minuite() < 10 ? "0" + TimeUtil.get_minuite() : TimeUtil.get_minuite());
        line = line + ":";
        line = line + (TimeUtil.get_second() < 10 ? "0" + TimeUtil.get_second() : TimeUtil.get_second());
        return line;
    }

    public static String getDate() {
        return TimeUtil.get_year() + "/" + TimeUtil.get_month() + "/" + TimeUtil.get_day();
    }

    public static int getRightX(String string, int x) {
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        return scaledResolution.func_78326_a() - x - mc.field_71466_p.func_78256_a(string);
    }
}
