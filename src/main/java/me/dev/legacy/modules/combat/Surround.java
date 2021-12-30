package me.dev.legacy.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.dev.legacy.Legacy;
import me.dev.legacy.api.util.BlockUtil;
import me.dev.legacy.api.util.EntityUtil;
import me.dev.legacy.api.util.InventoryUtil;
import me.dev.legacy.api.util.Timer;
import me.dev.legacy.impl.command.Command;
import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.modules.Module;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.*;

public class Surround extends Module {
    public static boolean isPlacing = false;
    private final Setting blocksPerTick = this.register(new Setting("BPT", Integer.valueOf(20), Integer.valueOf(1), Integer.valueOf(20)));
    private final Setting delay = this.register(new Setting("Delay", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(250)));
    private final Setting noGhost = this.register(new Setting("Packet", false));
    private final Setting center = this.register(new Setting("TPCenter", false));
    private final Setting rotate = this.register(new Setting("Rotate", false));
    private final Setting helpingBlocks = this.register(new Setting("HelpingBlocks", true));
    private final Setting antiPedo = this.register(new Setting("Always Help", false));
    private final Setting floor = this.register(new Setting("Floor", true));
    private final Timer timer = new Timer();
    private final Timer retryTimer = new Timer();
    private final Set extendingBlocks = new HashSet();
    private final Map retries = new HashMap();
    private BlockPos startPos;
    private boolean didPlace = false;
    private int lastHotbarSlot;
    private boolean isSneaking;
    private int placements = 0;
    private int extenders = 1;
    private boolean offHand = false;

    public Surround() {
        super("Surround", "surround", Module.Category.COMBAT, true, false, false);
    }

    public void onEnable() {
        if (fullNullCheck()) {
            this.disable();
        }

        this.lastHotbarSlot = mc.field_71439_g.field_71071_by.field_70461_c;
        this.startPos = EntityUtil.getRoundedBlockPos(mc.field_71439_g);
        if (((Boolean) this.center.getValue()).booleanValue()) {
            Legacy.positionManager.setPositionPacket((double) this.startPos.func_177958_n() + 0.5D, (double) this.startPos.func_177956_o(), (double) this.startPos.func_177952_p() + 0.5D, true, true, true);
        }

        this.retries.clear();
        this.retryTimer.reset();
    }

    public void onTick() {
        this.doFeetPlace();
    }

    public void onDisable() {
        if (!nullCheck()) {
            isPlacing = false;
            this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
        }
    }

    private void doFeetPlace() {
        if (!this.check()) {
            if (mc.field_71439_g.field_70163_u < mc.field_71439_g.field_70163_u) {
                this.setEnabled(false);
            } else {
                boolean onEChest = mc.field_71441_e.func_180495_p(new BlockPos(mc.field_71439_g.func_174791_d())).func_177230_c() == Blocks.field_150477_bB;
                if (mc.field_71439_g.field_70163_u - (double) ((int) mc.field_71439_g.field_70163_u) < 0.7D) {
                    onEChest = false;
                }

                if (!BlockUtil.isSafe(mc.field_71439_g, onEChest ? 1 : 0, ((Boolean) this.floor.getValue()).booleanValue())) {
                    this.placeBlocks(mc.field_71439_g.func_174791_d(), BlockUtil.getUnsafeBlockArray(mc.field_71439_g, onEChest ? 1 : 0, ((Boolean) this.floor.getValue()).booleanValue()), ((Boolean) this.helpingBlocks.getValue()).booleanValue(), false);
                } else if (!BlockUtil.isSafe(mc.field_71439_g, onEChest ? 0 : -1, false) && ((Boolean) this.antiPedo.getValue()).booleanValue()) {
                    this.placeBlocks(mc.field_71439_g.func_174791_d(), BlockUtil.getUnsafeBlockArray(mc.field_71439_g, onEChest ? 0 : -1, false), false, false);
                }

                this.processExtendingBlocks();
                if (this.didPlace) {
                    this.timer.reset();
                }

            }
        }
    }

    private void processExtendingBlocks() {
        if (this.extendingBlocks.size() == 2 && this.extenders < 1) {
            Vec3d[] array = new Vec3d[2];
            int i = 0;

            for (Iterator iterator = this.extendingBlocks.iterator(); iterator.hasNext(); ++i) {
                array[i] = (Vec3d) iterator.next();
            }

            int placementsBefore = this.placements;
            if (this.areClose(array) != null) {
                this.placeBlocks(this.areClose(array), EntityUtil.getUnsafeBlockArrayFromVec3d(this.areClose(array), 0, true), true, false);
            }

            if (placementsBefore < this.placements) {
                this.extendingBlocks.clear();
            }
        } else if (this.extendingBlocks.size() > 2 || this.extenders >= 1) {
            this.extendingBlocks.clear();
        }

    }

    private Vec3d areClose(Vec3d[] vec3ds) {
        int matches = 0;
        Vec3d[] var3 = vec3ds;
        int var4 = vec3ds.length;

        for (int var5 = 0; var5 < var4; ++var5) {
            Vec3d vec3d = var3[var5];
            Vec3d[] var7 = EntityUtil.getUnsafeBlockArray(mc.field_71439_g, 0, true);
            int var8 = var7.length;

            for (int var9 = 0; var9 < var8; ++var9) {
                Vec3d pos = var7[var9];
                if (vec3d.equals(pos)) {
                    ++matches;
                }
            }
        }

        if (matches == 2) {
            return mc.field_71439_g.func_174791_d().func_178787_e(vec3ds[0].func_178787_e(vec3ds[1]));
        } else {
            return null;
        }
    }

    private boolean placeBlocks(Vec3d pos, Vec3d[] vec3ds, boolean hasHelpingBlocks, boolean isHelping) {
        boolean gotHelp = true;
        Vec3d[] var6 = vec3ds;
        int var7 = vec3ds.length;

        for (int var8 = 0; var8 < var7; ++var8) {
            Vec3d vec3d = var6[var8];
            gotHelp = true;
            BlockPos position = (new BlockPos(pos)).func_177963_a(vec3d.field_72450_a, vec3d.field_72448_b, vec3d.field_72449_c);
            switch (BlockUtil.isPositionPlaceable(position, false)) {
                case 1:
                    if (this.retries.get(position) == null || ((Integer) this.retries.get(position)).intValue() < 4) {
                        this.placeBlock(position);
                        this.retries.put(position, this.retries.get(position) == null ? 1 : ((Integer) this.retries.get(position)).intValue() + 1);
                        this.retryTimer.reset();
                        continue;
                    }
                case 2:
                    if (!hasHelpingBlocks) {
                        continue;
                    }

                    gotHelp = this.placeBlocks(pos, BlockUtil.getHelpingBlocks(vec3d), false, true);
                case 3:
                    break;
                default:
                    continue;
            }

            if (gotHelp) {
                this.placeBlock(position);
            }

            if (isHelping) {
                return true;
            }
        }

        return false;
    }

    private boolean check() {
        if (nullCheck()) {
            return true;
        } else {
            int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
            int eChestSot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
            if (obbySlot == -1 && eChestSot == -1) {
                this.toggle();
            }

            this.offHand = InventoryUtil.isBlock(mc.field_71439_g.func_184592_cb().func_77973_b(), BlockObsidian.class);
            isPlacing = false;
            this.didPlace = false;
            this.extenders = 1;
            this.placements = 0;
            obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
            int echestSlot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
            if (this.isOff()) {
                return true;
            } else {
                if (this.retryTimer.passedMs(2500L)) {
                    this.retries.clear();
                    this.retryTimer.reset();
                }

                if (obbySlot == -1 && !this.offHand && echestSlot == -1) {
                    Command.sendMessage("<" + this.getDisplayName() + "> " + ChatFormatting.RED + "No Obsidian in hotbar disabling...");
                    this.disable();
                    return true;
                } else {
                    this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
                    if (mc.field_71439_g.field_71071_by.field_70461_c != this.lastHotbarSlot && mc.field_71439_g.field_71071_by.field_70461_c != obbySlot && mc.field_71439_g.field_71071_by.field_70461_c != echestSlot) {
                        this.lastHotbarSlot = mc.field_71439_g.field_71071_by.field_70461_c;
                    }

                    if (!this.startPos.equals(EntityUtil.getRoundedBlockPos(mc.field_71439_g))) {
                        this.disable();
                        return true;
                    } else {
                        return !this.timer.passedMs((long) ((Integer) this.delay.getValue()).intValue());
                    }
                }
            }
        }
    }

    private void placeBlock(BlockPos pos) {
        if (this.placements < ((Integer) this.blocksPerTick.getValue()).intValue()) {
            int originalSlot = mc.field_71439_g.field_71071_by.field_70461_c;
            int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
            int eChestSot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
            if (obbySlot == -1 && eChestSot == -1) {
                this.toggle();
            }

            isPlacing = true;
            mc.field_71439_g.field_71071_by.field_70461_c = obbySlot == -1 ? eChestSot : obbySlot;
            mc.field_71442_b.func_78765_e();
            this.isSneaking = BlockUtil.placeBlock(pos, this.offHand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, ((Boolean) this.rotate.getValue()).booleanValue(), ((Boolean) this.noGhost.getValue()).booleanValue(), this.isSneaking);
            mc.field_71439_g.field_71071_by.field_70461_c = originalSlot;
            mc.field_71442_b.func_78765_e();
            this.didPlace = true;
            ++this.placements;
        }

    }
}
