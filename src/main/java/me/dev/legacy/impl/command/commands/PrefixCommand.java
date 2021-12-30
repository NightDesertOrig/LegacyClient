package me.dev.legacy.impl.command.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.dev.legacy.Legacy;
import me.dev.legacy.impl.command.Command;

public class PrefixCommand extends Command {
    public PrefixCommand() {
        super("prefix", new String[]{"<char>"});
    }

    public void execute(String[] commands) {
        if (commands.length == 1) {
            Command.sendMessage(ChatFormatting.GREEN + "Current prefix is " + Legacy.commandManager.getPrefix());
        } else {
            Legacy.commandManager.setPrefix(commands[0]);
            Command.sendMessage("Prefix changed to " + ChatFormatting.GRAY + commands[0]);
        }
    }
}
