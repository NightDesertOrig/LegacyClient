package me.dev.legacy.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.dev.legacy.Legacy;
import me.dev.legacy.api.util.Timer;
import me.dev.legacy.api.util.*;
import me.dev.legacy.impl.command.Command;
import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.modules.Module;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.*;

public class AutoTrap extends Module {
    public static boolean isPlacing = false;
    private final Setting delay = this.register(new Setting("Delay", Integer.valueOf(50), Integer.valueOf(0), Integer.valueOf(250)));
    private final Setting blocksPerPlace = this.register(new Setting("BlocksPerTick", Integer.valueOf(8), Integer.valueOf(1), Integer.valueOf(30)));
    private final Setting rotate = this.register(new Setting("Rotate", false));
    private final Setting raytrace = this.register(new Setting("Raytrace", false));
    private final Setting antiScaffold = this.register(new Setting("AntiScaffold", false));
    private final Setting antiStep = this.register(new Setting("AntiStep", false));
    private final Setting noGhost = this.register(new Setting("Packet", true));
    private final Timer timer = new Timer();
    private final Map retries = new HashMap();
    private final Timer retryTimer = new Timer();
    public EntityPlayer target;
    private boolean didPlace = false;
    private boolean switchedItem;
    private boolean isSneaking;
    private int lastHotbarSlot;
    private int placements = 0;
    private BlockPos startPos = null;
    private boolean offHand = false;

    public AutoTrap() {
        super("AutoTrap", "Traps other players", Module.Category.COMBAT, true, false, false);
    }

    public void onEnable() {
        if (!fullNullCheck()) {
            this.startPos = EntityUtil.getRoundedBlockPos(mc.field_71439_g);
            this.lastHotbarSlot = mc.field_71439_g.field_71071_by.field_70461_c;
            this.retries.clear();
        }
    }

    public void onTick() {
        if (!fullNullCheck()) {
            this.doTrap();
        }
    }

    public String getDisplayInfo() {
        return this.target != null ? this.target.func_70005_c_() : null;
    }

    public void onDisable() {
        isPlacing = false;
        this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
    }

    private void doTrap() {
        if (!this.check()) {
            this.doStaticTrap();
            if (this.didPlace) {
                this.timer.reset();
            }

        }
    }

    private void doStaticTrap() {
        List placeTargets = EntityUtil.targets(this.target.func_174791_d(), ((Boolean) this.antiScaffold.getValue()).booleanValue(), ((Boolean) this.antiStep.getValue()).booleanValue(), false, false, false, ((Boolean) this.raytrace.getValue()).booleanValue());
        this.placeList(placeTargets);
    }

    private void placeList(List list) {
        list.sort((vec3d, vec3d2) -> {
            return Double.compare(mc.field_71439_g.func_70092_e(vec3d2.field_72450_a, vec3d2.field_72448_b, vec3d2.field_72449_c), mc.field_71439_g.func_70092_e(vec3d.field_72450_a, vec3d.field_72448_b, vec3d.field_72449_c));
        });
        list.sort(Comparator.comparingDouble((vec3d) -> {
            return vec3d.field_72448_b;
        }));
        Iterator var2 = list.iterator();

        while (true) {
            while (var2.hasNext()) {
                Vec3d vec3d3 = (Vec3d) var2.next();
                BlockPos position = new BlockPos(vec3d3);
                int placeability = BlockUtil.isPositionPlaceable(position, ((Boolean) this.raytrace.getValue()).booleanValue());
                if (placeability == 1 && (this.retries.get(position) == null || ((Integer) this.retries.get(position)).intValue() < 4)) {
                    this.placeBlock(position);
                    this.retries.put(position, this.retries.get(position) == null ? 1 : ((Integer) this.retries.get(position)).intValue() + 1);
                    this.retryTimer.reset();
                } else if (placeability == 3) {
                    this.placeBlock(position);
                }
            }

            return;
        }
    }

    private boolean check() {
        isPlacing = false;
        this.didPlace = false;
        this.placements = 0;
        int obbySlot2 = InventoryUtil.findHotbarBlock(BlockObsidian.class);
        if (obbySlot2 == -1) {
            this.toggle();
        }

        int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
        if (this.isOff()) {
            return true;
        } else if (!this.startPos.equals(EntityUtil.getRoundedBlockPos(mc.field_71439_g))) {
            this.disable();
            return true;
        } else {
            if (this.retryTimer.passedMs(2000L)) {
                this.retries.clear();
                this.retryTimer.reset();
            }

            if (obbySlot == -1) {
                Command.sendMessage("<" + this.getDisplayName() + "> " + ChatFormatting.RED + "No Obsidian in hotbar disabling...");
                this.disable();
                return true;
            } else {
                if (mc.field_71439_g.field_71071_by.field_70461_c != this.lastHotbarSlot && mc.field_71439_g.field_71071_by.field_70461_c != obbySlot) {
                    this.lastHotbarSlot = mc.field_71439_g.field_71071_by.field_70461_c;
                }

                this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
                this.target = this.getTarget(10.0D, true);
                return this.target == null || !this.timer.passedMs((long) ((Integer) this.delay.getValue()).intValue());
            }
        }
    }

    private EntityPlayer getTarget(double range, boolean trapped) {
        EntityPlayer target = null;
        double distance = Math.pow(range, 2.0D) + 1.0D;
        Iterator var7 = mc.field_71441_e.field_73010_i.iterator();

        while (true) {
            EntityPlayer player;
            do {
                do {
                    if (!var7.hasNext()) {
                        return target;
                    }

                    player = (EntityPlayer) var7.next();
                } while (EntityUtil.isntValid(player, range));
            } while (trapped && EntityUtil.isTrapped(player, ((Boolean) this.antiScaffold.getValue()).booleanValue(), ((Boolean) this.antiStep.getValue()).booleanValue(), false, false, false));

            if (Legacy.speedManager.getPlayerSpeed(player) <= 10.0D) {
                if (target == null) {
                    target = player;
                    distance = mc.field_71439_g.func_70068_e(player);
                } else if (mc.field_71439_g.func_70068_e(player) < distance) {
                    target = player;
                    distance = mc.field_71439_g.func_70068_e(player);
                }
            }
        }
    }

    private void placeBlock(BlockPos pos) {
        if (this.placements < ((Integer) this.blocksPerPlace.getValue()).intValue() && mc.field_71439_g.func_174818_b(pos) <= MathUtil.square(5.0D)) {
            isPlacing = true;
            int originalSlot = mc.field_71439_g.field_71071_by.field_70461_c;
            int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
            int eChestSot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
            if (obbySlot == -1 && eChestSot == -1) {
                this.toggle();
            }

            if (((Boolean) this.rotate.getValue()).booleanValue()) {
                mc.field_71439_g.field_71071_by.field_70461_c = obbySlot == -1 ? eChestSot : obbySlot;
                mc.field_71442_b.func_78765_e();
                this.isSneaking = BlockUtil.placeBlock(pos, this.offHand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, ((Boolean) this.rotate.getValue()).booleanValue(), ((Boolean) this.noGhost.getValue()).booleanValue(), this.isSneaking);
                mc.field_71439_g.field_71071_by.field_70461_c = originalSlot;
                mc.field_71442_b.func_78765_e();
            } else {
                mc.field_71439_g.field_71071_by.field_70461_c = obbySlot == -1 ? eChestSot : obbySlot;
                mc.field_71442_b.func_78765_e();
                this.isSneaking = BlockUtil.placeBlock(pos, this.offHand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, ((Boolean) this.rotate.getValue()).booleanValue(), ((Boolean) this.noGhost.getValue()).booleanValue(), this.isSneaking);
                mc.field_71439_g.field_71071_by.field_70461_c = originalSlot;
                mc.field_71442_b.func_78765_e();
            }

            this.didPlace = true;
            ++this.placements;
        }

    }
}
