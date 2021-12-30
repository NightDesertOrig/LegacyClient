package me.dev.legacy.modules.client;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.dev.legacy.api.event.events.render.Render2DEvent;
import me.dev.legacy.api.util.Colour;
import me.dev.legacy.api.util.HudUtil;
import me.dev.legacy.api.util.RainbowUtil;
import me.dev.legacy.api.util.RenderUtil;
import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.modules.Module;

public class Watermark extends Module {
    private static Watermark INSTANCE = new Watermark();
    public Setting waterMarkX = this.register(new Setting("WatermarkPosX", Integer.valueOf(740), Integer.valueOf(0), Integer.valueOf(885)));
    public Setting waterMarkY = this.register(new Setting("WatermarkPosY", Integer.valueOf(2), Integer.valueOf(0), Integer.valueOf(100)));
    public Setting waterMarkoffset = this.register(new Setting("Offset", Integer.valueOf(1000), Integer.valueOf(0), Integer.valueOf(1000)));
    public Setting Fps = this.register(new Setting("Fps", false));
    public Setting Tps = this.register(new Setting("Tps", false));
    public Setting Time = this.register(new Setting("Time", false));
    public Setting Ping = this.register(new Setting("Ping", false));
    String text = "";

    public Watermark() {
        super("Watermark", "CSGO watermark", Module.Category.CLIENT, true, false, false);
        this.setInstance();
    }

    public static Watermark getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Watermark();
        }

        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    public int getHeight() {
        return HudUtil.getHudStringHeight(this.text);
    }

    public int getWidth() {
        return HudUtil.getHudStringWidth(this.text);
    }

    public void onRender2D(Render2DEvent event) {
        int padding = 5;
        Colour fill = new Colour(0, 0, 0, 255);
        Colour outline = new Colour(127, 127, 127, 255);
        RenderUtil.drawBorderedRect(((Integer) this.waterMarkX.getValue()).intValue() - padding, (double) (((Integer) this.waterMarkY.getValue()).intValue() - padding), ((Integer) this.waterMarkX.getValue()).intValue() + padding + this.getWidth(), (double) (((Integer) this.waterMarkY.getValue()).intValue() + padding + this.getHeight() - 1), 1, fill.hashCode(), outline.hashCode(), false);
        RenderUtil.drawHLineG(((Integer) this.waterMarkX.getValue()).intValue() - padding, ((Integer) this.waterMarkY.getValue()).intValue() - padding + 1, ((Integer) this.waterMarkX.getValue()).intValue() + padding + this.getWidth() - 1 - (((Integer) this.waterMarkX.getValue()).intValue() - padding - 1), RainbowUtil.getColour().hashCode(), RainbowUtil.getFurtherColour(((Integer) INSTANCE.waterMarkoffset.getValue()).intValue()).hashCode());
        this.text = "legacy" + ChatFormatting.RESET + " | " + mc.field_71439_g.func_70005_c_();
        if (((Boolean) getInstance().Fps.getValue()).booleanValue()) {
            this.text = this.text + " |" + HudUtil.getFpsLine() + "Fps" + ChatFormatting.RESET;
        }

        if (((Boolean) getInstance().Tps.getValue()).booleanValue()) {
            this.text = this.text + " |" + HudUtil.getTpsLine() + "Tps" + ChatFormatting.RESET;
        }

        if (((Boolean) getInstance().Ping.getValue()).booleanValue()) {
            this.text = this.text + " |" + HudUtil.getPingLine() + "Ms" + ChatFormatting.RESET;
        }

        if (((Boolean) getInstance().Time.getValue()).booleanValue()) {
            this.text = this.text + " | " + HudUtil.getAnaTimeLine() + ChatFormatting.RESET;
        }

        HudUtil.drawHudString(this.text, ((Integer) this.waterMarkX.getValue()).intValue(), ((Integer) this.waterMarkY.getValue()).intValue(), (new Colour(255, 255, 255, 255)).hashCode());
    }
}
