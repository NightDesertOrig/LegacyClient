package me.dev.legacy.api.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;

public class Wrapper {
    private static FontRenderer fontRenderer;
    public static Minecraft mc = Minecraft.func_71410_x();

    public static Minecraft getMinecraft() {
        return Minecraft.func_71410_x();
    }

    public static Minecraft GetMC() {
        return mc;
    }

    public static EntityPlayerSP GetPlayer() {
        return mc.field_71439_g;
    }

    public static EntityPlayerSP getPlayer() {
        return getMinecraft().field_71439_g;
    }

    public static Entity getRenderEntity() {
        return mc.func_175606_aa();
    }

    public static World getWorld() {
        return getMinecraft().field_71441_e;
    }

    public static int getKey(String keyname) {
        return Keyboard.getKeyIndex(keyname.toUpperCase());
    }

    public static FontRenderer getFontRenderer() {
        return fontRenderer;
    }
}
