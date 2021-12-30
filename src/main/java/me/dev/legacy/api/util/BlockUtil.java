package me.dev.legacy.api.util;

import com.google.common.util.concurrent.AtomicDouble;
import me.dev.legacy.Legacy;
import me.dev.legacy.impl.command.Command;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraft.network.play.client.CPacketPlayer.Rotation;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.Chunk;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class BlockUtil implements Util {
    public static final List blackList;
    public static final List shulkerList;
    public static final List unSafeBlocks;
    public static List unSolidBlocks;
    public static List emptyBlocks;

    public static List getBlockSphere(float breakRange, Class clazz) {
        NonNullList positions = NonNullList.func_191196_a();
        positions.addAll((Collection) getSphere(EntityUtil.getPlayerPos(mc.field_71439_g), breakRange, (int) breakRange, false, true, 0).stream().filter((pos) -> {
            return clazz.isInstance(mc.field_71441_e.func_180495_p(pos).func_177230_c());
        }).collect(Collectors.toList()));
        return positions;
    }

    public static void placeBlockScaffold(BlockPos pos) {
        Vec3d eyesPos = new Vec3d(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + (double) mc.field_71439_g.func_70047_e(), mc.field_71439_g.field_70161_v);
        EnumFacing[] var2 = EnumFacing.values();
        int var3 = var2.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            EnumFacing side = var2[var4];
            BlockPos neighbor = pos.func_177972_a(side);
            EnumFacing side2 = side.func_176734_d();
            Vec3d hitVec;
            if (canBeClicked(neighbor) && eyesPos.func_72436_e(hitVec = (new Vec3d(neighbor)).func_72441_c(0.5D, 0.5D, 0.5D).func_178787_e((new Vec3d(side2.func_176730_m())).func_186678_a(0.5D))) <= 18.0625D) {
                HoleFillUtil.faceVectorPacketInstant(hitVec);
                HoleFillUtil.processRightClickBlock(neighbor, side2, hitVec);
                mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
                mc.field_71467_ac = 4;
                return;
            }
        }

    }

    public static double getNearestBlockBelow() {
        for (double y = mc.field_71439_g.field_70163_u; y > 0.0D; y -= 0.001D) {
            if (!(mc.field_71441_e.func_180495_p(new BlockPos(mc.field_71439_g.field_70165_t, y, mc.field_71439_g.field_70161_v)).func_177230_c() instanceof BlockSlab) && mc.field_71441_e.func_180495_p(new BlockPos(mc.field_71439_g.field_70165_t, y, mc.field_71439_g.field_70161_v)).func_177230_c().func_176223_P().func_185890_d(mc.field_71441_e, new BlockPos(0, 0, 0)) != null) {
                return y;
            }
        }

        return -1.0D;
    }

    public static void faceVectorPacketInstant(Vec3d var0) {
        float[] var1 = RotationUtil.getLegitRotations(var0);
        Wrapper.getPlayer().field_71174_a.func_147297_a(new Rotation(var1[0], var1[1], Wrapper.getPlayer().field_70122_E));
    }

    public static boolean isInHole() {
        BlockPos blockPos = new BlockPos(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v);
        IBlockState blockState = mc.field_71441_e.func_180495_p(blockPos);
        return isBlockValid(blockState, blockPos);
    }

    public static boolean isBlockValid(IBlockState blockState, BlockPos blockPos) {
        if (blockState.func_177230_c() != Blocks.field_150350_a) {
            return false;
        } else if (mc.field_71439_g.func_174818_b(blockPos) < 1.0D) {
            return false;
        } else if (mc.field_71441_e.func_180495_p(blockPos.func_177984_a()).func_177230_c() != Blocks.field_150350_a) {
            return false;
        } else if (mc.field_71441_e.func_180495_p(blockPos.func_177981_b(2)).func_177230_c() != Blocks.field_150350_a) {
            return false;
        } else {
            return isBedrockHole(blockPos) || isObbyHole(blockPos) || isBothHole(blockPos) || isElseHole(blockPos);
        }
    }

    public static boolean isObbyHole(BlockPos blockPos) {
        BlockPos[] var1 = getTouchingBlocks(blockPos);
        int var2 = var1.length;

        for (int var3 = 0; var3 < var2; ++var3) {
            BlockPos pos = var1[var3];
            IBlockState touchingState = mc.field_71441_e.func_180495_p(pos);
            if (touchingState.func_177230_c() == Blocks.field_150350_a || touchingState.func_177230_c() != Blocks.field_150343_Z) {
                return false;
            }
        }

        return true;
    }

    public static boolean isBedrockHole(BlockPos blockPos) {
        BlockPos[] var1 = getTouchingBlocks(blockPos);
        int var2 = var1.length;

        for (int var3 = 0; var3 < var2; ++var3) {
            BlockPos pos = var1[var3];
            IBlockState touchingState = mc.field_71441_e.func_180495_p(pos);
            if (touchingState.func_177230_c() == Blocks.field_150350_a || touchingState.func_177230_c() != Blocks.field_150357_h) {
                return false;
            }
        }

        return true;
    }

    public static boolean isBothHole(BlockPos blockPos) {
        BlockPos[] var1 = getTouchingBlocks(blockPos);
        int var2 = var1.length;

        for (int var3 = 0; var3 < var2; ++var3) {
            BlockPos pos = var1[var3];
            IBlockState touchingState = mc.field_71441_e.func_180495_p(pos);
            if (touchingState.func_177230_c() == Blocks.field_150350_a || touchingState.func_177230_c() != Blocks.field_150357_h && touchingState.func_177230_c() != Blocks.field_150343_Z) {
                return false;
            }
        }

        return true;
    }

    public static boolean isElseHole(BlockPos blockPos) {
        BlockPos[] var1 = getTouchingBlocks(blockPos);
        int var2 = var1.length;

        for (int var3 = 0; var3 < var2; ++var3) {
            BlockPos pos = var1[var3];
            IBlockState touchingState = mc.field_71441_e.func_180495_p(pos);
            if (touchingState.func_177230_c() == Blocks.field_150350_a || !touchingState.func_185913_b()) {
                return false;
            }
        }

        return true;
    }

    public static BlockPos[] getTouchingBlocks(BlockPos blockPos) {
        return new BlockPos[]{blockPos.func_177978_c(), blockPos.func_177968_d(), blockPos.func_177974_f(), blockPos.func_177976_e(), blockPos.func_177977_b()};
    }

    public static boolean canPlaceCrystal(BlockPos pos, boolean checkSecond) {
        Chunk chunk = mc.field_71441_e.func_175726_f(pos);
        Block block = chunk.func_177435_g(pos).func_177230_c();
        if (block != Blocks.field_150357_h && block != Blocks.field_150343_Z) {
            return false;
        } else {
            BlockPos boost = pos.func_177967_a(EnumFacing.UP, 1);
            return chunk.func_177435_g(boost).func_177230_c() == Blocks.field_150350_a && chunk.func_177435_g(pos.func_177967_a(EnumFacing.UP, 2)).func_177230_c() == Blocks.field_150350_a ? mc.field_71441_e.func_175647_a(Entity.class, new AxisAlignedBB((double) boost.func_177958_n(), (double) boost.func_177956_o(), (double) boost.func_177952_p(), (double) (boost.func_177958_n() + 1), (double) (boost.func_177956_o() + (checkSecond ? 2 : 1)), (double) (boost.func_177952_p() + 1)), (e) -> {
                return !(e instanceof EntityEnderCrystal);
            }).isEmpty() : false;
        }
    }

    public static boolean isBlockEmpty(BlockPos pos) {
        try {
            if (emptyBlocks.contains(mc.field_71441_e.func_180495_p(pos).func_177230_c())) {
                AxisAlignedBB box = new AxisAlignedBB(pos);
                Iterator entityIter = mc.field_71441_e.field_72996_f.iterator();

                Entity e;
                do {
                    do {
                        if (!entityIter.hasNext()) {
                            return true;
                        }

                        e = (Entity) entityIter.next();
                    } while (!(e instanceof EntityLivingBase));
                } while (!box.func_72326_a(e.func_174813_aQ()));
            }
        } catch (Exception var4) {
            ;
        }

        return false;
    }

    public static boolean canPlaceBlock(BlockPos pos) {
        if (isBlockEmpty(pos)) {
            EnumFacing[] facings = EnumFacing.values();
            EnumFacing[] var2 = facings;
            int var3 = facings.length;

            for (int var4 = 0; var4 < var3; ++var4) {
                EnumFacing f = var2[var4];
                if (!emptyBlocks.contains(mc.field_71441_e.func_180495_p(pos.func_177972_a(f)).func_177230_c()) && mc.field_71439_g.func_174824_e(mc.func_184121_ak()).func_72438_d(new Vec3d((double) pos.func_177958_n() + 0.5D + (double) f.func_82601_c() * 0.5D, (double) pos.func_177956_o() + 0.5D + (double) f.func_96559_d() * 0.5D, (double) pos.func_177952_p() + 0.5D + (double) f.func_82599_e() * 0.5D)) <= 4.25D) {
                    return true;
                }
            }
        }

        return false;
    }

    public static List getPossibleSides(BlockPos pos) {
        ArrayList facings = new ArrayList();
        EnumFacing[] var2 = EnumFacing.values();
        int var3 = var2.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            EnumFacing side = var2[var4];
            BlockPos neighbour = pos.func_177972_a(side);
            if (mc.field_71441_e.func_180495_p(neighbour).func_177230_c().func_176209_a(mc.field_71441_e.func_180495_p(neighbour), false) && !mc.field_71441_e.func_180495_p(neighbour).func_185904_a().func_76222_j()) {
                facings.add(side);
            }
        }

        return facings;
    }

    public static EnumFacing getFirstFacing(BlockPos pos) {
        Iterator iterator = getPossibleSides(pos).iterator();
        if (iterator.hasNext()) {
            EnumFacing facing = (EnumFacing) iterator.next();
            return facing;
        } else {
            return null;
        }
    }

    public static EnumFacing getRayTraceFacing(BlockPos pos) {
        RayTraceResult result = mc.field_71441_e.func_72933_a(new Vec3d(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + (double) mc.field_71439_g.func_70047_e(), mc.field_71439_g.field_70161_v), new Vec3d((double) pos.func_177958_n() + 0.5D, (double) pos.func_177958_n() - 0.5D, (double) pos.func_177958_n() + 0.5D));
        return result != null && result.field_178784_b != null ? result.field_178784_b : EnumFacing.UP;
    }

    public static int isPositionPlaceable(BlockPos pos, boolean rayTrace) {
        return isPositionPlaceable(pos, rayTrace, true);
    }

    public static int isPositionPlaceable(BlockPos pos, boolean rayTrace, boolean entityCheck) {
        Block block = mc.field_71441_e.func_180495_p(pos).func_177230_c();
        if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid) && !(block instanceof BlockTallGrass) && !(block instanceof BlockFire) && !(block instanceof BlockDeadBush) && !(block instanceof BlockSnow)) {
            return 0;
        } else if (!rayTracePlaceCheck(pos, rayTrace, 0.0F)) {
            return -1;
        } else {
            Iterator var4;
            if (entityCheck) {
                var4 = mc.field_71441_e.func_72872_a(Entity.class, new AxisAlignedBB(pos)).iterator();

                while (var4.hasNext()) {
                    Entity entity = (Entity) var4.next();
                    if (!(entity instanceof EntityItem) && !(entity instanceof EntityXPOrb)) {
                        return 1;
                    }
                }
            }

            var4 = getPossibleSides(pos).iterator();

            EnumFacing side;
            do {
                if (!var4.hasNext()) {
                    return 2;
                }

                side = (EnumFacing) var4.next();
            } while (!canBeClicked(pos.func_177972_a(side)));

            return 3;
        }
    }

    public static void rightClickBlock(BlockPos pos, Vec3d vec, EnumHand hand, EnumFacing direction, boolean packet) {
        if (packet) {
            float f = (float) (vec.field_72450_a - (double) pos.func_177958_n());
            float f1 = (float) (vec.field_72448_b - (double) pos.func_177956_o());
            float f2 = (float) (vec.field_72449_c - (double) pos.func_177952_p());
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerTryUseItemOnBlock(pos, direction, hand, f, f1, f2));
        } else {
            mc.field_71442_b.func_187099_a(mc.field_71439_g, mc.field_71441_e, pos, direction, vec, hand);
        }

        mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
        mc.field_71467_ac = 4;
    }

    public static void rightClickBlockLegit(BlockPos pos, float range, boolean rotate, EnumHand hand, AtomicDouble Yaw, AtomicDouble Pitch, AtomicBoolean rotating) {
        Vec3d eyesPos = RotationUtil.getEyesPos();
        Vec3d posVec = (new Vec3d(pos)).func_72441_c(0.5D, 0.5D, 0.5D);
        double distanceSqPosVec = eyesPos.func_72436_e(posVec);
        EnumFacing[] var11 = EnumFacing.values();
        int var12 = var11.length;

        for (int var13 = 0; var13 < var12; ++var13) {
            EnumFacing side = var11[var13];
            Vec3d hitVec = posVec.func_178787_e((new Vec3d(side.func_176730_m())).func_186678_a(0.5D));
            double distanceSqHitVec = eyesPos.func_72436_e(hitVec);
            if (distanceSqHitVec <= MathUtil.square((double) range) && distanceSqHitVec < distanceSqPosVec && mc.field_71441_e.func_147447_a(eyesPos, hitVec, false, true, false) == null) {
                if (rotate) {
                    float[] rotations = RotationUtil.getLegitRotations(hitVec);
                    Yaw.set((double) rotations[0]);
                    Pitch.set((double) rotations[1]);
                    rotating.set(true);
                }

                mc.field_71442_b.func_187099_a(mc.field_71439_g, mc.field_71441_e, pos, side, hitVec, hand);
                mc.field_71439_g.func_184609_a(hand);
                mc.field_71467_ac = 4;
                break;
            }
        }

    }

    public static boolean placeBlock(BlockPos pos, EnumHand hand, boolean rotate, boolean packet, boolean isSneaking) {
        boolean sneaking = false;
        EnumFacing side = getFirstFacing(pos);
        if (side == null) {
            return isSneaking;
        } else {
            BlockPos neighbour = pos.func_177972_a(side);
            EnumFacing opposite = side.func_176734_d();
            Vec3d hitVec = (new Vec3d(neighbour)).func_72441_c(0.5D, 0.5D, 0.5D).func_178787_e((new Vec3d(opposite.func_176730_m())).func_186678_a(0.5D));
            Block neighbourBlock = mc.field_71441_e.func_180495_p(neighbour).func_177230_c();
            if (!mc.field_71439_g.func_70093_af() && (blackList.contains(neighbourBlock) || shulkerList.contains(neighbourBlock))) {
                mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.START_SNEAKING));
                mc.field_71439_g.func_70095_a(true);
                sneaking = true;
            }

            if (rotate) {
                RotationUtil.faceVector(hitVec, true);
            }

            rightClickBlock(neighbour, hitVec, hand, opposite, packet);
            mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
            mc.field_71467_ac = 4;
            return sneaking || isSneaking;
        }
    }

    public static boolean placeBlockSmartRotate(BlockPos pos, EnumHand hand, boolean rotate, boolean packet, boolean isSneaking) {
        boolean sneaking = false;
        EnumFacing side = getFirstFacing(pos);
        Command.sendMessage(side.toString());
        if (side == null) {
            return isSneaking;
        } else {
            BlockPos neighbour = pos.func_177972_a(side);
            EnumFacing opposite = side.func_176734_d();
            Vec3d hitVec = (new Vec3d(neighbour)).func_72441_c(0.5D, 0.5D, 0.5D).func_178787_e((new Vec3d(opposite.func_176730_m())).func_186678_a(0.5D));
            Block neighbourBlock = mc.field_71441_e.func_180495_p(neighbour).func_177230_c();
            if (!mc.field_71439_g.func_70093_af() && (blackList.contains(neighbourBlock) || shulkerList.contains(neighbourBlock))) {
                mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.START_SNEAKING));
                sneaking = true;
            }

            if (rotate) {
                Legacy.rotationManager.lookAtVec3d(hitVec);
            }

            rightClickBlock(neighbour, hitVec, hand, opposite, packet);
            mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
            mc.field_71467_ac = 4;
            return sneaking || isSneaking;
        }
    }

    public static void placeBlockStopSneaking(BlockPos pos, EnumHand hand, boolean rotate, boolean packet, boolean isSneaking) {
        boolean sneaking = placeBlockSmartRotate(pos, hand, rotate, packet, isSneaking);
        if (!isSneaking && sneaking) {
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.STOP_SNEAKING));
        }

    }

    public static Vec3d[] getHelpingBlocks(Vec3d vec3d) {
        return new Vec3d[]{new Vec3d(vec3d.field_72450_a, vec3d.field_72448_b - 1.0D, vec3d.field_72449_c), new Vec3d(vec3d.field_72450_a != 0.0D ? vec3d.field_72450_a * 2.0D : vec3d.field_72450_a, vec3d.field_72448_b, vec3d.field_72450_a != 0.0D ? vec3d.field_72449_c : vec3d.field_72449_c * 2.0D), new Vec3d(vec3d.field_72450_a == 0.0D ? vec3d.field_72450_a + 1.0D : vec3d.field_72450_a, vec3d.field_72448_b, vec3d.field_72450_a == 0.0D ? vec3d.field_72449_c : vec3d.field_72449_c + 1.0D), new Vec3d(vec3d.field_72450_a == 0.0D ? vec3d.field_72450_a - 1.0D : vec3d.field_72450_a, vec3d.field_72448_b, vec3d.field_72450_a == 0.0D ? vec3d.field_72449_c : vec3d.field_72449_c - 1.0D), new Vec3d(vec3d.field_72450_a, vec3d.field_72448_b + 1.0D, vec3d.field_72449_c)};
    }

    public static List possiblePlacePositions(float placeRange) {
        NonNullList positions = NonNullList.func_191196_a();
        positions.addAll((Collection) getSphere(EntityUtil.getPlayerPos(mc.field_71439_g), placeRange, (int) placeRange, false, true, 0).stream().filter(BlockUtil::canPlaceCrystal).collect(Collectors.toList()));
        return positions;
    }

    public static List getSphere(BlockPos pos, float r, int h, boolean hollow, boolean sphere, int plus_y) {
        ArrayList circleblocks = new ArrayList();
        int cx = pos.func_177958_n();
        int cy = pos.func_177956_o();
        int cz = pos.func_177952_p();

        for (int x = cx - (int) r; (float) x <= (float) cx + r; ++x) {
            for (int z = cz - (int) r; (float) z <= (float) cz + r; ++z) {
                int y = sphere ? cy - (int) r : cy;

                while (true) {
                    float f = (float) y;
                    float f2 = sphere ? (float) cy + r : (float) (cy + h);
                    if (f >= f2) {
                        break;
                    }

                    double dist = (double) ((cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0));
                    if (dist < (double) (r * r) && (!hollow || dist >= (double) ((r - 1.0F) * (r - 1.0F)))) {
                        BlockPos l = new BlockPos(x, y + plus_y, z);
                        circleblocks.add(l);
                    }

                    ++y;
                }
            }
        }

        return circleblocks;
    }

    public static boolean canPlaceCrystal(BlockPos blockPos) {
        BlockPos boost = blockPos.func_177982_a(0, 1, 0);
        BlockPos boost2 = blockPos.func_177982_a(0, 2, 0);

        try {
            return (mc.field_71441_e.func_180495_p(blockPos).func_177230_c() == Blocks.field_150357_h || mc.field_71441_e.func_180495_p(blockPos).func_177230_c() == Blocks.field_150343_Z) && mc.field_71441_e.func_180495_p(boost).func_177230_c() == Blocks.field_150350_a && mc.field_71441_e.func_180495_p(boost2).func_177230_c() == Blocks.field_150350_a && mc.field_71441_e.func_72872_a(Entity.class, new AxisAlignedBB(boost)).isEmpty() && mc.field_71441_e.func_72872_a(Entity.class, new AxisAlignedBB(boost2)).isEmpty();
        } catch (Exception var4) {
            return false;
        }
    }

    public static List possiblePlacePositions(float placeRange, boolean specialEntityCheck, boolean oneDot15) {
        NonNullList positions = NonNullList.func_191196_a();
        positions.addAll((Collection) getSphere(EntityUtil.getPlayerPos(mc.field_71439_g), placeRange, (int) placeRange, false, true, 0).stream().filter((pos) -> {
            return canPlaceCrystal(pos, specialEntityCheck, oneDot15);
        }).collect(Collectors.toList()));
        return positions;
    }

    public static boolean canPlaceCrystal(BlockPos blockPos, boolean specialEntityCheck, boolean oneDot15) {
        BlockPos boost = blockPos.func_177982_a(0, 1, 0);
        BlockPos boost2 = blockPos.func_177982_a(0, 2, 0);

        try {
            if (mc.field_71441_e.func_180495_p(blockPos).func_177230_c() != Blocks.field_150357_h && mc.field_71441_e.func_180495_p(blockPos).func_177230_c() != Blocks.field_150343_Z) {
                return false;
            } else if ((oneDot15 || mc.field_71441_e.func_180495_p(boost2).func_177230_c() == Blocks.field_150350_a) && mc.field_71441_e.func_180495_p(boost).func_177230_c() == Blocks.field_150350_a) {
                Iterator var5 = mc.field_71441_e.func_72872_a(Entity.class, new AxisAlignedBB(boost)).iterator();

                Entity entity;
                do {
                    do {
                        if (!var5.hasNext()) {
                            if (!oneDot15) {
                                var5 = mc.field_71441_e.func_72872_a(Entity.class, new AxisAlignedBB(boost2)).iterator();

                                while (var5.hasNext()) {
                                    entity = (Entity) var5.next();
                                    if (!entity.field_70128_L && (!specialEntityCheck || !(entity instanceof EntityEnderCrystal))) {
                                        return false;
                                    }
                                }
                            }

                            return true;
                        }

                        entity = (Entity) var5.next();
                    } while (entity.field_70128_L);
                } while (specialEntityCheck && entity instanceof EntityEnderCrystal);

                return false;
            } else {
                return false;
            }
        } catch (Exception var7) {
            return false;
        }
    }

    public static boolean canBeClicked(BlockPos pos) {
        return getBlock(pos).func_176209_a(getState(pos), false);
    }

    private static Block getBlock(BlockPos pos) {
        return getState(pos).func_177230_c();
    }

    private static IBlockState getState(BlockPos pos) {
        return mc.field_71441_e.func_180495_p(pos);
    }

    public static boolean isBlockAboveEntitySolid(Entity entity) {
        if (entity != null) {
            BlockPos pos = new BlockPos(entity.field_70165_t, entity.field_70163_u + 2.0D, entity.field_70161_v);
            return isBlockSolid(pos);
        } else {
            return false;
        }
    }

    public static void debugPos(String message, BlockPos pos) {
        Command.sendMessage(message + pos.func_177958_n() + "x, " + pos.func_177956_o() + "y, " + pos.func_177952_p() + "z");
    }

    public static void placeCrystalOnBlock(BlockPos pos, EnumHand hand, boolean swing, boolean exactHand) {
        RayTraceResult result = mc.field_71441_e.func_72933_a(new Vec3d(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + (double) mc.field_71439_g.func_70047_e(), mc.field_71439_g.field_70161_v), new Vec3d((double) pos.func_177958_n() + 0.5D, (double) pos.func_177956_o() - 0.5D, (double) pos.func_177952_p() + 0.5D));
        EnumFacing facing = result != null && result.field_178784_b != null ? result.field_178784_b : EnumFacing.UP;
        mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerTryUseItemOnBlock(pos, facing, hand, 0.0F, 0.0F, 0.0F));
        if (swing) {
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketAnimation(exactHand ? hand : EnumHand.MAIN_HAND));
        }

    }

    public static BlockPos[] toBlockPos(Vec3d[] vec3ds) {
        BlockPos[] list = new BlockPos[vec3ds.length];

        for (int i = 0; i < vec3ds.length; ++i) {
            list[i] = new BlockPos(vec3ds[i]);
        }

        return list;
    }

    public static Vec3d posToVec3d(BlockPos pos) {
        return new Vec3d(pos);
    }

    public static BlockPos vec3dToPos(Vec3d vec3d) {
        return new BlockPos(vec3d);
    }

    public static Boolean isPosInFov(BlockPos pos) {
        int dirnumber = RotationUtil.getDirection4D();
        if (dirnumber == 0 && (double) pos.func_177952_p() - mc.field_71439_g.func_174791_d().field_72449_c < 0.0D) {
            return false;
        } else if (dirnumber == 1 && (double) pos.func_177958_n() - mc.field_71439_g.func_174791_d().field_72450_a > 0.0D) {
            return false;
        } else {
            return dirnumber == 2 && (double) pos.func_177952_p() - mc.field_71439_g.func_174791_d().field_72449_c > 0.0D ? false : dirnumber != 3 || (double) pos.func_177958_n() - mc.field_71439_g.func_174791_d().field_72450_a >= 0.0D;
        }
    }

    public static boolean isBlockBelowEntitySolid(Entity entity) {
        if (entity != null) {
            BlockPos pos = new BlockPos(entity.field_70165_t, entity.field_70163_u - 1.0D, entity.field_70161_v);
            return isBlockSolid(pos);
        } else {
            return false;
        }
    }

    public static boolean isBlockSolid(BlockPos pos) {
        return !isBlockUnSolid(pos);
    }

    public static boolean isBlockUnSolid(BlockPos pos) {
        return isBlockUnSolid(mc.field_71441_e.func_180495_p(pos).func_177230_c());
    }

    public static boolean isBlockUnSolid(Block block) {
        return unSolidBlocks.contains(block);
    }

    public static boolean isBlockUnSafe(Block block) {
        return unSafeBlocks.contains(block);
    }

    public static Vec3d[] convertVec3ds(Vec3d vec3d, Vec3d[] input) {
        Vec3d[] output = new Vec3d[input.length];

        for (int i = 0; i < input.length; ++i) {
            output[i] = vec3d.func_178787_e(input[i]);
        }

        return output;
    }

    public static Vec3d[] convertVec3ds(EntityPlayer entity, Vec3d[] input) {
        return convertVec3ds(entity.func_174791_d(), input);
    }

    public static boolean canBreak(BlockPos pos) {
        IBlockState blockState = mc.field_71441_e.func_180495_p(pos);
        Block block = blockState.func_177230_c();
        return block.func_176195_g(blockState, mc.field_71441_e, pos) != -1.0F;
    }

    public static boolean isValidBlock(BlockPos pos) {
        Block block = mc.field_71441_e.func_180495_p(pos).func_177230_c();
        return !(block instanceof BlockLiquid) && block.func_149688_o((IBlockState) null) != Material.field_151579_a;
    }

    public static boolean isScaffoldPos(BlockPos pos) {
        return mc.field_71441_e.func_175623_d(pos) || mc.field_71441_e.func_180495_p(pos).func_177230_c() == Blocks.field_150431_aC || mc.field_71441_e.func_180495_p(pos).func_177230_c() == Blocks.field_150329_H || mc.field_71441_e.func_180495_p(pos).func_177230_c() instanceof BlockLiquid;
    }

    public static boolean rayTracePlaceCheck(BlockPos pos, boolean shouldCheck, float height) {
        return !shouldCheck || mc.field_71441_e.func_147447_a(new Vec3d(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + (double) mc.field_71439_g.func_70047_e(), mc.field_71439_g.field_70161_v), new Vec3d((double) pos.func_177958_n(), (double) ((float) pos.func_177956_o() + height), (double) pos.func_177952_p()), false, true, false) == null;
    }

    public static boolean rayTracePlaceCheck(BlockPos pos, boolean shouldCheck) {
        return rayTracePlaceCheck(pos, shouldCheck, 1.0F);
    }

    public static List getSphere(float radius) {
        List sphere = new ArrayList();
        BlockPos pos = new BlockPos(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v);
        int posX = pos.func_177958_n();
        int posY = pos.func_177956_o();
        int posZ = pos.func_177952_p();

        for (int x = posX - (int) radius; (float) x <= (float) posX + radius; ++x) {
            for (int z = posZ - (int) radius; (float) z <= (float) posZ + radius; ++z) {
                for (int y = posY - (int) radius; (float) y < (float) posY + radius; ++y) {
                    if ((float) ((posX - x) * (posX - x) + (posZ - z) * (posZ - z) + (posY - y) * (posY - y)) < radius * radius) {
                        sphere.add(new BlockPos(x, y, z));
                    }
                }
            }
        }

        return sphere;
    }

    public static List getSphereRealth(float radius, boolean ignoreAir) {
        List sphere = new ArrayList();
        BlockPos pos = new BlockPos(mc.field_71439_g.func_174791_d());
        int posX = pos.func_177958_n();
        int posY = pos.func_177956_o();
        int posZ = pos.func_177952_p();
        int radiuss = (int) radius;

        for (int x = posX - radiuss; (float) x <= (float) posX + radius; ++x) {
            for (int z = posZ - radiuss; (float) z <= (float) posZ + radius; ++z) {
                for (int y = posY - radiuss; (float) y < (float) posY + radius; ++y) {
                    if ((float) ((posX - x) * (posX - x) + (posZ - z) * (posZ - z) + (posY - y) * (posY - y)) < radius * radius) {
                        BlockPos position = new BlockPos(x, y, z);
                        if (!ignoreAir || mc.field_71441_e.func_180495_p(position).func_177230_c() != Blocks.field_150350_a) {
                            sphere.add(position);
                        }
                    }
                }
            }
        }

        return sphere;
    }

    public static List getOffsetList(int y, boolean floor) {
        List offsets = new ArrayList(5);
        offsets.add(new Vec3d(-1.0D, (double) y, 0.0D));
        offsets.add(new Vec3d(1.0D, (double) y, 0.0D));
        offsets.add(new Vec3d(0.0D, (double) y, -1.0D));
        offsets.add(new Vec3d(0.0D, (double) y, 1.0D));
        if (floor) {
            offsets.add(new Vec3d(0.0D, (double) (y - 1), 0.0D));
        }

        return offsets;
    }

    public static Vec3d[] getOffsets(int y, boolean floor) {
        List offsets = getOffsetList(y, floor);
        Vec3d[] array = new Vec3d[offsets.size()];
        return (Vec3d[]) offsets.toArray(array);
    }

    public static boolean isSafe(Entity entity, int height, boolean floor) {
        return getUnsafeBlocks(entity, height, floor).size() == 0;
    }

    public static List getUnsafeBlocks(Entity entity, int height, boolean floor) {
        return getUnsafeBlocksFromVec3d(entity.func_174791_d(), height, floor);
    }

    public static List getUnsafeBlocksFromVec3d(Vec3d pos, int height, boolean floor) {
        List vec3ds = new ArrayList(5);
        Vec3d[] var4 = getOffsets(height, floor);
        int var5 = var4.length;

        for (int var6 = 0; var6 < var5; ++var6) {
            Vec3d vector = var4[var6];
            Block block = mc.field_71441_e.func_180495_p((new BlockPos(pos)).func_177963_a(vector.field_72450_a, vector.field_72448_b, vector.field_72449_c)).func_177230_c();
            if (block instanceof BlockAir || block instanceof BlockLiquid || block instanceof BlockTallGrass || block instanceof BlockFire || block instanceof BlockDeadBush || block instanceof BlockSnow) {
                vec3ds.add(vector);
            }
        }

        return vec3ds;
    }

    public static Vec3d[] getUnsafeBlockArray(Entity entity, int height, boolean floor) {
        List list = getUnsafeBlocks(entity, height, floor);
        Vec3d[] array = new Vec3d[list.size()];
        return (Vec3d[]) list.toArray(array);
    }

    public static boolean rayTracePlaceCheck(BlockPos pos) {
        return rayTracePlaceCheck(pos, true);
    }

    public static void placeCrystalOnBlock(BlockPos pos, EnumHand hand, boolean swing) {
        RayTraceResult result = mc.field_71441_e.func_72933_a(new Vec3d(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + (double) mc.field_71439_g.func_70047_e(), mc.field_71439_g.field_70161_v), new Vec3d((double) pos.func_177958_n() + 0.5D, (double) pos.func_177956_o() - 0.5D, (double) pos.func_177952_p() + 0.5D));
        EnumFacing facing = result != null && result.field_178784_b != null ? result.field_178784_b : EnumFacing.UP;
        mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerTryUseItemOnBlock(pos, facing, hand, 0.0F, 0.0F, 0.0F));
        if (swing) {
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketAnimation(hand));
        }

    }

    public static void placeBlock(BlockPos pos) {
        EnumFacing[] var1 = EnumFacing.field_82609_l;
        int var2 = var1.length;

        for (int var3 = 0; var3 < var2; ++var3) {
            EnumFacing side = var1[var3];
            BlockPos neighbor = pos.func_177972_a(side);
            IBlockState neighborState = mc.field_71441_e.func_180495_p(neighbor);
            if (neighborState.func_177230_c().func_176209_a(neighborState, false)) {
                boolean sneak = !PlayerUtil.isShifting() && neighborState.func_177230_c().func_180639_a(mc.field_71441_e, pos, mc.field_71441_e.func_180495_p(pos), mc.field_71439_g, EnumHand.MAIN_HAND, side, 0.5F, 0.5F, 0.5F);
                if (sneak) {
                    mc.func_147114_u().func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.START_SNEAKING));
                }

                mc.func_147114_u().func_147297_a(new CPacketPlayerTryUseItemOnBlock(neighbor, side.func_176734_d(), EnumHand.MAIN_HAND, 0.5F, 0.5F, 0.5F));
                mc.func_147114_u().func_147297_a(new CPacketAnimation(EnumHand.MAIN_HAND));
                if (sneak) {
                    mc.func_147114_u().func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.STOP_SNEAKING));
                }
            }
        }

    }

    static {
        blackList = Arrays.asList(Blocks.field_150477_bB, Blocks.field_150486_ae, Blocks.field_150447_bR, Blocks.field_150462_ai, Blocks.field_150467_bQ, Blocks.field_150382_bo, Blocks.field_150438_bZ, Blocks.field_150409_cd, Blocks.field_150367_z, Blocks.field_150415_aT, Blocks.field_150381_bn);
        shulkerList = Arrays.asList(Blocks.field_190977_dl, Blocks.field_190978_dm, Blocks.field_190979_dn, Blocks.field_190980_do, Blocks.field_190981_dp, Blocks.field_190982_dq, Blocks.field_190983_dr, Blocks.field_190984_ds, Blocks.field_190985_dt, Blocks.field_190986_du, Blocks.field_190987_dv, Blocks.field_190988_dw, Blocks.field_190989_dx, Blocks.field_190990_dy, Blocks.field_190991_dz, Blocks.field_190975_dA);
        unSafeBlocks = Arrays.asList(Blocks.field_150343_Z, Blocks.field_150357_h, Blocks.field_150477_bB, Blocks.field_150467_bQ);
        unSolidBlocks = Arrays.asList(Blocks.field_150356_k, Blocks.field_150457_bL, Blocks.field_150433_aE, Blocks.field_150404_cg, Blocks.field_185764_cQ, Blocks.field_150465_bP, Blocks.field_150457_bL, Blocks.field_150473_bD, Blocks.field_150479_bC, Blocks.field_150471_bO, Blocks.field_150442_at, Blocks.field_150430_aB, Blocks.field_150468_ap, Blocks.field_150441_bU, Blocks.field_150455_bV, Blocks.field_150413_aR, Blocks.field_150416_aS, Blocks.field_150437_az, Blocks.field_150429_aA, Blocks.field_150488_af, Blocks.field_150350_a, Blocks.field_150427_aO, Blocks.field_150384_bq, Blocks.field_150355_j, Blocks.field_150358_i, Blocks.field_150353_l, Blocks.field_150356_k, Blocks.field_150345_g, Blocks.field_150328_O, Blocks.field_150327_N, Blocks.field_150338_P, Blocks.field_150337_Q, Blocks.field_150464_aj, Blocks.field_150459_bM, Blocks.field_150469_bN, Blocks.field_185773_cZ, Blocks.field_150436_aH, Blocks.field_150393_bb, Blocks.field_150394_bc, Blocks.field_150392_bi, Blocks.field_150388_bm, Blocks.field_150375_by, Blocks.field_185766_cS, Blocks.field_185765_cR, Blocks.field_150329_H, Blocks.field_150330_I, Blocks.field_150395_bd, Blocks.field_150480_ab, Blocks.field_150448_aq, Blocks.field_150408_cc, Blocks.field_150319_E, Blocks.field_150318_D, Blocks.field_150478_aa);
        emptyBlocks = Arrays.asList(Blocks.field_150350_a, Blocks.field_150356_k, Blocks.field_150353_l, Blocks.field_150358_i, Blocks.field_150355_j, Blocks.field_150395_bd, Blocks.field_150431_aC, Blocks.field_150329_H, Blocks.field_150480_ab);
    }
}
