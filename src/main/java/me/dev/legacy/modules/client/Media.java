package me.dev.legacy.modules.client;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.dev.legacy.impl.command.Command;
import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.modules.Module;

public class Media extends Module {
    public final Setting NameString = this.register(new Setting("Name", "New Name Here..."));
    private static Media instance;

    public Media() {
        super("Media", "Changes name", Module.Category.CLIENT, false, false, false);
        instance = this;
    }

    public void onEnable() {
        Command.sendMessage(ChatFormatting.GRAY + "Success! Name succesfully changed to " + ChatFormatting.GREEN + (String) this.NameString.getValue());
    }

    public static Media getInstance() {
        if (instance == null) {
            instance = new Media();
        }

        return instance;
    }
}
