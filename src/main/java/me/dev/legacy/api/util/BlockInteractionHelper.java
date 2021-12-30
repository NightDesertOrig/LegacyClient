package me.dev.legacy.api.util;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayer.Rotation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlockInteractionHelper {
    public static final List blackList;
    public static final List shulkerList;
    private static final Minecraft mc;

    public static boolean hotbarSlotCheckEmpty(ItemStack stack) {
        return stack != ItemStack.field_190927_a;
    }

    public static boolean blockCheckNonBlock(ItemStack stack) {
        return stack.func_77973_b() instanceof ItemBlock;
    }

    public static void rotate(float yaw, float pitch) {
        Minecraft.func_71410_x().field_71439_g.field_70177_z = yaw;
        Minecraft.func_71410_x().field_71439_g.field_70125_A = pitch;
    }

    public static void rotate(double[] rotations) {
        Minecraft.func_71410_x().field_71439_g.field_70177_z = (float) rotations[0];
        Minecraft.func_71410_x().field_71439_g.field_70125_A = (float) rotations[1];
    }

    public static double[] calculateLookAt(double px, double py, double pz, EntityPlayer me) {
        double dirx = me.field_70165_t - px;
        double diry = me.field_70163_u - py;
        double dirz = me.field_70161_v - pz;
        double len = Math.sqrt(dirx * dirx + diry * diry + dirz * dirz);
        dirx /= len;
        diry /= len;
        dirz /= len;
        double pitch = Math.asin(diry);
        double yaw = Math.atan2(dirz, dirx);
        pitch = pitch * 180.0D / 3.141592653589793D;
        yaw = yaw * 180.0D / 3.141592653589793D;
        yaw += 90.0D;
        return new double[]{yaw, pitch};
    }

    public static void lookAtBlock(BlockPos blockToLookAt) {
        rotate(calculateLookAt((double) blockToLookAt.func_177958_n(), (double) blockToLookAt.func_177956_o(), (double) blockToLookAt.func_177952_p(), Minecraft.func_71410_x().field_71439_g));
    }

    public static void placeBlockScaffold(BlockPos pos) {
        Vec3d eyesPos = new Vec3d(Wrapper.getPlayer().field_70165_t, Wrapper.getPlayer().field_70163_u + (double) Wrapper.getPlayer().func_70047_e(), Wrapper.getPlayer().field_70161_v);
        EnumFacing[] var2 = EnumFacing.values();
        int var3 = var2.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            EnumFacing side = var2[var4];
            BlockPos neighbor = pos.func_177972_a(side);
            EnumFacing side2 = side.func_176734_d();
            if (canBeClicked(neighbor)) {
                Vec3d hitVec = (new Vec3d(neighbor)).func_72441_c(0.5D, 0.5D, 0.5D).func_178787_e((new Vec3d(side2.func_176730_m())).func_178789_a(0.5F));
                if (eyesPos.func_72436_e(hitVec) <= 18.0625D) {
                    faceVectorPacketInstant(hitVec);
                    processRightClickBlock(neighbor, side2, hitVec);
                    Wrapper.getPlayer().func_184609_a(EnumHand.MAIN_HAND);
                    mc.field_71467_ac = 4;
                    return;
                }
            }
        }

    }

    private static float[] getLegitRotations(Vec3d vec) {
        Vec3d eyesPos = getEyesPos();
        double diffX = vec.field_72450_a - eyesPos.field_72450_a;
        double diffY = vec.field_72448_b - eyesPos.field_72448_b;
        double diffZ = vec.field_72449_c - eyesPos.field_72449_c;
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0F;
        float pitch = (float) (-Math.toDegrees(Math.atan2(diffY, diffXZ)));
        return new float[]{Wrapper.getPlayer().field_70177_z + MathHelper.func_76142_g(yaw - Wrapper.getPlayer().field_70177_z), Wrapper.getPlayer().field_70125_A + MathHelper.func_76142_g(pitch - Wrapper.getPlayer().field_70125_A)};
    }

    private static Vec3d getEyesPos() {
        return new Vec3d(Wrapper.getPlayer().field_70165_t, Wrapper.getPlayer().field_70163_u + (double) Wrapper.getPlayer().func_70047_e(), Wrapper.getPlayer().field_70161_v);
    }

    public static void faceVectorPacketInstant(Vec3d vec) {
        float[] rotations = getLegitRotations(vec);
        Wrapper.getPlayer().field_71174_a.func_147297_a(new Rotation(rotations[0], rotations[1], Wrapper.getPlayer().field_70122_E));
    }

    private static void processRightClickBlock(BlockPos pos, EnumFacing side, Vec3d hitVec) {
        getPlayerController().func_187099_a(Wrapper.getPlayer(), mc.field_71441_e, pos, side, hitVec, EnumHand.MAIN_HAND);
    }

    public static boolean canBeClicked(BlockPos pos) {
        return getBlock(pos).func_176209_a(getState(pos), false);
    }

    private static Block getBlock(BlockPos pos) {
        return getState(pos).func_177230_c();
    }

    private static PlayerControllerMP getPlayerController() {
        return Minecraft.func_71410_x().field_71442_b;
    }

    private static IBlockState getState(BlockPos pos) {
        return Wrapper.getWorld().func_180495_p(pos);
    }

    public static boolean checkForNeighbours(BlockPos blockPos) {
        if (!hasNeighbour(blockPos)) {
            EnumFacing[] var1 = EnumFacing.values();
            int var2 = var1.length;

            for (int var3 = 0; var3 < var2; ++var3) {
                EnumFacing side = var1[var3];
                BlockPos neighbour = blockPos.func_177972_a(side);
                if (hasNeighbour(neighbour)) {
                    return true;
                }
            }

            return false;
        } else {
            return true;
        }
    }

    public static List getSphere(BlockPos loc, float r, int h, boolean hollow, boolean sphere, int plus_y) {
        List circleblocks = new ArrayList();
        int cx = loc.func_177958_n();
        int cy = loc.func_177956_o();
        int cz = loc.func_177952_p();

        for (int x = cx - (int) r; (float) x <= (float) cx + r; ++x) {
            for (int z = cz - (int) r; (float) z <= (float) cz + r; ++z) {
                for (int y = sphere ? cy - (int) r : cy; (float) y < (sphere ? (float) cy + r : (float) (cy + h)); ++y) {
                    double dist = (double) ((cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0));
                    if (dist < (double) (r * r) && (!hollow || dist >= (double) ((r - 1.0F) * (r - 1.0F)))) {
                        BlockPos l = new BlockPos(x, y + plus_y, z);
                        circleblocks.add(l);
                    }
                }
            }
        }

        return circleblocks;
    }

    public static List getCircle(BlockPos loc, int y, float r, boolean hollow) {
        List circleblocks = new ArrayList();
        int cx = loc.func_177958_n();
        int cz = loc.func_177952_p();

        for (int x = cx - (int) r; (float) x <= (float) cx + r; ++x) {
            for (int z = cz - (int) r; (float) z <= (float) cz + r; ++z) {
                double dist = (double) ((cx - x) * (cx - x) + (cz - z) * (cz - z));
                if (dist < (double) (r * r) && (!hollow || dist >= (double) ((r - 1.0F) * (r - 1.0F)))) {
                    BlockPos l = new BlockPos(x, y, z);
                    circleblocks.add(l);
                }
            }
        }

        return circleblocks;
    }

    private static boolean hasNeighbour(BlockPos blockPos) {
        EnumFacing[] var1 = EnumFacing.values();
        int var2 = var1.length;

        for (int var3 = 0; var3 < var2; ++var3) {
            EnumFacing side = var1[var3];
            BlockPos neighbour = blockPos.func_177972_a(side);
            if (!Wrapper.getWorld().func_180495_p(neighbour).func_185904_a().func_76222_j()) {
                return true;
            }
        }

        return false;
    }

    public static EnumFacing getPlaceableSide(BlockPos pos) {
        EnumFacing[] var1 = EnumFacing.values();
        int var2 = var1.length;

        for (int var3 = 0; var3 < var2; ++var3) {
            EnumFacing side = var1[var3];
            BlockPos neighbour = pos.func_177972_a(side);
            if (mc.field_71441_e.func_180495_p(neighbour).func_177230_c().func_176209_a(mc.field_71441_e.func_180495_p(neighbour), false)) {
                IBlockState blockState = mc.field_71441_e.func_180495_p(neighbour);
                if (!blockState.func_185904_a().func_76222_j()) {
                    return side;
                }
            }
        }

        return null;
    }

    public static float[] getDirectionToBlock(int var0, int var1, int var2, EnumFacing var3) {
        EntityEgg var4 = new EntityEgg(mc.field_71441_e);
        var4.field_70165_t = (double) var0 + 0.5D;
        var4.field_70163_u = (double) var1 + 0.5D;
        var4.field_70161_v = (double) var2 + 0.5D;
        var4.field_70165_t += (double) var3.func_176730_m().func_177958_n() * 0.25D;
        var4.field_70163_u += (double) var3.func_176730_m().func_177956_o() * 0.25D;
        var4.field_70161_v += (double) var3.func_176730_m().func_177952_p() * 0.25D;
        return getDirectionToEntity(var4);
    }

    private static float[] getDirectionToEntity(Entity var0) {
        return new float[]{getYaw(var0) + mc.field_71439_g.field_70177_z, getPitch(var0) + mc.field_71439_g.field_70125_A};
    }

    public static float[] getRotationNeededForBlock(EntityPlayer paramEntityPlayer, BlockPos pos) {
        double d1 = (double) pos.func_177958_n() - paramEntityPlayer.field_70165_t;
        double d2 = (double) pos.func_177956_o() + 0.5D - (paramEntityPlayer.field_70163_u + (double) paramEntityPlayer.func_70047_e());
        double d3 = (double) pos.func_177952_p() - paramEntityPlayer.field_70161_v;
        double d4 = Math.sqrt(d1 * d1 + d3 * d3);
        float f1 = (float) (Math.atan2(d3, d1) * 180.0D / 3.141592653589793D) - 90.0F;
        float f2 = (float) (-(Math.atan2(d2, d4) * 180.0D / 3.141592653589793D));
        return new float[]{f1, f2};
    }

    public static float getYaw(Entity var0) {
        double var = var0.field_70165_t - mc.field_71439_g.field_70165_t;
        double var2 = var0.field_70161_v - mc.field_71439_g.field_70161_v;
        double var3;
        if (var2 < 0.0D && var < 0.0D) {
            var3 = 90.0D + Math.toDegrees(Math.atan(var2 / var));
        } else if (var2 < 0.0D && var > 0.0D) {
            var3 = -90.0D + Math.toDegrees(Math.atan(var2 / var));
        } else {
            var3 = Math.toDegrees(-Math.atan(var / var2));
        }

        return MathHelper.func_76142_g(-(mc.field_71439_g.field_70177_z - (float) var3));
    }

    private static float wrapAngleTo180(float angle) {
        for (angle %= 360.0F; angle >= 180.0F; angle -= 360.0F) {
            ;
        }

        while (angle < -180.0F) {
            angle += 360.0F;
        }

        return angle;
    }

    public static float getPitch(Entity var0) {
        double var = var0.field_70165_t - mc.field_71439_g.field_70165_t;
        double var2 = var0.field_70161_v - mc.field_71439_g.field_70161_v;
        double var3 = var0.field_70163_u - 1.6D + (double) var0.func_70047_e() - mc.field_71439_g.field_70163_u;
        double var4 = (double) MathHelper.func_76133_a(var * var + var2 * var2);
        double var5 = -Math.toDegrees(Math.atan(var3 / var4));
        return -MathHelper.func_76142_g(mc.field_71439_g.field_70125_A - (float) var5);
    }

    static {
        blackList = Arrays.asList(Blocks.field_150477_bB, Blocks.field_150486_ae, Blocks.field_150447_bR, Blocks.field_150462_ai, Blocks.field_150467_bQ, Blocks.field_150382_bo, Blocks.field_150438_bZ, Blocks.field_150409_cd, Blocks.field_150367_z);
        shulkerList = Arrays.asList(Blocks.field_190977_dl, Blocks.field_190978_dm, Blocks.field_190979_dn, Blocks.field_190980_do, Blocks.field_190981_dp, Blocks.field_190982_dq, Blocks.field_190983_dr, Blocks.field_190984_ds, Blocks.field_190985_dt, Blocks.field_190986_du, Blocks.field_190987_dv, Blocks.field_190988_dw, Blocks.field_190989_dx, Blocks.field_190990_dy, Blocks.field_190991_dz, Blocks.field_190975_dA);
        mc = Minecraft.func_71410_x();
    }
}
