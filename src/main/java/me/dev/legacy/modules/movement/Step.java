package me.dev.legacy.modules.movement;

import me.dev.legacy.api.util.EntityUtil;
import me.dev.legacy.api.util.MotionUtil;
import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.modules.Module;
import net.minecraft.network.play.client.CPacketPlayer.Position;

import java.text.DecimalFormat;

public class Step extends Module {
    Setting height = this.register(new Setting("Height", 2.5D, 0.5D, 2.5D));
    Setting timer = this.register(new Setting("Timer", false));
    Setting reverse = this.register(new Setting("Reverse", false));
    public Setting vanilla = this.register(new Setting("Vanilla", false));
    private int ticks = 0;

    public Step() {
        super("Step", "Allows you to step up blocks", Module.Category.MOVEMENT, true, false, false);
    }

    public void onUpdate() {
        if (mc.field_71441_e != null && mc.field_71439_g != null) {
            if (!mc.field_71439_g.func_70090_H() && !mc.field_71439_g.func_180799_ab() && !mc.field_71439_g.func_70617_f_() && !mc.field_71474_y.field_74314_A.func_151470_d()) {
                if (((Boolean) this.vanilla.getValue()).booleanValue()) {
                    DecimalFormat df = new DecimalFormat("#");
                    mc.field_71439_g.field_70138_W = Float.parseFloat(df.format(this.height.getValue()));
                } else if (((Boolean) this.timer.getValue()).booleanValue()) {
                    if (this.ticks == 0) {
                        EntityUtil.resetTimer();
                    } else {
                        --this.ticks;
                    }
                }

                if (mc.field_71439_g != null && mc.field_71439_g.field_70122_E && !mc.field_71439_g.func_70090_H() && !mc.field_71439_g.func_70617_f_() && ((Boolean) this.reverse.getValue()).booleanValue()) {
                    for (double y = 0.0D; y < ((Double) this.height.getValue()).doubleValue() + 0.5D; y += 0.01D) {
                        if (!mc.field_71441_e.func_184144_a(mc.field_71439_g, mc.field_71439_g.func_174813_aQ().func_72317_d(0.0D, -y, 0.0D)).isEmpty()) {
                            mc.field_71439_g.field_70181_x = -10.0D;
                            break;
                        }
                    }
                }

                double[] dir = MotionUtil.forward(0.1D);
                boolean twofive = false;
                boolean two = false;
                boolean onefive = false;
                boolean one = false;
                if (mc.field_71441_e.func_184144_a(mc.field_71439_g, mc.field_71439_g.func_174813_aQ().func_72317_d(dir[0], 2.6D, dir[1])).isEmpty() && !mc.field_71441_e.func_184144_a(mc.field_71439_g, mc.field_71439_g.func_174813_aQ().func_72317_d(dir[0], 2.4D, dir[1])).isEmpty()) {
                    twofive = true;
                }

                if (mc.field_71441_e.func_184144_a(mc.field_71439_g, mc.field_71439_g.func_174813_aQ().func_72317_d(dir[0], 2.1D, dir[1])).isEmpty() && !mc.field_71441_e.func_184144_a(mc.field_71439_g, mc.field_71439_g.func_174813_aQ().func_72317_d(dir[0], 1.9D, dir[1])).isEmpty()) {
                    two = true;
                }

                if (mc.field_71441_e.func_184144_a(mc.field_71439_g, mc.field_71439_g.func_174813_aQ().func_72317_d(dir[0], 1.6D, dir[1])).isEmpty() && !mc.field_71441_e.func_184144_a(mc.field_71439_g, mc.field_71439_g.func_174813_aQ().func_72317_d(dir[0], 1.4D, dir[1])).isEmpty()) {
                    onefive = true;
                }

                if (mc.field_71441_e.func_184144_a(mc.field_71439_g, mc.field_71439_g.func_174813_aQ().func_72317_d(dir[0], 1.0D, dir[1])).isEmpty() && !mc.field_71441_e.func_184144_a(mc.field_71439_g, mc.field_71439_g.func_174813_aQ().func_72317_d(dir[0], 0.6D, dir[1])).isEmpty()) {
                    one = true;
                }

                if (mc.field_71439_g.field_70123_F && (mc.field_71439_g.field_191988_bg != 0.0F || mc.field_71439_g.field_70702_br != 0.0F) && mc.field_71439_g.field_70122_E) {
                    double[] twoFiveOffset;
                    int i;
                    if (one && ((Double) this.height.getValue()).doubleValue() >= 1.0D) {
                        twoFiveOffset = new double[]{0.42D, 0.753D};

                        for (i = 0; i < twoFiveOffset.length; ++i) {
                            mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + twoFiveOffset[i], mc.field_71439_g.field_70161_v, mc.field_71439_g.field_70122_E));
                        }

                        if (((Boolean) this.timer.getValue()).booleanValue()) {
                            EntityUtil.setTimer(0.6F);
                        }

                        mc.field_71439_g.func_70107_b(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 1.0D, mc.field_71439_g.field_70161_v);
                        this.ticks = 1;
                    }

                    if (onefive && ((Double) this.height.getValue()).doubleValue() >= 1.5D) {
                        twoFiveOffset = new double[]{0.42D, 0.75D, 1.0D, 1.16D, 1.23D, 1.2D};

                        for (i = 0; i < twoFiveOffset.length; ++i) {
                            mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + twoFiveOffset[i], mc.field_71439_g.field_70161_v, mc.field_71439_g.field_70122_E));
                        }

                        if (((Boolean) this.timer.getValue()).booleanValue()) {
                            EntityUtil.setTimer(0.35F);
                        }

                        mc.field_71439_g.func_70107_b(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 1.5D, mc.field_71439_g.field_70161_v);
                        this.ticks = 1;
                    }

                    if (two && ((Double) this.height.getValue()).doubleValue() >= 2.0D) {
                        twoFiveOffset = new double[]{0.42D, 0.78D, 0.63D, 0.51D, 0.9D, 1.21D, 1.45D, 1.43D};

                        for (i = 0; i < twoFiveOffset.length; ++i) {
                            mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + twoFiveOffset[i], mc.field_71439_g.field_70161_v, mc.field_71439_g.field_70122_E));
                        }

                        if (((Boolean) this.timer.getValue()).booleanValue()) {
                            EntityUtil.setTimer(0.25F);
                        }

                        mc.field_71439_g.func_70107_b(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 2.0D, mc.field_71439_g.field_70161_v);
                        this.ticks = 2;
                    }

                    if (twofive && ((Double) this.height.getValue()).doubleValue() >= 2.5D) {
                        twoFiveOffset = new double[]{0.425D, 0.821D, 0.699D, 0.599D, 1.022D, 1.372D, 1.652D, 1.869D, 2.019D, 1.907D};

                        for (i = 0; i < twoFiveOffset.length; ++i) {
                            mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + twoFiveOffset[i], mc.field_71439_g.field_70161_v, mc.field_71439_g.field_70122_E));
                        }

                        if (((Boolean) this.timer.getValue()).booleanValue()) {
                            EntityUtil.setTimer(0.15F);
                        }

                        mc.field_71439_g.func_70107_b(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 2.5D, mc.field_71439_g.field_70161_v);
                        this.ticks = 2;
                    }
                }

            }
        }
    }

    public void onDisable() {
        mc.field_71439_g.field_70138_W = 0.6F;
    }
}
