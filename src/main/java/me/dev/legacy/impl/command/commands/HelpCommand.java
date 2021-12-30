package me.dev.legacy.impl.command.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.dev.legacy.Legacy;
import me.dev.legacy.impl.command.Command;

import java.util.Iterator;

public class HelpCommand extends Command {
    public HelpCommand() {
        super("help");
    }

    public void execute(String[] commands) {
        sendMessage("Commands: ");
        Iterator var2 = Legacy.commandManager.getCommands().iterator();

        while (var2.hasNext()) {
            Command command = (Command) var2.next();
            sendMessage(ChatFormatting.GRAY + Legacy.commandManager.getPrefix() + command.getName());
        }

    }
}
