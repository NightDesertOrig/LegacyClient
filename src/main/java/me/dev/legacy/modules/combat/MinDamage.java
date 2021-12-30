package me.dev.legacy.modules.combat;

import com.google.common.eventbus.Subscribe;
import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.modules.Module;

public class MinDamage extends Module {
    private static MinDamage INSTANCE = new MinDamage();
    private final Setting EnableDamage = this.register(new Setting("EnableDamage", 4.0F, 1.0F, 36.0F));
    private final Setting DisableDamage = this.register(new Setting("DisableDamage", 4.0F, 1.0F, 36.0F));

    public MinDamage() {
        super("MinDamage", "Set minimal damage for auto crystal.", Module.Category.COMBAT, true, false, false);
        INSTANCE = this;
    }

    public static MinDamage getInstance() {
        return INSTANCE;
    }

    @Subscribe
    public void onEnable() {
        AutoCrystal.getInstance().minDamage.setValue(this.EnableDamage.getValue());
    }

    @Subscribe
    public void onDisable() {
        AutoCrystal.getInstance().minDamage.setValue(this.DisableDamage.getValue());
    }
}
