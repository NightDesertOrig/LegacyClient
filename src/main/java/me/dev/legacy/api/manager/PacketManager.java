package me.dev.legacy.api.manager;

import me.dev.legacy.api.AbstractModule;
import net.minecraft.network.Packet;

import java.util.ArrayList;
import java.util.List;

public class PacketManager extends AbstractModule {
    private final List noEventPackets = new ArrayList();

    public void sendPacketNoEvent(Packet packet) {
        if (packet != null && !nullCheck()) {
            this.noEventPackets.add(packet);
            mc.field_71439_g.field_71174_a.func_147297_a(packet);
        }

    }

    public boolean shouldSendPacket(Packet packet) {
        if (this.noEventPackets.contains(packet)) {
            this.noEventPackets.remove(packet);
            return false;
        } else {
            return true;
        }
    }
}
