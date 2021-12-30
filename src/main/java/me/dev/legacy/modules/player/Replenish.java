package me.dev.legacy.modules.player;

import me.dev.legacy.api.util.Timer;
import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.modules.Module;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;

public class Replenish extends Module {
    private final Setting delay = this.register(new Setting("Delay", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(10)));
    private final Setting gapStack = this.register(new Setting("GapStack", Integer.valueOf(32), Integer.valueOf(1), Integer.valueOf(64)));
    private final Setting xpStackAt = this.register(new Setting("XPStack", Integer.valueOf(32), Integer.valueOf(1), Integer.valueOf(64)));
    private final Timer timer = new Timer();
    private final ArrayList Hotbar = new ArrayList();

    public Replenish() {
        super("Replenish", "Replenishes your hotbar", Module.Category.PLAYER, false, false, false);
    }

    public void onEnable() {
        if (!fullNullCheck()) {
            this.Hotbar.clear();

            for (int l_I = 0; l_I < 9; ++l_I) {
                ItemStack l_Stack = mc.field_71439_g.field_71071_by.func_70301_a(l_I);
                if (!l_Stack.func_190926_b() && !this.Hotbar.contains(l_Stack.func_77973_b())) {
                    this.Hotbar.add(l_Stack.func_77973_b());
                } else {
                    this.Hotbar.add(Items.field_190931_a);
                }
            }

        }
    }

    public void onUpdate() {
        if (mc.field_71462_r == null) {
            if (this.timer.passedMs((long) (((Integer) this.delay.getValue()).intValue() * 1000))) {
                for (int l_I = 0; l_I < 9; ++l_I) {
                    if (this.RefillSlotIfNeed(l_I)) {
                        this.timer.reset();
                        return;
                    }
                }

            }
        }
    }

    private boolean RefillSlotIfNeed(int p_Slot) {
        ItemStack l_Stack = mc.field_71439_g.field_71071_by.func_70301_a(p_Slot);
        if (!l_Stack.func_190926_b() && l_Stack.func_77973_b() != Items.field_190931_a) {
            if (!l_Stack.func_77985_e()) {
                return false;
            } else if (l_Stack.func_190916_E() >= l_Stack.func_77976_d()) {
                return false;
            } else if (l_Stack.func_77973_b().equals(Items.field_151153_ao) && l_Stack.func_190916_E() >= ((Integer) this.gapStack.getValue()).intValue()) {
                return false;
            } else if (l_Stack.func_77973_b().equals(Items.field_151062_by) && l_Stack.func_190916_E() > ((Integer) this.xpStackAt.getValue()).intValue()) {
                return false;
            } else {
                for (int l_I = 9; l_I < 36; ++l_I) {
                    ItemStack l_Item = mc.field_71439_g.field_71071_by.func_70301_a(l_I);
                    if (!l_Item.func_190926_b() && this.CanItemBeMergedWith(l_Stack, l_Item)) {
                        mc.field_71442_b.func_187098_a(mc.field_71439_g.field_71069_bz.field_75152_c, l_I, 0, ClickType.QUICK_MOVE, mc.field_71439_g);
                        mc.field_71442_b.func_78765_e();
                        return true;
                    }
                }

                return false;
            }
        } else {
            return false;
        }
    }

    private boolean CanItemBeMergedWith(ItemStack p_Source, ItemStack p_Target) {
        return p_Source.func_77973_b() == p_Target.func_77973_b() && p_Source.func_82833_r().equals(p_Target.func_82833_r());
    }
}
