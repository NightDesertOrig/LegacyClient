package me.dev.legacy.api;

import me.dev.legacy.Legacy;
import me.dev.legacy.api.manager.TextManager;
import me.dev.legacy.api.util.Util;
import me.dev.legacy.impl.gui.LegacyGui;
import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.modules.Module;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AbstractModule implements Util {
    public List settings = new ArrayList();
    public TextManager renderer;
    private String name;

    public AbstractModule() {
        this.renderer = Legacy.textManager;
    }

    public AbstractModule(String name) {
        this.renderer = Legacy.textManager;
        this.name = name;
    }

    public static boolean nullCheck() {
        return mc.field_71439_g == null;
    }

    public static boolean fullNullCheck() {
        return mc.field_71439_g == null || mc.field_71441_e == null;
    }

    public String getName() {
        return this.name;
    }

    public List getSettings() {
        return this.settings;
    }

    public boolean hasSettings() {
        return !this.settings.isEmpty();
    }

    public boolean isEnabled() {
        return this instanceof Module ? ((Module) this).isOn() : false;
    }

    public boolean isDisabled() {
        return !this.isEnabled();
    }

    public Setting register(Setting setting) {
        setting.setFeature(this);
        this.settings.add(setting);
        if (this instanceof Module && mc.field_71462_r instanceof LegacyGui) {
            LegacyGui.getInstance().updateModule((Module) this);
        }

        return setting;
    }

    public void unregister(Setting settingIn) {
        ArrayList removeList = new ArrayList();
        Iterator var3 = this.settings.iterator();

        while (var3.hasNext()) {
            Setting setting = (Setting) var3.next();
            if (setting.equals(settingIn)) {
                removeList.add(setting);
            }
        }

        if (!removeList.isEmpty()) {
            this.settings.removeAll(removeList);
        }

        if (this instanceof Module && mc.field_71462_r instanceof LegacyGui) {
            LegacyGui.getInstance().updateModule((Module) this);
        }

    }

    public Setting getSettingByName(String name) {
        Iterator var2 = this.settings.iterator();

        Setting setting;
        do {
            if (!var2.hasNext()) {
                return null;
            }

            setting = (Setting) var2.next();
        } while (!setting.getName().equalsIgnoreCase(name));

        return setting;
    }

    public void reset() {
        Iterator var1 = this.settings.iterator();

        while (var1.hasNext()) {
            Setting setting = (Setting) var1.next();
            setting.setValue(setting.getDefaultValue());
        }

    }

    public void clearSettings() {
        this.settings = new ArrayList();
    }
}
