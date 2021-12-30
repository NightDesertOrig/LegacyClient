package me.dev.legacy.modules.player;

import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.modules.Module;

public class Reach extends Module {
    public Setting override = this.register(new Setting("Override", false));
    public Setting add = this.register(new Setting("Add", 3.0F, (v) -> {
        return !((Boolean) this.override.getValue()).booleanValue();
    }));
    public Setting reach = this.register(new Setting("Reach", 6.0F, (v) -> {
        return ((Boolean) this.override.getValue()).booleanValue();
    }));
    private static Reach INSTANCE = new Reach();

    public Reach() {
        super("Reach", "Extends your block reach", Module.Category.PLAYER, true, false, false);
        this.setInstance();
    }

    private void setInstance() {
        INSTANCE = this;
    }

    public static Reach getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Reach();
        }

        return INSTANCE;
    }

    public String getDisplayInfo() {
        return ((Boolean) this.override.getValue()).booleanValue() ? ((Float) this.reach.getValue()).toString() : ((Float) this.add.getValue()).toString();
    }
}
