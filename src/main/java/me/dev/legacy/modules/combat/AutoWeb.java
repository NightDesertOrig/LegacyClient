package me.dev.legacy.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.dev.legacy.Legacy;
import me.dev.legacy.api.util.*;
import me.dev.legacy.impl.command.Command;
import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.modules.Module;
import net.minecraft.block.BlockWeb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class AutoWeb extends Module {
    public static boolean isPlacing = false;
    private final Setting delay = this.register(new Setting("Delay", Integer.valueOf(50), Integer.valueOf(0), Integer.valueOf(250)));
    private final Setting blocksPerPlace = this.register(new Setting("BlocksPerTick", Integer.valueOf(8), Integer.valueOf(1), Integer.valueOf(30)));
    private final Setting packet = this.register(new Setting("Packet", false));
    private final Setting disable = this.register(new Setting("AutoDisable", false));
    private final Setting rotate = this.register(new Setting("Rotate", false));
    private final Setting raytrace = this.register(new Setting("Raytrace", false));
    private final Setting lowerbody = this.register(new Setting("Feet", true));
    private final Setting upperBody = this.register(new Setting("Face", false));
    private final Timer timer = new Timer();
    public EntityPlayer target;
    private boolean didPlace = false;
    private boolean switchedItem;
    private boolean isSneaking;
    private int lastHotbarSlot;
    private int placements = 0;
    private boolean smartRotate = false;
    private BlockPos startPos = null;

    public AutoWeb() {
        super("AutoWeb", "Traps other players in webs", Module.Category.COMBAT, true, false, false);
    }

    public void onEnable() {
        if (!fullNullCheck()) {
            this.startPos = EntityUtil.getRoundedBlockPos(mc.field_71439_g);
            this.lastHotbarSlot = mc.field_71439_g.field_71071_by.field_70461_c;
        }
    }

    public void onTick() {
        this.smartRotate = false;
        this.doTrap();
    }

    public String getDisplayInfo() {
        return this.target != null ? this.target.func_70005_c_() : null;
    }

    public void onDisable() {
        isPlacing = false;
        this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
        this.switchItem(true);
    }

    private void doTrap() {
        if (!this.check()) {
            this.doWebTrap();
            if (this.didPlace) {
                this.timer.reset();
            }

        }
    }

    private void doWebTrap() {
        List placeTargets = this.getPlacements();
        this.placeList(placeTargets);
    }

    private List getPlacements() {
        ArrayList list = new ArrayList();
        Vec3d baseVec = this.target.func_174791_d();
        if (((Boolean) this.lowerbody.getValue()).booleanValue()) {
            list.add(baseVec);
        }

        if (((Boolean) this.upperBody.getValue()).booleanValue()) {
            list.add(baseVec.func_72441_c(0.0D, 1.0D, 0.0D));
        }

        return list;
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
            BlockPos position;
            int placeability;
            do {
                if (!var2.hasNext()) {
                    return;
                }

                Vec3d vec3d3 = (Vec3d) var2.next();
                position = new BlockPos(vec3d3);
                placeability = BlockUtil.isPositionPlaceable(position, ((Boolean) this.raytrace.getValue()).booleanValue());
            } while (placeability != 3 && placeability != 1);

            this.placeBlock(position);
        }
    }

    private boolean check() {
        isPlacing = false;
        this.didPlace = false;
        this.placements = 0;
        int obbySlot = InventoryUtil.findHotbarBlock(BlockWeb.class);
        if (this.isOff()) {
            return true;
        } else if (((Boolean) this.disable.getValue()).booleanValue() && !this.startPos.equals(EntityUtil.getRoundedBlockPos(mc.field_71439_g))) {
            this.disable();
            return true;
        } else if (obbySlot == -1) {
            Command.sendMessage("<" + this.getDisplayName() + "> " + ChatFormatting.RED + "No Webs in hotbar disabling...");
            this.toggle();
            return true;
        } else {
            if (mc.field_71439_g.field_71071_by.field_70461_c != this.lastHotbarSlot && mc.field_71439_g.field_71071_by.field_70461_c != obbySlot) {
                this.lastHotbarSlot = mc.field_71439_g.field_71071_by.field_70461_c;
            }

            this.switchItem(true);
            this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
            this.target = this.getTarget(10.0D);
            return this.target == null || !this.timer.passedMs((long) ((Integer) this.delay.getValue()).intValue());
        }
    }

    private EntityPlayer getTarget(double range) {
        EntityPlayer target = null;
        double distance = Math.pow(range, 2.0D) + 1.0D;
        Iterator var6 = mc.field_71441_e.field_73010_i.iterator();

        while (var6.hasNext()) {
            EntityPlayer player = (EntityPlayer) var6.next();
            if (!EntityUtil.isntValid(player, range) && !player.field_70134_J && Legacy.speedManager.getPlayerSpeed(player) <= 30.0D) {
                if (target == null) {
                    target = player;
                    distance = mc.field_71439_g.func_70068_e(player);
                } else if (mc.field_71439_g.func_70068_e(player) < distance) {
                    target = player;
                    distance = mc.field_71439_g.func_70068_e(player);
                }
            }
        }

        return target;
    }

    private void placeBlock(BlockPos pos) {
        if (this.placements < ((Integer) this.blocksPerPlace.getValue()).intValue() && mc.field_71439_g.func_174818_b(pos) <= MathUtil.square(6.0D) && this.switchItem(false)) {
            isPlacing = true;
            this.isSneaking = this.smartRotate ? BlockUtil.placeBlockSmartRotate(pos, EnumHand.MAIN_HAND, true, ((Boolean) this.packet.getValue()).booleanValue(), this.isSneaking) : BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, ((Boolean) this.rotate.getValue()).booleanValue(), ((Boolean) this.packet.getValue()).booleanValue(), this.isSneaking);
            this.didPlace = true;
            ++this.placements;
        }

    }

    private boolean switchItem(boolean back) {
        boolean[] value = InventoryUtil.switchItem(back, this.lastHotbarSlot, this.switchedItem, InventoryUtil.Switch.NORMAL, BlockWeb.class);
        this.switchedItem = value[0];
        return value[1];
    }
}
