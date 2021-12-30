package me.dev.legacy.modules.client;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.dev.legacy.Legacy;
import me.dev.legacy.modules.Module;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScorePlayerTeam;

public class TabFriends extends Module {
    public static TabFriends INSTANCE;

    public TabFriends() {
        super("TabFriends", "TabModify", Module.Category.CLIENT, true, false, false);
        INSTANCE = this;
    }

    public static String getPlayerName(NetworkPlayerInfo networkPlayerInfoIn) {
        String dname = networkPlayerInfoIn.func_178854_k() != null ? networkPlayerInfoIn.func_178854_k().func_150254_d() : ScorePlayerTeam.func_96667_a(networkPlayerInfoIn.func_178850_i(), networkPlayerInfoIn.func_178845_a().getName());
        return Legacy.friendManager.isFriend(dname) ? ChatFormatting.BOLD + ChatFormatting.LIGHT_PURPLE.toString() + dname : dname;
    }
}
