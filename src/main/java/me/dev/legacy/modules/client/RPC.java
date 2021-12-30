package me.dev.legacy.modules.client;

import me.dev.legacy.DiscordPresence;
import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.modules.Module;

public class RPC extends Module {
    public static RPC INSTANCE;
    public Setting showIP = this.register(new Setting("IP", false));
    public Setting users = this.register(new Setting("Users", false));

    public RPC() {
        super("RPC", "Discord rich presence", Module.Category.CLIENT, false, false, false);
        INSTANCE = this;
    }

    public void onEnable() {
        DiscordPresence.start();
    }

    public void onDisable() {
        DiscordPresence.stop();
    }
}
