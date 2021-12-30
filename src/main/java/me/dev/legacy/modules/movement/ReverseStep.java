package me.dev.legacy.modules.movement;

import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.modules.Module;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ReverseStep extends Module {
    private final Setting speed = this.register(new Setting("Speed", 0.0F, 0.0F, 20.0F));

    public ReverseStep() {
        super("ReverseStep", "Reverse step", Module.Category.MOVEMENT, false, false, false);
    }

    @SubscribeEvent
    public void onUpdate() {
        if (mc.field_71439_g != null && mc.field_71441_e != null && !mc.field_71439_g.func_70090_H() && !mc.field_71439_g.func_180799_ab()) {
            if (mc.field_71439_g.field_70122_E) {
                mc.field_71439_g.field_70181_x -= (double) (((Float) this.speed.getValue()).floatValue() / 10.0F);
            }

        }
    }
}
