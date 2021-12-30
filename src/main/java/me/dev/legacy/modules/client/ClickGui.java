package me.dev.legacy.modules.client;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.dev.legacy.Legacy;
import me.dev.legacy.api.event.ClientEvent;
import me.dev.legacy.impl.command.Command;
import me.dev.legacy.impl.gui.LegacyGui;
import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.modules.Module;
import net.minecraft.client.settings.GameSettings.Options;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

public class ClickGui extends Module {
    private static ClickGui INSTANCE = new ClickGui();
    public Setting prefix = this.register(new Setting("Prefix", "."));
    public Setting customFov = this.register(new Setting("CustomFov", false));
    public Setting fov = this.register(new Setting("Fov", 150.0F, -180.0F, 180.0F));
    public Setting gears = this.register(new Setting("Gears", false, "draws gears"));
    public Setting red = this.register(new Setting("Red", Integer.valueOf(210), Integer.valueOf(0), Integer.valueOf(255)));
    public Setting green = this.register(new Setting("Green", Integer.valueOf(130), Integer.valueOf(0), Integer.valueOf(255)));
    public Setting blue = this.register(new Setting("Blue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
    public Setting hoverAlpha = this.register(new Setting("Alpha", Integer.valueOf(180), Integer.valueOf(0), Integer.valueOf(255)));
    public Setting alpha = this.register(new Setting("HoverAlpha", Integer.valueOf(240), Integer.valueOf(0), Integer.valueOf(255)));
    public Setting rainbow = this.register(new Setting("Rainbow", false));
    public Setting rainbowModeHud;
    public Setting rainbowModeA;
    public Setting rainbowHue;
    public Setting rainbowBrightness;
    public Setting rainbowSaturation;
    public Setting startcolorred;
    public Setting startcolorgreen;
    public Setting startcolorblue;
    public Setting endcolorred;
    public Setting endcolorgreen;
    public Setting endcolorblue;
    public Setting fontcolorr;
    public Setting fontcolorg;
    public Setting fontcolorb;
    public Setting outline;
    public Setting testcolorr;
    public Setting testcolorgreen;
    public Setting testcolorblue;
    public float hue;

    public ClickGui() {
        super("ClickGui", "Opens the ClickGui", Module.Category.CLIENT, true, false, false);
        this.rainbowModeHud = this.register(new Setting("HRainbowMode", ClickGui.rainbowMode.Static, (v) -> {
            return ((Boolean) this.rainbow.getValue()).booleanValue();
        }));
        this.rainbowModeA = this.register(new Setting("ARainbowMode", ClickGui.rainbowModeArray.Static, (v) -> {
            return ((Boolean) this.rainbow.getValue()).booleanValue();
        }));
        this.rainbowHue = this.register(new Setting("Delay", Integer.valueOf(240), Integer.valueOf(0), Integer.valueOf(600), (v) -> {
            return ((Boolean) this.rainbow.getValue()).booleanValue();
        }));
        this.rainbowBrightness = this.register(new Setting("Brightness ", 150.0F, 1.0F, 255.0F, (v) -> {
            return ((Boolean) this.rainbow.getValue()).booleanValue();
        }));
        this.rainbowSaturation = this.register(new Setting("Saturation", 150.0F, 1.0F, 255.0F, (v) -> {
            return ((Boolean) this.rainbow.getValue()).booleanValue();
        }));
        this.startcolorred = this.register(new Setting("StartRed", Integer.valueOf(210), Integer.valueOf(0), Integer.valueOf(255)));
        this.startcolorgreen = this.register(new Setting("StartGreen", Integer.valueOf(210), Integer.valueOf(0), Integer.valueOf(255)));
        this.startcolorblue = this.register(new Setting("StartBlue", Integer.valueOf(210), Integer.valueOf(0), Integer.valueOf(255)));
        this.endcolorred = this.register(new Setting("EndRed", Integer.valueOf(210), Integer.valueOf(0), Integer.valueOf(255)));
        this.endcolorgreen = this.register(new Setting("EndGreen", Integer.valueOf(210), Integer.valueOf(0), Integer.valueOf(255)));
        this.endcolorblue = this.register(new Setting("EndBlue", Integer.valueOf(210), Integer.valueOf(0), Integer.valueOf(255)));
        this.fontcolorr = this.register(new Setting("FontRed", Integer.valueOf(210), Integer.valueOf(0), Integer.valueOf(255)));
        this.fontcolorg = this.register(new Setting("FontGreen", Integer.valueOf(210), Integer.valueOf(0), Integer.valueOf(255)));
        this.fontcolorb = this.register(new Setting("FontBlue", Integer.valueOf(210), Integer.valueOf(0), Integer.valueOf(255)));
        this.outline = this.register(new Setting("Outline", false));
        this.testcolorr = this.register(new Setting("PanelRed", Integer.valueOf(210), Integer.valueOf(0), Integer.valueOf(255)));
        this.testcolorgreen = this.register(new Setting("PanelGreen", Integer.valueOf(210), Integer.valueOf(0), Integer.valueOf(255)));
        this.testcolorblue = this.register(new Setting("PanelBlue", Integer.valueOf(210), Integer.valueOf(0), Integer.valueOf(255)));
        this.setInstance();
    }

    public static ClickGui getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ClickGui();
        }

        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    public void onUpdate() {
        if (((Boolean) this.customFov.getValue()).booleanValue()) {
            mc.field_71474_y.func_74304_a(Options.FOV, ((Float) this.fov.getValue()).floatValue());
        }

    }

    @SubscribeEvent
    public void onSettingChange(ClientEvent event) {
        if (event.getStage() == 2 && event.getSetting().getFeature().equals(this)) {
            if (event.getSetting().equals(this.prefix)) {
                Legacy.commandManager.setPrefix((String) this.prefix.getPlannedValue());
                Command.sendMessage("Prefix set to " + ChatFormatting.DARK_GRAY + Legacy.commandManager.getPrefix());
            }

            Legacy.colorManager.setColor(((Integer) this.red.getPlannedValue()).intValue(), ((Integer) this.green.getPlannedValue()).intValue(), ((Integer) this.blue.getPlannedValue()).intValue(), ((Integer) this.hoverAlpha.getPlannedValue()).intValue());
        }

    }

    public Color getCurrentColor() {
        return ((Boolean) this.rainbow.getValue()).booleanValue() ? Color.getHSBColor(this.hue, (float) ((Float) this.rainbowSaturation.getValue()).intValue() / 255.0F, (float) ((Float) this.rainbowBrightness.getValue()).intValue() / 255.0F) : new Color(((Integer) this.red.getValue()).intValue(), ((Integer) this.green.getValue()).intValue(), ((Integer) this.blue.getValue()).intValue(), ((Integer) this.alpha.getValue()).intValue());
    }

    public void onEnable() {
        mc.func_147108_a(LegacyGui.getClickGui());
    }

    public void onLoad() {
        Legacy.colorManager.setColor(((Integer) this.red.getValue()).intValue(), ((Integer) this.green.getValue()).intValue(), ((Integer) this.blue.getValue()).intValue(), ((Integer) this.hoverAlpha.getValue()).intValue());
        Legacy.commandManager.setPrefix((String) this.prefix.getValue());
    }

    public void onTick() {
        if (!(mc.field_71462_r instanceof LegacyGui)) {
            this.disable();
        }

    }

    public static enum rainbowMode {
        Static,
        Sideway;
    }

    public static enum rainbowModeArray {
        Static,
        Up;
    }
}
