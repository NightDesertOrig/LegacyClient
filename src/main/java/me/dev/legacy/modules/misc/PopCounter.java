package me.dev.legacy.modules.misc;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.dev.legacy.impl.command.Command;
import me.dev.legacy.modules.Module;
import net.minecraft.entity.player.EntityPlayer;

import java.util.HashMap;

public class PopCounter extends Module {
    public static HashMap TotemPopContainer = new HashMap();
    public static PopCounter INSTANCE = new PopCounter();

    public PopCounter() {
        super("PopCounter", "Counts other players totem pops.", Module.Category.MISC, true, false, false);
        this.setInstance();
    }

    public static PopCounter getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PopCounter();
        }

        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    public void onEnable() {
        TotemPopContainer.clear();
    }

    public void onDeath(EntityPlayer player) {
        if (TotemPopContainer.containsKey(player.func_70005_c_())) {
            int l_Count = ((Integer) TotemPopContainer.get(player.func_70005_c_())).intValue();
            TotemPopContainer.remove(player.func_70005_c_());
            if (l_Count == 1) {
                Command.sendMessage(ChatFormatting.WHITE + player.func_70005_c_() + ChatFormatting.LIGHT_PURPLE + " died after popping " + ChatFormatting.WHITE + l_Count + ChatFormatting.LIGHT_PURPLE + " totem " + ChatFormatting.BOLD);
            } else {
                Command.sendMessage(ChatFormatting.WHITE + player.func_70005_c_() + ChatFormatting.LIGHT_PURPLE + " died after popping " + ChatFormatting.WHITE + l_Count + ChatFormatting.LIGHT_PURPLE + " totems " + ChatFormatting.BOLD);
            }
        }

    }

    public void onTotemPop(EntityPlayer player) {
        if (!fullNullCheck()) {
            if (!mc.field_71439_g.equals(player)) {
                int l_Count = 1;
                if (TotemPopContainer.containsKey(player.func_70005_c_())) {
                    l_Count = ((Integer) TotemPopContainer.get(player.func_70005_c_())).intValue();
                    HashMap var10000 = TotemPopContainer;
                    String var10001 = player.func_70005_c_();
                    ++l_Count;
                    var10000.put(var10001, l_Count);
                } else {
                    TotemPopContainer.put(player.func_70005_c_(), l_Count);
                }

                if (l_Count == 1) {
                    Command.sendMessage(ChatFormatting.WHITE + player.func_70005_c_() + ChatFormatting.LIGHT_PURPLE + " just popped " + ChatFormatting.WHITE + l_Count + ChatFormatting.LIGHT_PURPLE + " totem " + ChatFormatting.BOLD);
                } else {
                    Command.sendMessage(ChatFormatting.WHITE + player.func_70005_c_() + ChatFormatting.LIGHT_PURPLE + " just popped " + ChatFormatting.WHITE + l_Count + ChatFormatting.LIGHT_PURPLE + " totems " + ChatFormatting.BOLD);
                }

            }
        }
    }
}
