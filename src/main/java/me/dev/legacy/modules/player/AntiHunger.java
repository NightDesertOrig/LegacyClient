package me.dev.legacy.modules.player;

import me.dev.legacy.modules.Module;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.network.play.client.CPacketPlayer;

import java.util.function.Predicate;

public class AntiHunger extends Module {
    @EventHandler
    public Listener packetListener = new Listener((event) -> {
        if (event.getPacket() instanceof CPacketPlayer) {
            ((CPacketPlayer) event.getPacket()).field_149474_g = false;
        }

    }, new Predicate[0]);

    public AntiHunger() {
        super("AntiHunger", "AntiHunger", Module.Category.PLAYER, true, false, false);
    }
}
