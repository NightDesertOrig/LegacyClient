package me.dev.legacy.modules.client;

import me.dev.legacy.Legacy;
import me.dev.legacy.api.event.ClientEvent;
import me.dev.legacy.impl.command.Command;
import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.modules.Module;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

public class Font extends Module {
    private boolean reloadFont = false;
    public Setting fontName = this.register(new Setting("FontName", "Arial", "Name of the font."));
    public Setting fontSize = this.register(new Setting("FontSize", Integer.valueOf(18), "Size of the font."));
    public Setting fontStyle = this.register(new Setting("FontStyle", Integer.valueOf(0), "Style of the font."));
    public Setting antiAlias = this.register(new Setting("AntiAlias", true, "Smoother font."));
    public Setting fractionalMetrics = this.register(new Setting("Metrics", true, "Thinner font."));
    public Setting shadow = this.register(new Setting("Shadow", true, "Less shadow offset font."));
    public Setting showFonts = this.register(new Setting("Fonts", false, "Shows all fonts."));
    public Setting full = this.register(new Setting("Full", false));
    private static Font INSTANCE = new Font();

    public Font() {
        super("CustomFont", "CustomFont for all of the clients text. Use the font command.", Module.Category.CLIENT, true, false, false);
        this.setInstance();
    }

    private void setInstance() {
        INSTANCE = this;
    }

    public static Font getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Font();
        }

        return INSTANCE;
    }

    @SubscribeEvent
    public void onSettingChange(ClientEvent event) {
        Setting setting;
        if (event.getStage() == 2 && (setting = event.getSetting()) != null && setting.getFeature().equals(this)) {
            if (setting.getName().equals("FontName") && !checkFont(setting.getPlannedValue().toString(), false)) {
                Command.sendMessage("§cThat font doesnt exist.");
                event.setCanceled(true);
                return;
            }

            this.reloadFont = true;
        }

    }

    public void onTick() {
        if (((Boolean) this.showFonts.getValue()).booleanValue()) {
            checkFont("Hello", true);
            Command.sendMessage("Current Font: " + (String) this.fontName.getValue());
            this.showFonts.setValue(false);
        }

        if (this.reloadFont) {
            Legacy.textManager.init(false);
            this.reloadFont = false;
        }

    }

    public static boolean checkFont(String font, boolean message) {
        String[] var3 = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        int var4 = var3.length;

        for (int var5 = 0; var5 < var4; ++var5) {
            String s = var3[var5];
            if (!message && s.equals(font)) {
                return true;
            }

            if (message) {
                Command.sendMessage(s);
            }
        }

        return false;
    }
}
