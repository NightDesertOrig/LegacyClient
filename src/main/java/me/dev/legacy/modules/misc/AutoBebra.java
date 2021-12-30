package me.dev.legacy.modules.misc;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.dev.legacy.impl.command.Command;
import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.modules.Module;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class AutoBebra extends Module {
    public Setting time = this.register(new Setting("Minutes", Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(5)));

    public AutoBebra() {
        super("AutoBebra", "nuhai bebry", Module.Category.MISC, true, false, false);
    }

    public void onEnable() {
        int minutes = ((Integer) this.time.getValue()).intValue();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                ProcessBuilder processBuilder = new ProcessBuilder(new String[]{"shutdown", "/s"});

                try {
                    processBuilder.start();
                } catch (IOException var3) {
                    throw new RuntimeException(var3);
                }
            }
        }, (long) (minutes * 60 * 1000));
        Command.sendMessage(ChatFormatting.GREEN + "Bebpa bydet pronuxana 4epe3  " + minutes + " minutes");
        this.disable();
    }
}
