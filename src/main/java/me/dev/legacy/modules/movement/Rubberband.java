package me.dev.legacy.modules.movement;

import me.dev.legacy.api.AbstractModule;
import me.dev.legacy.api.util.Util;
import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.modules.Module;
import net.minecraft.network.play.client.CPacketPlayer.Position;

public class Rubberband extends Module {
    private final Setting mode;
    private final Setting Ym;

    public Rubberband() {
        super("Rubberband", "does rubberband", Module.Category.MOVEMENT, true, false, false);
        this.mode = this.register(new Setting("Mode", Rubberband.RubbeMode.Motion));
        this.Ym = this.register(new Setting("Motion", Integer.valueOf(5), Integer.valueOf(1), Integer.valueOf(15), (v) -> {
            return this.mode.getValue() == Rubberband.RubbeMode.Motion;
        }));
    }

    public void onEnable() {
        if (!AbstractModule.fullNullCheck()) {
            ;
        }
    }

    public void onUpdate() {
        switch ((Rubberband.RubbeMode) this.mode.getValue()) {
            case Motion:
                Util.mc.field_71439_g.field_70181_x = (double) ((Integer) this.Ym.getValue()).intValue();
                break;
            case Packet:
                mc.func_147114_u().func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + (double) ((Integer) this.Ym.getValue()).intValue(), mc.field_71439_g.field_70161_v, true));
                break;
            case Teleport:
                mc.field_71439_g.func_70634_a(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + (double) ((Integer) this.Ym.getValue()).intValue(), mc.field_71439_g.field_70161_v);
        }

        this.toggle();
    }

    public static enum RubbeMode {
        Motion,
        Teleport,
        Packet;
    }
}
