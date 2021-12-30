package me.dev.legacy.modules.combat;

import me.dev.legacy.Legacy;
import me.dev.legacy.api.util.PlayerUtil;
import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.modules.Module;
import net.minecraft.block.BlockAir;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.stream.Collectors;

public class SmartBurrow extends Module {
    private static SmartBurrow INSTANCE = new SmartBurrow();
    public Setting smartRange = this.register(new Setting("Smart Range", 2.5F, 1.0F, 10.0F));
    public Setting onlyInHole = this.register(new Setting("Hole Only", true));

    public SmartBurrow() {
        super("SmartBurrow", "Big smart", Module.Category.COMBAT, true, false, false);
        this.setInstance();
    }

    public static SmartBurrow getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SmartBurrow();
        }

        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    public void onUpdate() {
        if (!((Boolean) this.onlyInHole.getValue()).booleanValue() || PlayerUtil.isInHole(mc.field_71439_g)) {
            ArrayList entsSorted = (ArrayList) mc.field_71441_e.field_72996_f.stream().filter((entity) -> {
                return entity instanceof EntityPlayer && entity != mc.field_71439_g;
            }).sorted(Comparator.comparing((e) -> {
                return mc.field_71439_g.func_70032_d(e);
            })).collect(Collectors.toCollection(ArrayList::new));
            Collections.reverse(entsSorted);
            Burrow burrow = (Burrow) Legacy.moduleManager.getModuleByName("Burrow");
            BlockPos pos = new BlockPos(Math.floor(mc.field_71439_g.func_174791_d().field_72450_a), Math.floor(mc.field_71439_g.func_174791_d().field_72448_b + 0.2D), Math.floor(mc.field_71439_g.func_174791_d().field_72449_c));
            Iterator var4 = entsSorted.iterator();

            while (var4.hasNext()) {
                Entity ent = (Entity) var4.next();
                if (ent != mc.field_71439_g && mc.field_71439_g.func_70032_d(ent) < ((Float) this.smartRange.getValue()).floatValue() && !PlayerUtil.isInHole(ent) && !burrow.isEnabled() && mc.field_71441_e.func_180495_p(pos).func_177230_c() instanceof BlockAir) {
                    burrow.enable();
                    if (((Boolean) this.onlyInHole.getValue()).booleanValue()) {
                        return;
                    }

                    getInstance().disable();
                }
            }
        }

    }
}
