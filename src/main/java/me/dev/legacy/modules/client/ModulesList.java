package me.dev.legacy.modules.client;

import me.dev.legacy.Legacy;
import me.dev.legacy.api.event.events.render.Render2DEvent;
import me.dev.legacy.api.util.*;
import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.modules.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

import java.util.List;

@ModuleManifest(
        label = "CsArrsyList",
        category = Module.Category.CLIENT
)
public class ModulesList extends Module {
    public Setting arrayX = this.register(new Setting("arraylistPosX", Integer.valueOf(740), Integer.valueOf(0), Integer.valueOf(885)));
    public Setting arrayY = this.register(new Setting("arraylistPosY", Integer.valueOf(2), Integer.valueOf(0), Integer.valueOf(1000)));
    public Setting arrayoffset = this.register(new Setting("arraylistOffSet", Integer.valueOf(2), Integer.valueOf(0), Integer.valueOf(1000)));
    private static ModulesList INSTANCE = new ModulesList();
    public static final Minecraft mc = Minecraft.func_71410_x();
    int width = 0;
    int height = 0;

    public ModulesList() {
        super("ArrayList", "CSGO arraylist", Module.Category.CLIENT, true, false, false);
        this.setInstance();
    }

    public static ModulesList getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ModulesList();
        }

        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public void onRender2D(Render2DEvent event) {
        int padding = 5;
        Colour fill = new Colour(0, 0, 0, 255);
        Colour outline = new Colour(127, 127, 127, 255);
        RenderUtil.drawBorderedRect(((Integer) this.arrayX.getValue()).intValue() - padding, (double) (((Integer) this.arrayY.getValue()).intValue() - padding), ((Integer) this.arrayX.getValue()).intValue() + padding + this.getWidth(), (double) (((Integer) this.arrayY.getValue()).intValue() + this.getHeight() - 1), 1, fill.hashCode(), outline.hashCode(), false);
        RenderUtil.drawHLineG(((Integer) this.arrayX.getValue()).intValue() - padding, ((Integer) this.arrayY.getValue()).intValue() - padding, ((Integer) this.arrayX.getValue()).intValue() + padding + this.getWidth() - (((Integer) this.arrayX.getValue()).intValue() - padding), RainbowUtil.getColour().hashCode(), RainbowUtil.getFurtherColour(((Integer) this.arrayoffset.getValue()).intValue()).hashCode());
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        boolean isTop = false;
        boolean isLeft = false;
        if ((float) ((Integer) this.arrayX.getValue()).intValue() < (float) scaledResolution.func_78328_b() / 2.0F) {
            isTop = true;
        }

        if ((float) ((Integer) this.arrayX.getValue()).intValue() < (float) scaledResolution.func_78326_a() / 2.0F) {
            isLeft = true;
        }

        List hacks = Legacy.moduleManager.getEnabledModules();
        int bestWidth = 0;
        int y = 0;
        if (HUD.getInstance().renderingMode.getValue() == HUD.RenderingMode.ABC) {
            for (int k = 0; k < Legacy.moduleManager.sortedModulesABC.size(); ++k) {
                String str = (String) Legacy.moduleManager.sortedModulesABC.get(k);
                HudUtil.drawHudString(str, ((Integer) this.arrayX.getValue()).intValue(), ((Integer) this.arrayY.getValue()).intValue() + y, (new Colour(255, 255, 255, 255)).hashCode());
                int w = HudUtil.getHudStringWidth(str);
                if (w > bestWidth) {
                    bestWidth = w;
                }

                y += 11;
            }

            this.height = y;
            this.width = bestWidth;
        }

    }
}
