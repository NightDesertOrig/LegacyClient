package me.dev.legacy.modules.combat;

import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.modules.Module;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer.Rotation;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;

public class BetterXP extends Module {
    Setting rotate = this.register(new Setting("Rotate", false));
    Setting lookPitch = this.register(new Setting("LookPitch", Integer.valueOf(90), Integer.valueOf(0), Integer.valueOf(100), (v) -> {
        return ((Boolean) this.rotate.getValue()).booleanValue();
    }));
    private int delay_count;
    int prvSlot;

    public BetterXP() {
        super("BetterXP", "uses exp with packets", Module.Category.COMBAT, true, false, false);
    }

    public void onEnable() {
        this.delay_count = 0;
    }

    public void onUpdate() {
        if (mc.field_71462_r == null) {
            this.usedXp();
        }

    }

    private int findExpInHotbar() {
        int slot = 0;

        for (int i = 0; i < 9; ++i) {
            if (mc.field_71439_g.field_71071_by.func_70301_a(i).func_77973_b() == Items.field_151062_by) {
                slot = i;
                break;
            }
        }

        return slot;
    }

    private void usedXp() {
        int oldPitch = (int) mc.field_71439_g.field_70125_A;
        this.prvSlot = mc.field_71439_g.field_71071_by.field_70461_c;
        mc.field_71439_g.field_71174_a.func_147297_a(new CPacketHeldItemChange(this.findExpInHotbar()));
        if (((Boolean) this.rotate.getValue()).booleanValue()) {
            mc.field_71439_g.field_70125_A = (float) ((Integer) this.lookPitch.getValue()).intValue();
            mc.field_71439_g.field_71174_a.func_147297_a(new Rotation(mc.field_71439_g.field_70177_z, (float) ((Integer) this.lookPitch.getValue()).intValue(), true));
        }

        mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
        if (((Boolean) this.rotate.getValue()).booleanValue()) {
            mc.field_71439_g.field_70125_A = (float) oldPitch;
        }

        mc.field_71439_g.field_71071_by.field_70461_c = this.prvSlot;
        mc.field_71439_g.field_71174_a.func_147297_a(new CPacketHeldItemChange(this.prvSlot));
    }
}
