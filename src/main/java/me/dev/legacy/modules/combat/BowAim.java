package me.dev.legacy.modules.combat;

import me.dev.legacy.Legacy;
import me.dev.legacy.api.util.EntityUtil;
import me.dev.legacy.api.util.MathUtil;
import me.dev.legacy.modules.Module;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBow;
import net.minecraft.util.math.Vec3d;

import java.util.Iterator;

public class BowAim extends Module {
    public BowAim() {
        super("BowAim", "BowAim", Module.Category.COMBAT, true, false, false);
    }

    public void onUpdate() {
        if (mc.field_71439_g.func_184614_ca().func_77973_b() instanceof ItemBow && mc.field_71439_g.func_184587_cr() && mc.field_71439_g.func_184612_cw() >= 3) {
            EntityPlayer player = null;
            float tickDis = 100.0F;
            Iterator var3 = mc.field_71441_e.field_73010_i.iterator();

            while (var3.hasNext()) {
                EntityPlayer p = (EntityPlayer) var3.next();
                if (!(p instanceof EntityPlayerSP) && !Legacy.friendManager.isFriend(p.func_70005_c_())) {
                    float dis = p.func_70032_d(mc.field_71439_g);
                    if (dis < tickDis) {
                        tickDis = dis;
                        player = p;
                    }
                }
            }

            if (player != null) {
                Vec3d pos = EntityUtil.getInterpolatedPos(player, mc.func_184121_ak());
                float[] angels = MathUtil.calcAngle(EntityUtil.getInterpolatedPos(mc.field_71439_g, mc.func_184121_ak()), pos);
                mc.field_71439_g.field_70177_z = angels[0];
                mc.field_71439_g.field_70125_A = angels[1];
            }
        }

    }
}
