package me.dev.legacy.impl.command.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.dev.legacy.Legacy;
import me.dev.legacy.impl.command.Command;
import me.dev.legacy.impl.setting.Bind;
import me.dev.legacy.modules.Module;
import org.lwjgl.input.Keyboard;

public class BindCommand extends Command {
    public BindCommand() {
        super("bind", new String[]{"<module>", "<bind>"});
    }

    public void execute(String[] commands) {
        if (commands.length == 1) {
            sendMessage("Please specify a module.");
        } else {
            String rkey = commands[1];
            String moduleName = commands[0];
            Module module = Legacy.moduleManager.getModuleByName(moduleName);
            if (module == null) {
                sendMessage("Unknown module '" + module + "'!");
            } else if (rkey == null) {
                sendMessage(module.getName() + " is bound to " + ChatFormatting.GRAY + module.getBind().toString());
            } else {
                int key = Keyboard.getKeyIndex(rkey.toUpperCase());
                if (rkey.equalsIgnoreCase("none")) {
                    key = -1;
                }

                if (key == 0) {
                    sendMessage("Unknown key '" + rkey + "'!");
                } else {
                    module.bind.setValue(new Bind(key));
                    sendMessage("Bind for " + ChatFormatting.GREEN + module.getName() + ChatFormatting.WHITE + " set to " + ChatFormatting.GRAY + rkey.toUpperCase());
                }
            }
        }
    }
}
