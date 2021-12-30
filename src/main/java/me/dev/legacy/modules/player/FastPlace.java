package me.dev.legacy.modules.player;

import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.modules.Module;
import net.minecraft.item.Item;
import net.minecraft.item.ItemEndCrystal;
import net.minecraft.item.ItemExpBottle;

public class FastPlace extends Module {
    Setting xp = this.register(new Setting("XP", true));
    Setting crystals = this.register(new Setting("Crystals", true));
    Setting everything = this.register(new Setting("Everything", false));

    public FastPlace() {
        super("FastPlace", "Fast place items.", Module.Category.PLAYER, true, false, false);
    }

    public void onUpdate() {
        Item main = mc.field_71439_g.func_184614_ca().func_77973_b();
        Item off = mc.field_71439_g.func_184592_cb().func_77973_b();
        boolean mainXP = main instanceof ItemExpBottle;
        boolean offXP = off instanceof ItemExpBottle;
        boolean mainC = main instanceof ItemEndCrystal;
        boolean offC = off instanceof ItemEndCrystal;
        if (mainXP | offXP && ((Boolean) this.xp.getValue()).booleanValue()) {
            mc.field_71467_ac = 0;
        }

        if (mainC | offC && ((Boolean) this.crystals.getValue()).booleanValue()) {
            mc.field_71467_ac = 0;
        }

        if (((Boolean) this.everything.getValue()).booleanValue()) {
            mc.field_71467_ac = 0;
        }

    }
}
