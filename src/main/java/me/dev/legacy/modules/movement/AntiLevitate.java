package me.dev.legacy.modules.movement;

import me.dev.legacy.modules.Module;
import net.minecraft.potion.Potion;

import java.util.Objects;

public class AntiLevitate extends Module {
    public AntiLevitate() {
        super("AntiLevitate", "Removes levitation effect", Module.Category.MOVEMENT, false, false, false);
    }

    public void onUpdate() {
        if (mc.field_71439_g.func_70644_a((Potion) Objects.requireNonNull(Potion.func_180142_b("levitation")))) {
            mc.field_71439_g.func_184596_c(Potion.func_180142_b("levitation"));
        }

    }
}
