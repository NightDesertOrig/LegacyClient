package me.dev.legacy.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.dev.legacy.Legacy;
import me.dev.legacy.impl.command.Command;
import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.modules.Module;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerDigging.Action;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;

import java.util.Iterator;
import java.util.List;

public class AutoCity extends Module {
    private boolean firstRun;
    private BlockPos mineTarget;
    private EntityPlayer closestTarget;
    Setting range = this.register(new Setting("Range", 5.0D, 1.0D, 6.0D));
    Setting announceUsage = this.register(new Setting("Announce", false));

    public AutoCity() {
        super("AutoCity", "AutoCity", Module.Category.COMBAT, true, false, false);
    }

    public void onEnable() {
        if (mc.field_71439_g == null) {
            this.toggle();
        } else {
            MinecraftForge.EVENT_BUS.register(this);
            this.firstRun = true;
        }
    }

    public void onDisable() {
        if (mc.field_71439_g != null) {
            MinecraftForge.EVENT_BUS.unregister(this);
            Command.sendMessage(ChatFormatting.RED.toString() + " Disabled");
        }
    }

    public void onUpdate() {
        if (mc.field_71439_g != null) {
            this.findClosestTarget();
            if (this.closestTarget == null) {
                if (this.firstRun) {
                    this.firstRun = false;
                    if (((Boolean) this.announceUsage.getValue()).booleanValue()) {
                        Command.sendMessage(ChatFormatting.WHITE.toString() + "Enabled" + ChatFormatting.RESET.toString() + ", no one to city!");
                    }
                }

                this.toggle();
            } else {
                if (this.firstRun && this.mineTarget != null) {
                    this.firstRun = false;
                    if (((Boolean) this.announceUsage.getValue()).booleanValue()) {
                        Command.sendMessage(" Trying to mine: " + ChatFormatting.AQUA.toString() + this.closestTarget.func_70005_c_());
                    }
                }

                this.findCityBlock();
                if (this.mineTarget != null) {
                    int newSlot = -1;

                    for (int i = 0; i < 9; ++i) {
                        ItemStack stack = mc.field_71439_g.field_71071_by.func_70301_a(i);
                        if (stack != ItemStack.field_190927_a && stack.func_77973_b() instanceof ItemPickaxe) {
                            newSlot = i;
                            break;
                        }
                    }

                    if (newSlot != -1) {
                        mc.field_71439_g.field_71071_by.field_70461_c = newSlot;
                    }

                    mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
                    mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerDigging(Action.START_DESTROY_BLOCK, this.mineTarget, EnumFacing.UP));
                    mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerDigging(Action.STOP_DESTROY_BLOCK, this.mineTarget, EnumFacing.UP));
                    this.toggle();
                } else {
                    Command.sendMessage(TextFormatting.BLUE + "] No city blocks to mine!");
                    this.toggle();
                }

            }
        }
    }

    public BlockPos findCityBlock() {
        Double dist = (Double) this.range.getValue();
        Vec3d vec = this.closestTarget.func_174791_d();
        if (mc.field_71439_g.func_174791_d().func_72438_d(vec) <= dist.doubleValue()) {
            BlockPos targetX = new BlockPos(vec.func_72441_c(1.0D, 0.0D, 0.0D));
            BlockPos targetXMinus = new BlockPos(vec.func_72441_c(-1.0D, 0.0D, 0.0D));
            BlockPos targetZ = new BlockPos(vec.func_72441_c(0.0D, 0.0D, 1.0D));
            BlockPos targetZMinus = new BlockPos(vec.func_72441_c(0.0D, 0.0D, -1.0D));
            if (this.canBreak(targetX)) {
                this.mineTarget = targetX;
            }

            if (!this.canBreak(targetX) && this.canBreak(targetXMinus)) {
                this.mineTarget = targetXMinus;
            }

            if (!this.canBreak(targetX) && !this.canBreak(targetXMinus) && this.canBreak(targetZ)) {
                this.mineTarget = targetZ;
            }

            if (!this.canBreak(targetX) && !this.canBreak(targetXMinus) && !this.canBreak(targetZ) && this.canBreak(targetZMinus)) {
                this.mineTarget = targetZMinus;
            }

            if (!this.canBreak(targetX) && !this.canBreak(targetXMinus) && !this.canBreak(targetZ) && !this.canBreak(targetZMinus) || mc.field_71439_g.func_174791_d().func_72438_d(vec) > dist.doubleValue()) {
                this.mineTarget = null;
            }
        }

        return this.mineTarget;
    }

    private boolean canBreak(BlockPos pos) {
        IBlockState blockState = mc.field_71441_e.func_180495_p(pos);
        Block block = blockState.func_177230_c();
        return block.func_176195_g(blockState, mc.field_71441_e, pos) != -1.0F;
    }

    private void findClosestTarget() {
        List playerList = mc.field_71441_e.field_73010_i;
        this.closestTarget = null;
        Iterator var2 = playerList.iterator();

        while (var2.hasNext()) {
            EntityPlayer target = (EntityPlayer) var2.next();
            if (target != mc.field_71439_g && !Legacy.friendManager.isFriend(target.func_70005_c_()) && isLiving(target) && target.func_110143_aJ() > 0.0F) {
                if (this.closestTarget == null) {
                    this.closestTarget = target;
                } else if (mc.field_71439_g.func_70032_d(target) < mc.field_71439_g.func_70032_d(this.closestTarget)) {
                    this.closestTarget = target;
                }
            }
        }

    }

    public static boolean isLiving(Entity e) {
        return e instanceof EntityLivingBase;
    }
}
