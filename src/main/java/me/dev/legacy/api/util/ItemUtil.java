package me.dev.legacy.api.util;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer.Rotation;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ItemUtil {
    public static final Minecraft mc = Minecraft.func_71410_x();

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
            if (!mc.field_71439_g.func_70093_af()) {
                mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.START_SNEAKING));
                mc.field_71439_g.func_70095_a(true);
                sneaking = true;
            }

            if (rotate) {
                faceVector(hitVec, true);
            }

            rightClickBlock(neighbour, hitVec, hand, opposite, packet);
            mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
            mc.field_71467_ac = 4;
            return sneaking || isSneaking;
        }
    }

    public static List getPossibleSides(BlockPos pos) {
        List facings = new ArrayList();
        EnumFacing[] var2 = EnumFacing.values();
        int var3 = var2.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            EnumFacing side = var2[var4];
            BlockPos neighbour = pos.func_177972_a(side);
            if (mc.field_71441_e.func_180495_p(neighbour).func_177230_c().func_176209_a(mc.field_71441_e.func_180495_p(neighbour), false)) {
                IBlockState blockState = mc.field_71441_e.func_180495_p(neighbour);
                if (!blockState.func_185904_a().func_76222_j()) {
                    facings.add(side);
                }
            }
        }

        return facings;
    }

    public static EnumFacing getFirstFacing(BlockPos pos) {
        Iterator var1 = getPossibleSides(pos).iterator();
        if (var1.hasNext()) {
            EnumFacing facing = (EnumFacing) var1.next();
            return facing;
        } else {
            return null;
        }
    }

    public static Vec3d getEyesPos() {
        return new Vec3d(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + (double) mc.field_71439_g.func_70047_e(), mc.field_71439_g.field_70161_v);
    }

    public static float[] getLegitRotations(Vec3d vec) {
        Vec3d eyesPos = getEyesPos();
        double diffX = vec.field_72450_a - eyesPos.field_72450_a;
        double diffY = vec.field_72448_b - eyesPos.field_72448_b;
        double diffZ = vec.field_72449_c - eyesPos.field_72449_c;
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0F;
        float pitch = (float) (-Math.toDegrees(Math.atan2(diffY, diffXZ)));
        return new float[]{mc.field_71439_g.field_70177_z + MathHelper.func_76142_g(yaw - mc.field_71439_g.field_70177_z), mc.field_71439_g.field_70125_A + MathHelper.func_76142_g(pitch - mc.field_71439_g.field_70125_A)};
    }

    public static void faceVector(Vec3d vec, boolean normalizeAngle) {
        float[] rotations = getLegitRotations(vec);
        mc.field_71439_g.field_71174_a.func_147297_a(new Rotation(rotations[0], normalizeAngle ? (float) MathHelper.func_180184_b((int) rotations[1], 360) : rotations[1], mc.field_71439_g.field_70122_E));
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

    public static int findHotbarBlock(Class clazz) {
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = mc.field_71439_g.field_71071_by.func_70301_a(i);
            if (stack != ItemStack.field_190927_a) {
                if (clazz.isInstance(stack.func_77973_b())) {
                    return i;
                }

                if (stack.func_77973_b() instanceof ItemBlock) {
                    Block block = ((ItemBlock) stack.func_77973_b()).func_179223_d();
                    if (clazz.isInstance(block)) {
                        return i;
                    }
                }
            }
        }

        return -1;
    }

    public static void switchToSlot(int slot) {
        mc.field_71439_g.field_71174_a.func_147297_a(new CPacketHeldItemChange(slot));
        mc.field_71439_g.field_71071_by.field_70461_c = slot;
        mc.field_71442_b.func_78765_e();
    }

    public static int getBlockFromHotbar(Block block) {
        int slot = -1;

        for (int i = 8; i >= 0; --i) {
            if (mc.field_71439_g.field_71071_by.func_70301_a(i).func_77973_b() == Item.func_150898_a(block)) {
                slot = i;
                break;
            }
        }

        return slot;
    }

    public static int getItemFromHotbar(Class clazz) {
        int slot = -1;

        for (int i = 8; i >= 0; --i) {
            if (mc.field_71439_g.field_71071_by.func_70301_a(i).func_77973_b().getClass() == clazz) {
                slot = i;
                break;
            }
        }

        return slot;
    }

    public static int getItemFromHotbar(Item item) {
        int slot = -1;

        for (int i = 8; i >= 0; --i) {
            if (mc.field_71439_g.field_71071_by.func_70301_a(i).func_77973_b() == item) {
                slot = i;
                break;
            }
        }

        return slot;
    }

    public static boolean isArmorUnderPercent(EntityPlayer player, float percent) {
        for (int i = 3; i >= 0; --i) {
            ItemStack stack = (ItemStack) player.field_71071_by.field_70460_b.get(i);
            if (getDamageInPercent(stack) < percent) {
                return true;
            }
        }

        return false;
    }

    public static int getRoundedDamage(ItemStack stack) {
        return (int) getDamageInPercent(stack);
    }

    public static float getDamageInPercent(ItemStack stack) {
        float green = ((float) stack.func_77958_k() - (float) stack.func_77952_i()) / (float) stack.func_77958_k();
        float red = 1.0F - green;
        return (float) (100 - (int) (red * 100.0F));
    }
}
