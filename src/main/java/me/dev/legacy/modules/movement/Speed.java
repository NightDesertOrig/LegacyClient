package me.dev.legacy.modules.movement;

import me.dev.legacy.api.util.EntityUtil;
import me.dev.legacy.api.util.MathUtil;
import me.dev.legacy.api.util.PlayerUtil;
import me.dev.legacy.api.util.Timer;
import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.modules.Module;

public class Speed extends Module {
    Setting mode;
    Setting yPortSpeed;
    Setting step;
    Setting vanillaSpeed;
    private double playerSpeed;
    private final Timer timer;

    public Speed() {
        super("Speed", "YPort Speed.", Module.Category.MOVEMENT, false, false, false);
        this.mode = this.register(new Setting("Mode", Speed.Mode.yPort));
        this.yPortSpeed = this.register(new Setting("YPort Speed", 0.6D, 0.5D, 1.5D, (v) -> {
            return this.mode.getValue() == Speed.Mode.yPort;
        }));
        this.step = this.register(new Setting("Step", true, (v) -> {
            return this.mode.getValue() == Speed.Mode.yPort;
        }));
        this.vanillaSpeed = this.register(new Setting("Vanilla Speed", 1.0D, 1.7D, 10.0D, (v) -> {
            return this.mode.getValue() == Speed.Mode.Vanilla;
        }));
        this.timer = new Timer();
    }

    public void onEnable() {
        this.playerSpeed = PlayerUtil.getBaseMoveSpeed();
        if (((Boolean) this.step.getValue()).booleanValue()) {
            if (fullNullCheck()) {
                return;
            }

            mc.field_71439_g.field_70138_W = 2.0F;
        }

    }

    public void onDisable() {
        EntityUtil.resetTimer();
        this.timer.reset();
        if (((Boolean) this.step.getValue()).booleanValue()) {
            mc.field_71439_g.field_70138_W = 0.6F;
        }

    }

    public void onUpdate() {
        if (nullCheck()) {
            this.disable();
        } else {
            if (this.mode.getValue() == Speed.Mode.Vanilla) {
                if (mc.field_71439_g == null || mc.field_71441_e == null) {
                    return;
                }

                double[] calc = MathUtil.directionSpeed(((Double) this.vanillaSpeed.getValue()).doubleValue() / 10.0D);
                mc.field_71439_g.field_70159_w = calc[0];
                mc.field_71439_g.field_70179_y = calc[1];
            }

            if (this.mode.getValue() == Speed.Mode.yPort) {
                if (!PlayerUtil.isMoving(mc.field_71439_g) || mc.field_71439_g.func_70090_H() && mc.field_71439_g.func_180799_ab() || mc.field_71439_g.field_70123_F) {
                    return;
                }

                if (mc.field_71439_g.field_70122_E) {
                    EntityUtil.setTimer(1.15F);
                    mc.field_71439_g.func_70664_aZ();
                    PlayerUtil.setSpeed(mc.field_71439_g, PlayerUtil.getBaseMoveSpeed() + ((Double) this.yPortSpeed.getValue()).doubleValue() / 10.0D);
                } else {
                    mc.field_71439_g.field_70181_x = -1.0D;
                    EntityUtil.resetTimer();
                }
            }

        }
    }

    public static enum Mode {
        yPort,
        Vanilla;
    }
}
