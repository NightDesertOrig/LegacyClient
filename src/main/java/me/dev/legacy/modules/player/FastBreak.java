package me.dev.legacy.modules.player;

import me.dev.legacy.Legacy;
import me.dev.legacy.api.event.events.block.BlockEvent;
import me.dev.legacy.api.event.events.render.Render3DEvent;
import me.dev.legacy.api.util.BlockUtil;
import me.dev.legacy.api.util.InventoryUtil;
import me.dev.legacy.api.util.RenderUtil;
import me.dev.legacy.api.util.Timer;
import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.modules.Module;
import me.dev.legacy.modules.combat.Surround;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerDigging.Action;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

public class FastBreak extends Module {
    private static FastBreak INSTANCE = new FastBreak();
    private final Timer timer = new Timer();
    public Setting mode;
    public Setting damage;
    public Setting webSwitch;
    public Setting doubleBreak;
    public Setting autosw;
    public Setting render;
    public Setting box;
    private final Setting boxAlpha;
    public Setting outline;
    private final Setting lineWidth;
    public BlockPos currentPos;
    public IBlockState currentBlockState;
    private int lasthotbarslot;

    public FastBreak() {
        super("FastBreak", "Speeds up mining.", Module.Category.PLAYER, true, false, false);
        this.mode = this.register(new Setting("Mode", FastBreak.Mode.PACKET));
        this.damage = this.register(new Setting("Damage", 0.7F, 0.0F, 1.0F, (v) -> {
            return this.mode.getValue() == FastBreak.Mode.DAMAGE;
        }));
        this.webSwitch = this.register(new Setting("WebSwitch", false));
        this.doubleBreak = this.register(new Setting("DoubleBreak", false));
        this.autosw = this.register(new Setting("AutoSwitch", false));
        this.render = this.register(new Setting("Render", false));
        this.box = this.register(new Setting("Box", false, (v) -> {
            return ((Boolean) this.render.getValue()).booleanValue();
        }));
        this.boxAlpha = this.register(new Setting("BoxAlpha", Integer.valueOf(85), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
            return ((Boolean) this.box.getValue()).booleanValue() && ((Boolean) this.render.getValue()).booleanValue();
        }));
        this.outline = this.register(new Setting("Outline", true, (v) -> {
            return ((Boolean) this.render.getValue()).booleanValue();
        }));
        this.lineWidth = this.register(new Setting("Width", 1.0F, 0.1F, 5.0F, (v) -> {
            return ((Boolean) this.outline.getValue()).booleanValue() && ((Boolean) this.render.getValue()).booleanValue();
        }));
        this.setInstance();
    }

    public static FastBreak getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FastBreak();
        }

        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    public void onTick() {
        if (this.currentPos != null) {
            if (mc.field_71441_e.func_180495_p(this.currentPos).equals(this.currentBlockState) && mc.field_71441_e.func_180495_p(this.currentPos).func_177230_c() != Blocks.field_150350_a) {
                if (((Boolean) this.webSwitch.getValue()).booleanValue() && this.currentBlockState.func_177230_c() == Blocks.field_150321_G && mc.field_71439_g.func_184614_ca().func_77973_b() instanceof ItemPickaxe) {
                    InventoryUtil.switchToHotbarSlot(ItemSword.class, false);
                }
            } else {
                this.currentPos = null;
                this.currentBlockState = null;
            }
        }

    }

    public void onUpdate() {
        if (!fullNullCheck()) {
            mc.field_71442_b.field_78781_i = 0;
        }
    }

    public void onRender3D(Render3DEvent event) {
        if (((Boolean) this.render.getValue()).booleanValue() && this.currentPos != null && this.currentBlockState.func_177230_c() == Blocks.field_150343_Z) {
            Color color = new Color(this.timer.passedMs((long) ((int) (2000.0F * Legacy.serverManager.getTpsFactor()))) ? 0 : 255, this.timer.passedMs((long) ((int) (2000.0F * Legacy.serverManager.getTpsFactor()))) ? 255 : 0, 0, 255);
            RenderUtil.drawBoxESP(this.currentPos, color, false, color, ((Float) this.lineWidth.getValue()).floatValue(), ((Boolean) this.outline.getValue()).booleanValue(), ((Boolean) this.box.getValue()).booleanValue(), ((Integer) this.boxAlpha.getValue()).intValue(), false);
            if (((Boolean) this.autosw.getValue()).booleanValue()) {
                boolean hasPickaxe = mc.field_71439_g.func_184614_ca().func_77973_b() == Items.field_151046_w;
                if (!hasPickaxe) {
                    for (int i = 0; i < 9; ++i) {
                        ItemStack stack = mc.field_71439_g.field_71071_by.func_70301_a(i);
                        if (!stack.func_190926_b()) {
                            this.lasthotbarslot = Surround.mc.field_71439_g.field_71071_by.field_70461_c;
                            if (mc.field_71439_g.field_71071_by.field_70461_c != this.lasthotbarslot) {
                                this.lasthotbarslot = mc.field_71439_g.field_71071_by.field_70461_c;
                            }

                            if (stack.func_77973_b() == Items.field_151046_w) {
                                hasPickaxe = true;
                                mc.field_71439_g.field_71071_by.field_70461_c = i;
                                mc.field_71442_b.func_78765_e();
                                break;
                            }
                        }
                    }
                }
            }
        }

    }

    @SubscribeEvent
    public void onBlockEvent(BlockEvent event) {
        if (!fullNullCheck()) {
            if (event.getStage() == 3 && mc.field_71442_b.field_78770_f > 0.1F) {
                mc.field_71442_b.field_78778_j = true;
            }

            if (event.getStage() == 4) {
                if (BlockUtil.canBreak(event.pos)) {
                    mc.field_71442_b.field_78778_j = false;
                    switch ((FastBreak.Mode) this.mode.getValue()) {
                        case PACKET:
                            if (this.currentPos == null) {
                                this.currentPos = event.pos;
                                this.currentBlockState = mc.field_71441_e.func_180495_p(this.currentPos);
                                this.timer.reset();
                            }

                            mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
                            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerDigging(Action.START_DESTROY_BLOCK, event.pos, event.facing));
                            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerDigging(Action.STOP_DESTROY_BLOCK, event.pos, event.facing));
                            event.setCanceled(true);
                            break;
                        case DAMAGE:
                            if (mc.field_71442_b.field_78770_f >= ((Float) this.damage.getValue()).floatValue()) {
                                mc.field_71442_b.field_78770_f = 1.0F;
                            }
                            break;
                        case INSTANT:
                            mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
                            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerDigging(Action.START_DESTROY_BLOCK, event.pos, event.facing));
                            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerDigging(Action.STOP_DESTROY_BLOCK, event.pos, event.facing));
                            mc.field_71442_b.func_187103_a(event.pos);
                            mc.field_71441_e.func_175698_g(event.pos);
                    }
                }

                BlockPos above;
                if (((Boolean) this.doubleBreak.getValue()).booleanValue() && BlockUtil.canBreak(above = event.pos.func_177982_a(0, 1, 0)) && mc.field_71439_g.func_70011_f((double) above.func_177958_n(), (double) above.func_177956_o(), (double) above.func_177952_p()) <= 5.0D) {
                    mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
                    mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerDigging(Action.START_DESTROY_BLOCK, above, event.facing));
                    mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerDigging(Action.STOP_DESTROY_BLOCK, above, event.facing));
                    mc.field_71442_b.func_187103_a(above);
                    mc.field_71441_e.func_175698_g(above);
                }
            }

        }
    }

    public String getDisplayInfo() {
        return this.mode.currentEnumName();
    }

    public static enum Mode {
        PACKET,
        DAMAGE,
        INSTANT;
    }
}
