package me.dev.legacy.modules.misc;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.dev.legacy.impl.command.Command;
import me.dev.legacy.modules.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class BurrowAlert extends Module {
    private final ConcurrentHashMap players = new ConcurrentHashMap();
    List anti_spam = new ArrayList();
    List burrowedPlayers = new ArrayList();

    public BurrowAlert() {
        super("BurrowAlert", "Burrow Alert", Module.Category.MISC, true, false, false);
    }

    public void onEnable() {
        this.players.clear();
        this.anti_spam.clear();
    }

    public void onUpdate() {
        if (mc.field_71439_g != null && mc.field_71441_e != null) {
            Iterator var1 = ((List) mc.field_71441_e.field_72996_f.stream().filter((e) -> {
                return e instanceof EntityPlayer;
            }).collect(Collectors.toList())).iterator();

            while (true) {
                while (true) {
                    Entity entity;
                    do {
                        if (!var1.hasNext()) {
                            return;
                        }

                        entity = (Entity) var1.next();
                    } while (!(entity instanceof EntityPlayer));

                    if (!this.burrowedPlayers.contains(entity) && this.isBurrowed(entity)) {
                        this.burrowedPlayers.add(entity);
                        Command.sendMessage(ChatFormatting.RED + entity.func_70005_c_() + " has just burrowed!");
                    } else if (this.burrowedPlayers.contains(entity) && !this.isBurrowed(entity)) {
                        this.burrowedPlayers.remove(entity);
                        Command.sendMessage(ChatFormatting.GREEN + entity.func_70005_c_() + " is no longer burrowed!");
                    }
                }
            }
        }
    }

    private boolean isBurrowed(Entity entity) {
        BlockPos entityPos = new BlockPos(this.roundValueToCenter(entity.field_70165_t), entity.field_70163_u + 0.2D, this.roundValueToCenter(entity.field_70161_v));
        return mc.field_71441_e.func_180495_p(entityPos).func_177230_c() == Blocks.field_150343_Z || mc.field_71441_e.func_180495_p(entityPos).func_177230_c() == Blocks.field_150477_bB;
    }

    private double roundValueToCenter(double inputVal) {
        double roundVal = (double) Math.round(inputVal);
        if (roundVal > inputVal) {
            roundVal -= 0.5D;
        } else if (roundVal <= inputVal) {
            roundVal += 0.5D;
        }

        return roundVal;
    }
}
