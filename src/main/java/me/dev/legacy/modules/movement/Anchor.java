package me.dev.legacy.modules.movement;

import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.modules.Module;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;

public class Anchor extends Module {
    public Setting pull = this.register(new Setting("Pull", true));
    public Setting pitch = this.register(new Setting("Pitch", Integer.valueOf(60), Integer.valueOf(0), Integer.valueOf(90)));
    private final ArrayList holes = new ArrayList();
    int holeblocks;
    public static boolean AnchorING;
    private Vec3d Center;

    public Anchor() {
        super("Anchor", "Stops all movement if player is above a hole.", Module.Category.MOVEMENT, false, false, false);
        this.Center = Vec3d.field_186680_a;
    }

    public boolean isBlockHole(BlockPos blockpos) {
        this.holeblocks = 0;
        if (mc.field_71441_e.func_180495_p(blockpos.func_177982_a(0, 3, 0)).func_177230_c() == Blocks.field_150350_a) {
            ++this.holeblocks;
        }

        if (mc.field_71441_e.func_180495_p(blockpos.func_177982_a(0, 2, 0)).func_177230_c() == Blocks.field_150350_a) {
            ++this.holeblocks;
        }

        if (mc.field_71441_e.func_180495_p(blockpos.func_177982_a(0, 1, 0)).func_177230_c() == Blocks.field_150350_a) {
            ++this.holeblocks;
        }

        if (mc.field_71441_e.func_180495_p(blockpos.func_177982_a(0, 0, 0)).func_177230_c() == Blocks.field_150350_a) {
            ++this.holeblocks;
        }

        if (mc.field_71441_e.func_180495_p(blockpos.func_177982_a(0, -1, 0)).func_177230_c() == Blocks.field_150343_Z || mc.field_71441_e.func_180495_p(blockpos.func_177982_a(0, -1, 0)).func_177230_c() == Blocks.field_150357_h) {
            ++this.holeblocks;
        }

        if (mc.field_71441_e.func_180495_p(blockpos.func_177982_a(1, 0, 0)).func_177230_c() == Blocks.field_150343_Z || mc.field_71441_e.func_180495_p(blockpos.func_177982_a(1, 0, 0)).func_177230_c() == Blocks.field_150357_h) {
            ++this.holeblocks;
        }

        if (mc.field_71441_e.func_180495_p(blockpos.func_177982_a(-1, 0, 0)).func_177230_c() == Blocks.field_150343_Z || mc.field_71441_e.func_180495_p(blockpos.func_177982_a(-1, 0, 0)).func_177230_c() == Blocks.field_150357_h) {
            ++this.holeblocks;
        }

        if (mc.field_71441_e.func_180495_p(blockpos.func_177982_a(0, 0, 1)).func_177230_c() == Blocks.field_150343_Z || mc.field_71441_e.func_180495_p(blockpos.func_177982_a(0, 0, 1)).func_177230_c() == Blocks.field_150357_h) {
            ++this.holeblocks;
        }

        if (mc.field_71441_e.func_180495_p(blockpos.func_177982_a(0, 0, -1)).func_177230_c() == Blocks.field_150343_Z || mc.field_71441_e.func_180495_p(blockpos.func_177982_a(0, 0, -1)).func_177230_c() == Blocks.field_150357_h) {
            ++this.holeblocks;
        }

        return this.holeblocks >= 9;
    }

    public Vec3d GetCenter(double posX, double posY, double posZ) {
        double x = Math.floor(posX) + 0.5D;
        double y = Math.floor(posY);
        double z = Math.floor(posZ) + 0.5D;
        return new Vec3d(x, y, z);
    }

    @SubscribeEvent
    public void onUpdate() {
        if (mc.field_71441_e != null) {
            if (mc.field_71439_g.field_70163_u >= 0.0D) {
                if (mc.field_71439_g.field_70125_A >= (float) ((Integer) this.pitch.getValue()).intValue()) {
                    if (!this.isBlockHole(this.getPlayerPos().func_177979_c(1)) && !this.isBlockHole(this.getPlayerPos().func_177979_c(2)) && !this.isBlockHole(this.getPlayerPos().func_177979_c(3)) && !this.isBlockHole(this.getPlayerPos().func_177979_c(4))) {
                        AnchorING = false;
                    } else {
                        AnchorING = true;
                        if (!((Boolean) this.pull.getValue()).booleanValue()) {
                            mc.field_71439_g.field_70159_w = 0.0D;
                            mc.field_71439_g.field_70179_y = 0.0D;
                        } else {
                            this.Center = this.GetCenter(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v);
                            double XDiff = Math.abs(this.Center.field_72450_a - mc.field_71439_g.field_70165_t);
                            double ZDiff = Math.abs(this.Center.field_72449_c - mc.field_71439_g.field_70161_v);
                            if (XDiff <= 0.1D && ZDiff <= 0.1D) {
                                this.Center = Vec3d.field_186680_a;
                            } else {
                                double MotionX = this.Center.field_72450_a - mc.field_71439_g.field_70165_t;
                                double MotionZ = this.Center.field_72449_c - mc.field_71439_g.field_70161_v;
                                mc.field_71439_g.field_70159_w = MotionX / 2.0D;
                                mc.field_71439_g.field_70179_y = MotionZ / 2.0D;
                            }
                        }
                    }
                }

            }
        }
    }

    public void onDisable() {
        AnchorING = false;
        this.holeblocks = 0;
    }

    public BlockPos getPlayerPos() {
        return new BlockPos(Math.floor(mc.field_71439_g.field_70165_t), Math.floor(mc.field_71439_g.field_70163_u), Math.floor(mc.field_71439_g.field_70161_v));
    }
}
