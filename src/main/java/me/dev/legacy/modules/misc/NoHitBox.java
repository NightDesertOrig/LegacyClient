package me.dev.legacy.modules.misc;

import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.modules.Module;

public class NoHitBox extends Module {
    private static NoHitBox INSTANCE = new NoHitBox();
    public Setting pickaxe = this.register(new Setting("Pickaxe", true));
    public Setting crystal = this.register(new Setting("Crystal", true));
    public Setting gapple = this.register(new Setting("Gapple", true));

    public NoHitBox() {
        super("NoHitBox", "nhb", Module.Category.MISC, true, false, false);
        this.setInstance();
    }

    public static NoHitBox getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new NoHitBox();
        }

        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }
}
