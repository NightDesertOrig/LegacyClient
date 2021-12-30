package me.dev.legacy;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import me.dev.legacy.modules.client.RPC;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;

public class DiscordPresence {
    public static DiscordRichPresence presence;
    private static final DiscordRPC rpc;
    private static Thread thread;
    private static int index = 1;

    public static void start() {
        DiscordEventHandlers handlers = new DiscordEventHandlers();
        rpc.Discord_Initialize("847105722956513312", handlers, true, "");
        presence.startTimestamp = System.currentTimeMillis() / 1000L;
        presence.details = Minecraft.func_71410_x().field_71462_r instanceof GuiMainMenu ? "Main menu" : "Playing " + (Minecraft.func_71410_x().field_71422_O != null ? (((Boolean) RPC.INSTANCE.showIP.getValue()).booleanValue() ? "on " + Minecraft.func_71410_x().field_71422_O.field_78845_b + "." : " multiplayer") : " singleplayer");
        presence.state = "Donbass activity";
        presence.largeImageKey = "legacy-1";
        presence.largeImageText = "v1.2.5";
        presence.smallImageKey = "";
        rpc.Discord_UpdatePresence(presence);
        thread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                rpc.Discord_RunCallbacks();
                presence.details = Minecraft.func_71410_x().field_71462_r instanceof GuiMainMenu ? "Main menu" : "Playing " + (Minecraft.func_71410_x().field_71422_O != null ? (((Boolean) RPC.INSTANCE.showIP.getValue()).booleanValue() ? "on " + Minecraft.func_71410_x().field_71422_O.field_78845_b + "." : " multiplayer") : " singleplayer");
                presence.state = "Eating kids";
                if (((Boolean) RPC.INSTANCE.users.getValue()).booleanValue()) {
                    if (index == 6) {
                        index = 1;
                    }

                    presence.smallImageKey = "user" + index;
                    ++index;
                    if (index == 2) {
                        presence.smallImageText = "BlackBro4";
                    }

                    if (index == 3) {
                        presence.smallImageText = "rianix";
                    }

                    if (index == 4) {
                        presence.smallImageText = "Sudmarin";
                    }

                    if (index == 5) {
                        presence.smallImageText = "Ziasan";
                    }

                    if (index == 6) {
                        presence.smallImageText = "end41r";
                    }
                }

                rpc.Discord_UpdatePresence(presence);

                try {
                    Thread.sleep(2000L);
                } catch (InterruptedException var1) {
                    ;
                }
            }

        }, "RPC-Callback-Handler");
        thread.start();
    }

    public static void stop() {
        if (thread != null && !thread.isInterrupted()) {
            thread.interrupt();
        }

        rpc.Discord_Shutdown();
    }

    static {
        rpc = DiscordRPC.INSTANCE;
        presence = new DiscordRichPresence();
    }
}
