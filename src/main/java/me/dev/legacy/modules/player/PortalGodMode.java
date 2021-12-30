package me.dev.legacy.modules.player;

import me.dev.legacy.modules.Module;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.network.play.client.CPacketConfirmTeleport;

import java.util.function.Predicate;

public class PortalGodMode extends Module {
    @EventHandler
    public Listener listener = new Listener((event) -> {
        if (this.isEnabled() && event.getPacket() instanceof CPacketConfirmTeleport) {
            event.setCanceled(true);
        }

    }, new Predicate[0]);

    public PortalGodMode() {
        super("PortalGodMode", "PortalGodMode", Module.Category.PLAYER, true, false, false);
    }
}
