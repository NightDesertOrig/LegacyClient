package me.dev.legacy.modules.combat;

import me.dev.legacy.api.util.BlockUtil;
import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.modules.Module;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.tileentity.TileEntityBed;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;

import java.util.Comparator;

public class AutoBed extends Module {
    boolean moving = false;
    Setting range = this.register(new Setting("Range", 4.5D, 0.0D, 10.0D));
    Setting rotate = this.register(new Setting("Rotate", true));
    Setting dimensionCheck = this.register(new Setting("DimensionCheck", true));
    Setting refill = this.register(new Setting("RefillBed", true));

    public AutoBed() {
        super("AutoBed", "Fucked (Future)", Module.Category.COMBAT, true, false, false);
    }

    public void onUpdate() {
        if (((Boolean) this.refill.getValue()).booleanValue()) {
            int slot = -1;

            int t;
            for (t = 0; t < 9; ++t) {
                if (mc.field_71439_g.field_71071_by.func_70301_a(t) == ItemStack.field_190927_a) {
                    slot = t;
                    break;
                }
            }

            if (this.moving && slot != -1) {
                mc.field_71442_b.func_187098_a(0, slot + 36, 0, ClickType.PICKUP, mc.field_71439_g);
                this.moving = false;
                slot = -1;
            }

            if (slot != -1 && !(mc.field_71462_r instanceof GuiContainer) && mc.field_71439_g.field_71071_by.func_70445_o().func_190926_b()) {
                t = -1;

                for (int i = 0; i < 45; ++i) {
                    if (mc.field_71439_g.field_71071_by.func_70301_a(i).func_77973_b() == Items.field_151104_aV && i >= 9) {
                        t = i;
                        break;
                    }
                }

                if (t != -1) {
                    mc.field_71442_b.func_187098_a(0, t, 0, ClickType.PICKUP, mc.field_71439_g);
                    this.moving = true;
                }
            }
        }

        mc.field_71441_e.field_147482_g.stream().filter((e) -> {
            return e instanceof TileEntityBed;
        }).filter((e) -> {
            return mc.field_71439_g.func_70011_f((double) e.func_174877_v().func_177958_n(), (double) e.func_174877_v().func_177956_o(), (double) e.func_174877_v().func_177952_p()) <= ((Double) this.range.getValue()).doubleValue();
        }).sorted(Comparator.comparing((e) -> {
            return mc.field_71439_g.func_70011_f((double) e.func_174877_v().func_177958_n(), (double) e.func_174877_v().func_177956_o(), (double) e.func_174877_v().func_177952_p());
        })).forEach((bed) -> {
            if (!((Boolean) this.dimensionCheck.getValue()).booleanValue() || mc.field_71439_g.field_71093_bK != 0) {
                if (((Boolean) this.rotate.getValue()).booleanValue()) {
                    BlockUtil.faceVectorPacketInstant(new Vec3d(bed.func_174877_v().func_177963_a(0.5D, 0.5D, 0.5D)));
                }

                mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerTryUseItemOnBlock(bed.func_174877_v(), EnumFacing.UP, EnumHand.MAIN_HAND, 0.0F, 0.0F, 0.0F));
            }
        });
    }
}
