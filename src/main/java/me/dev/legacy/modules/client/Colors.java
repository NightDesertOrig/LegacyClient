package me.dev.legacy.modules.client;

import me.dev.legacy.api.event.ClientEvent;
import me.dev.legacy.api.event.events.update.UpdateEvent;
import me.dev.legacy.api.util.ColorHandler;
import me.dev.legacy.api.util.ColorUtil;
import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.modules.Module;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

public class Colors extends Module {
    private static Colors INSTANCE = new Colors();
    public Setting rainbow = this.register(new Setting("Rainbow", true));
    public Setting rainbowModeHud;
    public Setting rainbowHue;
    public Setting rainbowBrightness;
    public Setting rainbowSaturation;
    public static Setting saturation;
    Setting red;
    Setting green;
    Setting blue;
    Setting alpha;
    int ticks;

    public Colors() {
        super("Colors", "Colors for sync.", Module.Category.CLIENT, true, false, false);
        this.rainbowModeHud = this.register(new Setting("HRainbowMode", Colors.rainbowMode.Static, (v) -> {
            return ((Boolean) this.rainbow.getValue()).booleanValue();
        }));
        this.rainbowHue = this.register(new Setting("Delay", Integer.valueOf(200), Integer.valueOf(0), Integer.valueOf(600), (v) -> {
            return ((Boolean) this.rainbow.getValue()).booleanValue();
        }));
        this.rainbowBrightness = this.register(new Setting("Brightness ", 255.0F, 1.0F, 255.0F, (v) -> {
            return ((Boolean) this.rainbow.getValue()).booleanValue();
        }));
        this.rainbowSaturation = this.register(new Setting("Saturation", 100.0F, 1.0F, 255.0F, (v) -> {
            return ((Boolean) this.rainbow.getValue()).booleanValue();
        }));
        this.red = this.register(new Setting("Red", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
        this.green = this.register(new Setting("Green", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
        this.blue = this.register(new Setting("Blue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
        this.alpha = this.register(new Setting("Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
        this.setInstance();
    }

    public static Colors getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Colors();
        }

        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (this.ticks++ < 10) {
            ColorHandler.setColor(((Integer) this.red.getValue()).intValue(), ((Integer) this.green.getValue()).intValue(), ((Integer) this.blue.getValue()).intValue());
        }

    }

    @SubscribeEvent
    public void onClientEvent(ClientEvent event) {
        if (event.getProperty() == this.red || event.getProperty() == this.green || event.getProperty() == this.blue) {
            ColorHandler.setColor(((Integer) this.red.getValue()).intValue(), ((Integer) this.green.getValue()).intValue(), ((Integer) this.blue.getValue()).intValue());
        }

    }

    public int getCurrentColorHex() {
        return ((Boolean) this.rainbow.getValue()).booleanValue() ? Color.HSBtoRGB((float) ((Integer) this.rainbowHue.getValue()).intValue(), (float) ((Float) this.rainbowSaturation.getValue()).intValue() / 255.0F, (float) ((Float) this.rainbowBrightness.getValue()).intValue() / 255.0F) : ColorUtil.toARGB(((Integer) this.red.getValue()).intValue(), ((Integer) this.green.getValue()).intValue(), ((Integer) this.blue.getValue()).intValue(), ((Integer) this.alpha.getValue()).intValue());
    }

    public static enum rainbowMode {
        Static;
    }

    public static enum rainbowModeArray {
        Static,
        Up;
    }
}
