package me.dev.legacy.modules.combat;

import me.dev.legacy.Legacy;
import me.dev.legacy.api.util.BlockInteractionHelper;
import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.modules.Module;
import me.dev.legacy.modules.ModuleManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameType;

import java.util.Iterator;

public class AntiCity extends Module {
    private final Setting triggerable = this.register(new Setting("Triggerable", true));
    private final Setting turnOffCauras = this.register(new Setting("Toggle Other Cauras", true));
    private final Setting timeoutTicks = this.register(new Setting("TimeoutTicks", Integer.valueOf(40), Integer.valueOf(1), Integer.valueOf(100)));
    private final Setting blocksPerTick = this.register(new Setting("BlocksPerTick", Integer.valueOf(4), Integer.valueOf(1), Integer.valueOf(9)));
    private final Setting tickDelay = this.register(new Setting("TickDelay", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(10)));
    private final Setting rotate = this.register(new Setting("Rotate", true));
    private final Setting noGlitchBlocks = this.register(new Setting("NoGlitchBlocks", true));
    private int playerHotbarSlot = -1;
    private int lastHotbarSlot = -1;
    private int offsetStep = 0;
    private int delayStep = 0;
    private int totalTicksRunning = 0;
    private boolean firstRun;
    private boolean isSneaking = false;
    double oldY;
    int cDelay = 0;
    String caura;
    boolean isDisabling;
    boolean hasDisabled;

    public AntiCity() {
        super("AntiCity", "AntiCity.", Module.Category.COMBAT, true, false, false);
    }

    public void onEnable() {
        if (Surround.mc.field_71439_g == null) {
            this.disable();
        } else {
            this.hasDisabled = false;
            this.oldY = mc.field_71439_g.field_70163_u;
            this.firstRun = true;
            this.playerHotbarSlot = Surround.mc.field_71439_g.field_71071_by.field_70461_c;
            this.lastHotbarSlot = -1;
        }
    }

    public void onDisable() {
        if (Surround.mc.field_71439_g != null) {
            if (this.lastHotbarSlot != this.playerHotbarSlot && this.playerHotbarSlot != -1) {
                Surround.mc.field_71439_g.field_71071_by.field_70461_c = this.playerHotbarSlot;
            }

            if (this.isSneaking) {
                Surround.mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(Surround.mc.field_71439_g, Action.STOP_SNEAKING));
                this.isSneaking = false;
            }

            this.playerHotbarSlot = -1;
            this.lastHotbarSlot = -1;
        }
    }

    public void onUpdate() {
        if (this.cDelay > 0) {
            --this.cDelay;
        }

        if (this.cDelay == 0 && this.isDisabling) {
            Legacy.moduleManager.getModuleByName(this.caura).toggle();
            this.isDisabling = false;
            this.hasDisabled = true;
        }

        if (Surround.mc.field_71439_g != null) {
            ModuleManager var10000 = Legacy.moduleManager;
            if (!ModuleManager.isModuleEnabled("Freecam")) {
                if (Legacy.moduleManager.getModuleByName("AutoCrystal") != null && Legacy.moduleManager.getModuleByName("AutoCrystal").isEnabled() && ((Boolean) this.turnOffCauras.getValue()).booleanValue() && !this.hasDisabled) {
                    this.caura = "AutoCrystal";
                    this.cDelay = 19;
                    this.isDisabling = true;
                    Legacy.moduleManager.getModuleByName(this.caura).toggle();
                }

                if (((Boolean) this.triggerable.getValue()).booleanValue() && this.totalTicksRunning >= ((Integer) this.timeoutTicks.getValue()).intValue()) {
                    this.totalTicksRunning = 0;
                    this.disable();
                    return;
                }

                if (mc.field_71439_g.field_70163_u != this.oldY) {
                    this.disable();
                    return;
                }

                if (!this.firstRun) {
                    if (this.delayStep < ((Integer) this.tickDelay.getValue()).intValue()) {
                        ++this.delayStep;
                        return;
                    }

                    this.delayStep = 0;
                }

                if (this.firstRun) {
                    this.firstRun = false;
                }

                int blocksPlaced;
                for (blocksPlaced = 0; blocksPlaced < ((Integer) this.blocksPerTick.getValue()).intValue(); ++this.offsetStep) {
                    Vec3d[] offsetPattern = new Vec3d[0];
                    int maxSteps = false;
                    offsetPattern = AntiCity.Offsets.SURROUND;
                    int maxSteps = AntiCity.Offsets.SURROUND.length;
                    if (this.offsetStep >= maxSteps) {
                        this.offsetStep = 0;
                        break;
                    }

                    BlockPos offsetPos = new BlockPos(offsetPattern[this.offsetStep]);
                    BlockPos targetPos = (new BlockPos(Surround.mc.field_71439_g.func_174791_d())).func_177982_a(offsetPos.func_177958_n(), offsetPos.func_177956_o(), offsetPos.func_177952_p());
                    if (this.placeBlock(targetPos)) {
                        ++blocksPlaced;
                    }
                }

                if (blocksPlaced > 0) {
                    if (this.lastHotbarSlot != this.playerHotbarSlot && this.playerHotbarSlot != -1) {
                        Surround.mc.field_71439_g.field_71071_by.field_70461_c = this.playerHotbarSlot;
                        this.lastHotbarSlot = this.playerHotbarSlot;
                    }

                    if (this.isSneaking) {
                        Surround.mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(Surround.mc.field_71439_g, Action.STOP_SNEAKING));
                        this.isSneaking = false;
                    }
                }

                ++this.totalTicksRunning;
                return;
            }
        }

    }

    private boolean placeBlock(BlockPos pos) {
        Block block = Surround.mc.field_71441_e.func_180495_p(pos).func_177230_c();
        if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid)) {
            return false;
        } else {
            Iterator var3 = Surround.mc.field_71441_e.func_72839_b((Entity) null, new AxisAlignedBB(pos)).iterator();

            while (var3.hasNext()) {
                Entity entity = (Entity) var3.next();
                if (!(entity instanceof EntityItem) && !(entity instanceof EntityXPOrb)) {
                    return false;
                }
            }

            EnumFacing side = BlockInteractionHelper.getPlaceableSide(pos);
            if (side == null) {
                return false;
            } else {
                BlockPos neighbour = pos.func_177972_a(side);
                EnumFacing opposite = side.func_176734_d();
                if (!BlockInteractionHelper.canBeClicked(neighbour)) {
                    return false;
                } else {
                    Vec3d hitVec = (new Vec3d(neighbour)).func_72441_c(0.5D, 0.5D, 0.5D).func_178787_e((new Vec3d(opposite.func_176730_m())).func_186678_a(0.5D));
                    Block neighbourBlock = Surround.mc.field_71441_e.func_180495_p(neighbour).func_177230_c();
                    int obiSlot = this.findObiInHotbar();
                    if (obiSlot == -1) {
                        this.disable();
                    }

                    if (this.lastHotbarSlot != obiSlot) {
                        Surround.mc.field_71439_g.field_71071_by.field_70461_c = obiSlot;
                        this.lastHotbarSlot = obiSlot;
                    }

                    if (!this.isSneaking && BlockInteractionHelper.blackList.contains(neighbourBlock) || BlockInteractionHelper.shulkerList.contains(neighbourBlock)) {
                        Surround.mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(Surround.mc.field_71439_g, Action.START_SNEAKING));
                        this.isSneaking = true;
                    }

                    if (((Boolean) this.rotate.getValue()).booleanValue()) {
                        BlockInteractionHelper.faceVectorPacketInstant(hitVec);
                    }

                    Surround.mc.field_71442_b.func_187099_a(Surround.mc.field_71439_g, Surround.mc.field_71441_e, neighbour, opposite, hitVec, EnumHand.MAIN_HAND);
                    Surround.mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
                    Surround.mc.field_71467_ac = 4;
                    if (((Boolean) this.noGlitchBlocks.getValue()).booleanValue() && !Surround.mc.field_71442_b.func_178889_l().equals(GameType.CREATIVE)) {
                        Surround.mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerDigging(net.minecraft.network.play.client.CPacketPlayerDigging.Action.START_DESTROY_BLOCK, neighbour, opposite));
                    }

                    return true;
                }
            }
        }
    }

    private int findObiInHotbar() {
        int slot = -1;

        for (int i = 0; i < 9; ++i) {
            ItemStack stack = Surround.mc.field_71439_g.field_71071_by.func_70301_a(i);
            if (stack != ItemStack.field_190927_a && stack.func_77973_b() instanceof ItemBlock) {
                Block block = ((ItemBlock) stack.func_77973_b()).func_179223_d();
                if (block instanceof BlockObsidian) {
                    slot = i;
                    break;
                }
            }
        }

        return slot;
    }

    private static class Offsets {
        private static final Vec3d[] SURROUND = new Vec3d[]{new Vec3d(2.0D, 0.0D, 0.0D), new Vec3d(0.0D, 0.0D, 2.0D), new Vec3d(-2.0D, 0.0D, 0.0D), new Vec3d(0.0D, 0.0D, -2.0D)};
    }

    private static enum Mode {
        SURROUND,
        FULL;
    }
}
