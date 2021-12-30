package me.dev.legacy.modules.player;

import me.dev.legacy.api.event.events.other.PacketEvent;
import me.dev.legacy.api.util.MathUtil;
import me.dev.legacy.api.util.Timer;
import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.modules.Module;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Blink extends Module {
    public Setting cPacketPlayer = this.register(new Setting("CPacketPlayer", true));
    public Setting autoOff;
    public Setting timeLimit;
    public Setting packetLimit;
    public Setting distance;
    private Timer timer;
    private Queue packets;
    private EntityOtherPlayerMP entity;
    private int packetsCanceled;
    private BlockPos startPos;
    private static Blink INSTANCE = new Blink();

    public Blink() {
        super("Blink", "Fake lag.", Module.Category.PLAYER, true, false, false);
        this.autoOff = this.register(new Setting("AutoOff", Blink.Mode.MANUAL));
        this.timeLimit = this.register(new Setting("Time", Integer.valueOf(20), Integer.valueOf(1), Integer.valueOf(500), (v) -> {
            return this.autoOff.getValue() == Blink.Mode.TIME;
        }));
        this.packetLimit = this.register(new Setting("Packets", Integer.valueOf(20), Integer.valueOf(1), Integer.valueOf(500), (v) -> {
            return this.autoOff.getValue() == Blink.Mode.PACKETS;
        }));
        this.distance = this.register(new Setting("Distance", 10.0F, 1.0F, 100.0F, (v) -> {
            return this.autoOff.getValue() == Blink.Mode.DISTANCE;
        }));
        this.timer = new Timer();
        this.packets = new ConcurrentLinkedQueue();
        this.packetsCanceled = 0;
        this.startPos = null;
        this.setInstance();
    }

    private void setInstance() {
        INSTANCE = this;
    }

    public static Blink getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Blink();
        }

        return INSTANCE;
    }

    public void onEnable() {
        if (!fullNullCheck()) {
            this.entity = new EntityOtherPlayerMP(mc.field_71441_e, mc.field_71449_j.func_148256_e());
            this.entity.func_82149_j(mc.field_71439_g);
            this.entity.field_70177_z = mc.field_71439_g.field_70177_z;
            this.entity.field_70759_as = mc.field_71439_g.field_70759_as;
            this.entity.field_71071_by.func_70455_b(mc.field_71439_g.field_71071_by);
            mc.field_71441_e.func_73027_a(6942069, this.entity);
            this.startPos = mc.field_71439_g.func_180425_c();
        } else {
            this.disable();
        }

        this.packetsCanceled = 0;
        this.timer.reset();
    }

    public void onUpdate() {
        if (nullCheck() || this.autoOff.getValue() == Blink.Mode.TIME && this.timer.passedS((double) ((Integer) this.timeLimit.getValue()).intValue()) || this.autoOff.getValue() == Blink.Mode.DISTANCE && this.startPos != null && mc.field_71439_g.func_174818_b(this.startPos) >= MathUtil.square((double) ((Float) this.distance.getValue()).floatValue()) || this.autoOff.getValue() == Blink.Mode.PACKETS && this.packetsCanceled >= ((Integer) this.packetLimit.getValue()).intValue()) {
            this.disable();
        }

    }

    public void onLogout() {
        if (this.isOn()) {
            this.disable();
        }

    }

    @SubscribeEvent
    public void onSendPacket(PacketEvent.Send event) {
        if (event.getStage() == 0 && mc.field_71441_e != null && !mc.func_71356_B()) {
            Object packet = event.getPacket();
            if (((Boolean) this.cPacketPlayer.getValue()).booleanValue() && packet instanceof CPacketPlayer) {
                event.setCanceled(true);
                this.packets.add((Packet) packet);
                ++this.packetsCanceled;
            }

            if (!((Boolean) this.cPacketPlayer.getValue()).booleanValue()) {
                if (packet instanceof CPacketChatMessage || packet instanceof CPacketConfirmTeleport || packet instanceof CPacketKeepAlive || packet instanceof CPacketTabComplete || packet instanceof CPacketClientStatus) {
                    return;
                }

                this.packets.add((Packet) packet);
                event.setCanceled(true);
                ++this.packetsCanceled;
            }
        }

    }

    public void onDisable() {
        if (!fullNullCheck()) {
            mc.field_71441_e.func_72900_e(this.entity);

            while (!this.packets.isEmpty()) {
                mc.field_71439_g.field_71174_a.func_147297_a((Packet) this.packets.poll());
            }
        }

        this.startPos = null;
    }

    public static enum Mode {
        MANUAL,
        TIME,
        DISTANCE,
        PACKETS;
    }
}
