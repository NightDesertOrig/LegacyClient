package me.dev.legacy.modules.combat;

import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.modules.Module;
import net.minecraft.network.play.client.CPacketPlayer.Position;
import net.minecraft.util.text.TextComponentString;

public class AutoLog extends Module {
    public final Setting packetKick = this.register(new Setting("Packet Kick", false));
    public final Setting fakeKick = this.register(new Setting("Fake Kick", false));
    public final Setting Health = this.register(new Setting("Health", Integer.valueOf(6), Integer.valueOf(1), Integer.valueOf(20)));

    public AutoLog() {
        super("AutoLog", "Automatically logs on combat.", Module.Category.COMBAT, true, false, false);
    }

    public void onTick() {
        if (mc.field_71439_g != null && mc.field_71441_e != null && !mc.field_71439_g.field_71075_bZ.field_75098_d) {
            float health = mc.field_71439_g.func_110143_aJ();
            if (health <= (float) ((Integer) this.Health.getValue()).intValue() && health != 0.0F && !mc.field_71439_g.field_70128_L) {
                this.doLog();
                this.toggle();
            }

        }
    }

    public void doLog() {
        if (((Boolean) this.packetKick.getValue()).booleanValue()) {
            mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 50.0D, mc.field_71439_g.field_70161_v, false));
        }

        if (((Boolean) this.fakeKick.getValue()).booleanValue()) {
            mc.field_71439_g.field_71174_a.func_147298_b().func_150718_a(new TextComponentString("Internal Exception: java.lang.NullPointerException"));
        } else {
            mc.field_71439_g.field_71174_a.func_147298_b().func_150718_a(new TextComponentString("Auto Log!"));
        }
    }
}
