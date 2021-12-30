package me.dev.legacy.api.util;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class HoleUtil {
    public static final List holeBlocks = Arrays.asList(new BlockPos(0, -1, 0), new BlockPos(0, 0, -1), new BlockPos(-1, 0, 0), new BlockPos(1, 0, 0), new BlockPos(0, 0, 1));
    private static Minecraft mc = Minecraft.func_71410_x();
    public static final Vec3d[] cityOffsets = new Vec3d[]{new Vec3d(1.0D, 0.0D, 0.0D), new Vec3d(0.0D, 0.0D, 1.0D), new Vec3d(-1.0D, 0.0D, 0.0D), new Vec3d(0.0D, 0.0D, -1.0D)};

    public static boolean isInHole() {
        Vec3d playerPos = CombatUtil.interpolateEntity(mc.field_71439_g);
        BlockPos blockpos = new BlockPos(playerPos.field_72450_a, playerPos.field_72448_b, playerPos.field_72449_c);
        int size = 0;
        Iterator var3 = holeBlocks.iterator();

        while (var3.hasNext()) {
            BlockPos bPos = (BlockPos) var3.next();
            if (CombatUtil.isHard(mc.field_71441_e.func_180495_p(blockpos.func_177971_a(bPos)).func_177230_c())) {
                ++size;
            }
        }

        return size == 5;
    }

    public static HoleUtil.BlockSafety isBlockSafe(Block block) {
        if (block == Blocks.field_150357_h) {
            return HoleUtil.BlockSafety.UNBREAKABLE;
        } else {
            return block != Blocks.field_150343_Z && block != Blocks.field_150477_bB && block != Blocks.field_150467_bQ ? HoleUtil.BlockSafety.BREAKABLE : HoleUtil.BlockSafety.RESISTANT;
        }
    }

    public static HoleUtil.HoleInfo isHole(BlockPos centreBlock, boolean onlyOneWide, boolean ignoreDown) {
        HoleUtil.HoleInfo output = new HoleUtil.HoleInfo();
        HashMap unsafeSides = getUnsafeSides(centreBlock);
        if (unsafeSides.containsKey(HoleUtil.BlockOffset.DOWN) && unsafeSides.remove(HoleUtil.BlockOffset.DOWN, HoleUtil.BlockSafety.BREAKABLE) && !ignoreDown) {
            output.setSafety(HoleUtil.BlockSafety.BREAKABLE);
            return output;
        } else {
            int size = unsafeSides.size();
            unsafeSides.entrySet().removeIf((entry) -> {
                return entry.getValue() == HoleUtil.BlockSafety.RESISTANT;
            });
            if (unsafeSides.size() != size) {
                output.setSafety(HoleUtil.BlockSafety.RESISTANT);
            }

            size = unsafeSides.size();
            if (size == 0) {
                output.setType(HoleUtil.HoleType.SINGLE);
                output.setCentre(new AxisAlignedBB(centreBlock));
                return output;
            } else if (size == 1 && !onlyOneWide) {
                return isDoubleHole(output, centreBlock, (HoleUtil.BlockOffset) unsafeSides.keySet().stream().findFirst().get());
            } else {
                output.setSafety(HoleUtil.BlockSafety.BREAKABLE);
                return output;
            }
        }
    }

    private static HoleUtil.HoleInfo isDoubleHole(HoleUtil.HoleInfo info, BlockPos centreBlock, HoleUtil.BlockOffset weakSide) {
        BlockPos unsafePos = weakSide.offset(centreBlock);
        HashMap unsafeSides = getUnsafeSides(unsafePos);
        int size = unsafeSides.size();
        unsafeSides.entrySet().removeIf((entry) -> {
            return entry.getValue() == HoleUtil.BlockSafety.RESISTANT;
        });
        if (unsafeSides.size() != size) {
            info.setSafety(HoleUtil.BlockSafety.RESISTANT);
        }

        if (unsafeSides.containsKey(HoleUtil.BlockOffset.DOWN)) {
            info.setType(HoleUtil.HoleType.CUSTOM);
            unsafeSides.remove(HoleUtil.BlockOffset.DOWN);
        }

        if (unsafeSides.size() > 1) {
            info.setType(HoleUtil.HoleType.NONE);
            return info;
        } else {
            double minX = (double) Math.min(centreBlock.func_177958_n(), unsafePos.func_177958_n());
            double maxX = (double) (Math.max(centreBlock.func_177958_n(), unsafePos.func_177958_n()) + 1);
            double minZ = (double) Math.min(centreBlock.func_177952_p(), unsafePos.func_177952_p());
            double maxZ = (double) (Math.max(centreBlock.func_177952_p(), unsafePos.func_177952_p()) + 1);
            info.setCentre(new AxisAlignedBB(minX, (double) centreBlock.func_177956_o(), minZ, maxX, (double) (centreBlock.func_177956_o() + 1), maxZ));
            if (info.getType() != HoleUtil.HoleType.CUSTOM) {
                info.setType(HoleUtil.HoleType.DOUBLE);
            }

            return info;
        }
    }

    public static HashMap getUnsafeSides(BlockPos pos) {
        HashMap output = new HashMap();
        HoleUtil.BlockSafety temp = isBlockSafe(mc.field_71441_e.func_180495_p(HoleUtil.BlockOffset.DOWN.offset(pos)).func_177230_c());
        if (temp != HoleUtil.BlockSafety.UNBREAKABLE) {
            output.put(HoleUtil.BlockOffset.DOWN, temp);
        }

        temp = isBlockSafe(mc.field_71441_e.func_180495_p(HoleUtil.BlockOffset.NORTH.offset(pos)).func_177230_c());
        if (temp != HoleUtil.BlockSafety.UNBREAKABLE) {
            output.put(HoleUtil.BlockOffset.NORTH, temp);
        }

        temp = isBlockSafe(mc.field_71441_e.func_180495_p(HoleUtil.BlockOffset.SOUTH.offset(pos)).func_177230_c());
        if (temp != HoleUtil.BlockSafety.UNBREAKABLE) {
            output.put(HoleUtil.BlockOffset.SOUTH, temp);
        }

        temp = isBlockSafe(mc.field_71441_e.func_180495_p(HoleUtil.BlockOffset.EAST.offset(pos)).func_177230_c());
        if (temp != HoleUtil.BlockSafety.UNBREAKABLE) {
            output.put(HoleUtil.BlockOffset.EAST, temp);
        }

        temp = isBlockSafe(mc.field_71441_e.func_180495_p(HoleUtil.BlockOffset.WEST.offset(pos)).func_177230_c());
        if (temp != HoleUtil.BlockSafety.UNBREAKABLE) {
            output.put(HoleUtil.BlockOffset.WEST, temp);
        }

        return output;
    }

    public static enum BlockOffset {
        DOWN(0, -1, 0),
        UP(0, 1, 0),
        NORTH(0, 0, -1),
        EAST(1, 0, 0),
        SOUTH(0, 0, 1),
        WEST(-1, 0, 0);

        private final int x;
        private final int y;
        private final int z;

        private BlockOffset(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public BlockPos offset(BlockPos pos) {
            return pos.func_177982_a(this.x, this.y, this.z);
        }

        public BlockPos forward(BlockPos pos, int scale) {
            return pos.func_177982_a(this.x * scale, 0, this.z * scale);
        }

        public BlockPos backward(BlockPos pos, int scale) {
            return pos.func_177982_a(-this.x * scale, 0, -this.z * scale);
        }

        public BlockPos left(BlockPos pos, int scale) {
            return pos.func_177982_a(this.z * scale, 0, -this.x * scale);
        }

        public BlockPos right(BlockPos pos, int scale) {
            return pos.func_177982_a(-this.z * scale, 0, this.x * scale);
        }
    }

    public static class HoleInfo {
        private HoleUtil.HoleType type;
        private HoleUtil.BlockSafety safety;
        private AxisAlignedBB centre;

        public HoleInfo() {
            this(HoleUtil.BlockSafety.UNBREAKABLE, HoleUtil.HoleType.NONE);
        }

        public HoleInfo(HoleUtil.BlockSafety safety, HoleUtil.HoleType type) {
            this.type = type;
            this.safety = safety;
        }

        public void setType(HoleUtil.HoleType type) {
            this.type = type;
        }

        public void setSafety(HoleUtil.BlockSafety safety) {
            this.safety = safety;
        }

        public void setCentre(AxisAlignedBB centre) {
            this.centre = centre;
        }

        public HoleUtil.HoleType getType() {
            return this.type;
        }

        public HoleUtil.BlockSafety getSafety() {
            return this.safety;
        }

        public AxisAlignedBB getCentre() {
            return this.centre;
        }
    }

    public static enum HoleType {
        SINGLE,
        DOUBLE,
        CUSTOM,
        NONE;
    }

    public static enum BlockSafety {
        UNBREAKABLE,
        RESISTANT,
        BREAKABLE;
    }
}
