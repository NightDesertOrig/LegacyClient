package me.dev.legacy.modules.movement;

import me.dev.legacy.Legacy;
import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.modules.Module;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.play.client.CPacketPlayer.Position;
import net.minecraft.network.play.client.CPacketPlayer.Rotation;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.network.play.server.SPacketPlayerPosLook.EnumFlags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import java.util.function.Predicate;

public class Phase extends Module {
    Setting debug = this.register(new Setting("Debug", false));
    Setting twodelay = this.register(new Setting("2Delay", true));
    Setting advd = this.register(new Setting("AVD", false));
    Setting EnhancedRots = this.register(new Setting("EnchancedControl", false));
    Setting invert = this.register(new Setting("InvertedYaw", false));
    Setting SendRotPackets = this.register(new Setting("SendRotPackets", true));
    Setting twobeepvp = this.register(new Setting("2b2tpvp", true));
    Setting PUP = this.register(new Setting("PUP", true));
    Setting tickDelay = this.register(new Setting("TickDelay", Integer.valueOf(2), Integer.valueOf(0), Integer.valueOf(40)));
    Setting EnhancedRotsAmount = this.register(new Setting("EnhancedCtrlSpeed", Integer.valueOf(2), Integer.valueOf(0), Integer.valueOf(20)));
    Setting speed = this.register(new Setting("Speed", 6.25D, 0.0D, 6.25D));
    Setting cmode;
    @EventHandler
    private Listener receiveListener;
    KeyBinding left;
    KeyBinding right;
    KeyBinding down;
    KeyBinding up;
    long last;

    public Phase() {
        super("Phase", "Block phase", Module.Category.MOVEMENT, false, false, false);
        this.cmode = this.register(new Setting("ControlMode", Phase.Mode.Rel));
        this.receiveListener = new Listener((event) -> {
            SPacketPlayerPosLook pak;
            if (event.getPacket() instanceof SPacketPlayerPosLook) {
                pak = (SPacketPlayerPosLook) event.getPacket();
                pak.field_148936_d = mc.field_71439_g.field_70177_z;
                pak.field_148937_e = mc.field_71439_g.field_70125_A;
            }

            if (event.getPacket() instanceof SPacketPlayerPosLook) {
                pak = (SPacketPlayerPosLook) event.getPacket();
                double dx = Math.abs(pak.func_179834_f().contains(EnumFlags.X) ? pak.field_148940_a : mc.field_71439_g.field_70165_t - pak.field_148940_a);
                double dy = Math.abs(pak.func_179834_f().contains(EnumFlags.Y) ? pak.field_148938_b : mc.field_71439_g.field_70163_u - pak.field_148938_b);
                double dz = Math.abs(pak.func_179834_f().contains(EnumFlags.Z) ? pak.field_148939_c : mc.field_71439_g.field_70161_v - pak.field_148939_c);
                if (dx < 0.001D) {
                    dx = 0.0D;
                }

                if (dz < 0.001D) {
                    dz = 0.0D;
                }

                if ((dx != 0.0D || dy != 0.0D || dz != 0.0D) && ((Boolean) this.debug.getValue()).booleanValue()) {
                    mc.field_71439_g.func_145747_a(new TextComponentString("position pak, dx=" + dx + " dy=" + dy + " dz=" + dz));
                }

                if (pak.field_148936_d != mc.field_71439_g.field_70177_z || pak.field_148937_e != mc.field_71439_g.field_70125_A) {
                    if (((Boolean) this.SendRotPackets.getValue()).booleanValue()) {
                        mc.func_147114_u().func_147297_a(new Rotation(mc.field_71439_g.field_70177_z, mc.field_71439_g.field_70125_A, mc.field_71439_g.field_70122_E));
                    }

                    pak.field_148936_d = mc.field_71439_g.field_70177_z;
                    pak.field_148937_e = mc.field_71439_g.field_70125_A;
                }
            }

        }, new Predicate[0]);
        this.last = 0L;
    }

    public void onUpdate() {
        try {
            mc.field_71439_g.field_70145_X = true;
            if (((Integer) this.tickDelay.getValue()).intValue() > 0 && mc.field_71439_g.field_70173_aa % ((Integer) this.tickDelay.getValue()).intValue() != 0 && ((Boolean) this.twodelay.getValue()).booleanValue()) {
                return;
            }

            int eca = ((Integer) this.EnhancedRotsAmount.getValue()).intValue();
            if (((Boolean) this.EnhancedRots.getValue()).booleanValue() && this.up.func_151470_d()) {
                mc.field_71439_g.field_70125_A -= (float) eca;
            }

            if (((Boolean) this.EnhancedRots.getValue()).booleanValue() && this.down.func_151470_d()) {
                mc.field_71439_g.field_70125_A += (float) eca;
            }

            if (((Boolean) this.EnhancedRots.getValue()).booleanValue() && this.left.func_151470_d()) {
                mc.field_71439_g.field_70177_z -= (float) eca;
            }

            if (((Boolean) this.EnhancedRots.getValue()).booleanValue() && this.right.func_151470_d()) {
                mc.field_71439_g.field_70177_z += (float) eca;
            }

            double yaw = (double) ((mc.field_71439_g.field_70177_z + 90.0F) * (float) (((Boolean) this.invert.getValue()).booleanValue() ? -1 : 1));
            double xDir;
            double zDir;
            if (((Phase.Mode) this.cmode.getValue()).equals("Rel")) {
                double dO_numer = 0.0D;
                double dO_denom = 0.0D;
                if (mc.field_71474_y.field_74370_x.func_151470_d()) {
                    dO_numer -= 90.0D;
                    ++dO_denom;
                }

                if (mc.field_71474_y.field_74366_z.func_151470_d()) {
                    dO_numer += 90.0D;
                    ++dO_denom;
                }

                if (mc.field_71474_y.field_74368_y.func_151470_d()) {
                    dO_numer += 180.0D;
                    ++dO_denom;
                }

                if (mc.field_71474_y.field_74351_w.func_151470_d()) {
                    ++dO_denom;
                }

                if (dO_denom > 0.0D) {
                    yaw += dO_numer / dO_denom % 361.0D;
                }

                if (yaw < 0.0D) {
                    yaw = 360.0D - yaw;
                }

                if (yaw > 360.0D) {
                    yaw %= 361.0D;
                }

                xDir = Math.cos(Math.toRadians(yaw));
                zDir = Math.sin(Math.toRadians(yaw));
            } else {
                xDir = 0.0D;
                zDir = 0.0D;
                xDir += mc.field_71474_y.field_74351_w.func_151470_d() ? 1.0D : 0.0D;
                xDir -= mc.field_71474_y.field_74368_y.func_151470_d() ? 1.0D : 0.0D;
                zDir += mc.field_71474_y.field_74370_x.func_151470_d() ? 1.0D : 0.0D;
                zDir -= mc.field_71474_y.field_74366_z.func_151470_d() ? 1.0D : 0.0D;
            }

            if (mc.field_71474_y.field_74351_w.func_151470_d() || mc.field_71474_y.field_74370_x.func_151470_d() || mc.field_71474_y.field_74366_z.func_151470_d() || mc.field_71474_y.field_74368_y.func_151470_d()) {
                mc.field_71439_g.field_70159_w = xDir * (((Double) this.speed.getValue()).doubleValue() / 100.0D);
                mc.field_71439_g.field_70179_y = zDir * (((Double) this.speed.getValue()).doubleValue() / 100.0D);
            }

            mc.field_71439_g.field_70181_x = 0.0D;
            boolean yes = false;
            if (((Boolean) this.advd.getValue()).booleanValue()) {
                if (this.last + 50L >= System.currentTimeMillis()) {
                    yes = false;
                } else {
                    this.last = System.currentTimeMillis();
                    yes = true;
                }
            }

            mc.field_71439_g.field_70145_X = true;
            if (yes) {
                mc.func_147114_u().func_147297_a(new Position(mc.field_71439_g.field_70165_t + mc.field_71439_g.field_70159_w, mc.field_71439_g.field_70163_u + (mc.field_71439_g.field_70163_u < (((Boolean) this.twobeepvp.getValue()).booleanValue() ? 1.1D : -0.98D) ? ((Double) this.speed.getValue()).doubleValue() / 100.0D : 0.0D) + (mc.field_71474_y.field_74314_A.func_151470_d() ? ((Double) this.speed.getValue()).doubleValue() / 100.0D : 0.0D) - (mc.field_71474_y.field_74311_E.func_151470_d() ? ((Double) this.speed.getValue()).doubleValue() / 100.0D : 0.0D), mc.field_71439_g.field_70161_v + mc.field_71439_g.field_70179_y, false));
            }

            if (((Boolean) this.PUP.getValue()).booleanValue()) {
                mc.field_71439_g.field_70145_X = true;
                mc.field_71439_g.func_70012_b(mc.field_71439_g.field_70165_t + mc.field_71439_g.field_70159_w, mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v + mc.field_71439_g.field_70179_y, mc.field_71439_g.field_70177_z, mc.field_71439_g.field_70125_A);
            }

            mc.field_71439_g.field_70145_X = true;
            if (yes) {
                mc.func_147114_u().func_147297_a(new Position(mc.field_71439_g.field_70165_t + mc.field_71439_g.field_70159_w, mc.field_71439_g.field_70163_u - 42069.0D, mc.field_71439_g.field_70161_v + mc.field_71439_g.field_70179_y, true));
            }

            double dx = 0.0D;
            double dy = 0.0D;
            double dz = 0.0D;
            if (mc.field_71474_y.field_74311_E.func_151470_d()) {
                dy = -0.0625D;
            }

            if (mc.field_71474_y.field_74314_A.func_151470_d()) {
                dy = 0.0625D;
            }

            mc.field_71439_g.func_70012_b(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v, mc.field_71439_g.field_70177_z, mc.field_71439_g.field_70125_A);
            mc.func_147114_u().func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v, false));
        } catch (Exception var15) {
            this.disable();
        }

    }

    public void onEnable() {
        Legacy.getEventManager().subscribe((Object) this);
    }

    public void onDisable() {
        if (mc.field_71439_g != null) {
            mc.field_71439_g.field_70145_X = false;
        }

        Legacy.getEventManager().unsubscribe((Object) this);
    }

    public static BlockPos getPlayerPos() {
        return new BlockPos(Math.floor(mc.field_71439_g.field_70165_t), Math.floor(mc.field_71439_g.field_70163_u), Math.floor(mc.field_71439_g.field_70161_v));
    }

    public static enum Mode {
        Rel,
        Abs;
    }
}
