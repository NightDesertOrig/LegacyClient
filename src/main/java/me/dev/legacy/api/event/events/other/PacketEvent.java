package me.dev.legacy.api.event.events.other;

import me.dev.legacy.api.event.EventStage;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

public class PacketEvent extends EventStage {
    private final net.minecraft.network.Packet packet;

    public PacketEvent(int stage, net.minecraft.network.Packet packet) {
        super(stage);
        this.packet = packet;
    }

    public net.minecraft.network.Packet getPacket() {
        return this.packet;
    }

    @Cancelable
    public static class Receive extends PacketEvent {
        public Receive(int stage, net.minecraft.network.Packet packet) {
            super(stage, packet);
        }
    }

    @Cancelable
    public static class Send extends PacketEvent {
        public Send(int stage, net.minecraft.network.Packet packet) {
            super(stage, packet);
        }
    }
}
