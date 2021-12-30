package me.dev.legacy.modules.movement;

import io.netty.util.internal.ConcurrentSet;
import me.dev.legacy.api.AbstractModule;
import me.dev.legacy.api.event.events.move.MoveEvent;
import me.dev.legacy.api.event.events.move.PushEvent;
import me.dev.legacy.api.event.events.move.UpdateWalkingPlayerEvent;
import me.dev.legacy.api.event.events.other.PacketEvent;
import me.dev.legacy.api.util.EntityUtil;
import me.dev.legacy.api.util.Timer;
import me.dev.legacy.modules.Module;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayer.Position;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class PacketFly extends Module {
    private final Set packets = new ConcurrentSet();
    private final Map teleportmap = new ConcurrentHashMap();
    private int flightCounter = 0;
    private int teleportID = 0;
    private static PacketFly instance;
    private boolean setMove = false;
    private boolean nocliperino = true;

    public PacketFly() {
        super("PacketFly", "WAN BLOCK BLYAT!", Module.Category.MOVEMENT, true, false, false);
        instance = this;
    }

    public static PacketFly getInstance() {
        if (instance == null) {
            instance = new PacketFly();
        }

        return instance;
    }

    public void onToggle() {
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
        if (event.getStage() != 1) {
            mc.field_71439_g.func_70016_h(0.0D, 0.0D, 0.0D);
            boolean checkCollisionBoxes = this.checkHitBoxes();
            double speed = mc.field_71439_g.field_71158_b.field_78901_c && (checkCollisionBoxes || !EntityUtil.isMoving()) ? (checkCollisionBoxes ? 0.062D : (this.resetCounter(10) ? -0.032D : 0.062D)) : (mc.field_71439_g.field_71158_b.field_78899_d ? -0.062D : (checkCollisionBoxes ? 0.0D : (this.resetCounter(4) ? -0.04D : 0.0D)));
            if (checkCollisionBoxes && EntityUtil.isMoving() && speed != 0.0D) {
                double antiFactor = 2.5D;
                speed /= 2.5D;
            }

            boolean strafeFactor = true;
            double[] strafing = this.getMotion(checkCollisionBoxes ? 0.031D : 0.26D);
            int loops = 1;

            for (int i = 1; i < loops + 1; ++i) {
                double extraFactor = 1.0D;
                mc.field_71439_g.field_70159_w = strafing[0] * (double) i * 1.0D;
                mc.field_71439_g.field_70181_x = speed * (double) i;
                mc.field_71439_g.field_70179_y = strafing[1] * (double) i * 1.0D;
                boolean sendTeleport = true;
                this.sendPackets(mc.field_71439_g.field_70159_w, mc.field_71439_g.field_70181_x, mc.field_71439_g.field_70179_y, true);
            }

        }
    }

    @SubscribeEvent
    public void onMove(MoveEvent event) {
        if (this.setMove && this.flightCounter != 0) {
            event.setX(mc.field_71439_g.field_70159_w);
            event.setY(mc.field_71439_g.field_70181_x);
            event.setZ(mc.field_71439_g.field_70179_y);
            if (this.nocliperino && this.checkHitBoxes()) {
                mc.field_71439_g.field_70145_X = true;
            }
        }

    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketPlayer && !this.packets.remove(event.getPacket())) {
            event.setCanceled(true);
        }

    }

    @SubscribeEvent
    public void onPushOutOfBlocks(PushEvent event) {
        if (event.getStage() == 1) {
            event.setCanceled(true);
        }

    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketPlayerPosLook && !AbstractModule.fullNullCheck()) {
            SPacketPlayerPosLook packet = (SPacketPlayerPosLook) event.getPacket();
            if (mc.field_71439_g.func_70089_S() && mc.field_71441_e.func_175668_a(new BlockPos(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v), false) && !(mc.field_71462_r instanceof GuiDownloadTerrain)) {
                this.teleportmap.remove(packet.func_186965_f());
            }

            this.teleportID = packet.func_186965_f();
        }

    }

    private boolean checkHitBoxes() {
        return !mc.field_71441_e.func_184144_a(mc.field_71439_g, mc.field_71439_g.func_174813_aQ().func_72321_a(-0.0D, -0.1D, -0.0D)).isEmpty();
    }

    private boolean resetCounter(int counter) {
        if (++this.flightCounter >= counter) {
            this.flightCounter = 0;
            return true;
        } else {
            return false;
        }
    }

    private double[] getMotion(double speed) {
        float moveForward = mc.field_71439_g.field_71158_b.field_192832_b;
        float moveStrafe = mc.field_71439_g.field_71158_b.field_78902_a;
        float rotationYaw = mc.field_71439_g.field_70126_B + (mc.field_71439_g.field_70177_z - mc.field_71439_g.field_70126_B) * mc.func_184121_ak();
        if (moveForward != 0.0F) {
            if (moveStrafe > 0.0F) {
                rotationYaw += (float) (moveForward > 0.0F ? -45 : 45);
            } else if (moveStrafe < 0.0F) {
                rotationYaw += (float) (moveForward > 0.0F ? 45 : -45);
            }

            moveStrafe = 0.0F;
            if (moveForward > 0.0F) {
                moveForward = 1.0F;
            } else if (moveForward < 0.0F) {
                moveForward = -1.0F;
            }
        }

        double posX = (double) moveForward * speed * -Math.sin(Math.toRadians((double) rotationYaw)) + (double) moveStrafe * speed * Math.cos(Math.toRadians((double) rotationYaw));
        double posZ = (double) moveForward * speed * Math.cos(Math.toRadians((double) rotationYaw)) - (double) moveStrafe * speed * -Math.sin(Math.toRadians((double) rotationYaw));
        return new double[]{posX, posZ};
    }

    private void sendPackets(double x, double y, double z, boolean teleport) {
        Vec3d vec = new Vec3d(x, y, z);
        Vec3d position = mc.field_71439_g.func_174791_d().func_178787_e(vec);
        Vec3d outOfBoundsVec = this.outOfBoundsVec(position);
        this.packetSender(new Position(position.field_72450_a, position.field_72448_b, position.field_72449_c, mc.field_71439_g.field_70122_E));
        this.packetSender(new Position(outOfBoundsVec.field_72450_a, outOfBoundsVec.field_72448_b, outOfBoundsVec.field_72449_c, mc.field_71439_g.field_70122_E));
        this.teleportPacket(position, teleport);
    }

    private void teleportPacket(Vec3d pos, boolean shouldTeleport) {
        if (shouldTeleport) {
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketConfirmTeleport(++this.teleportID));
            this.teleportmap.put(this.teleportID, new PacketFly.IDtime(pos, new Timer()));
        }

    }

    private Vec3d outOfBoundsVec(Vec3d position) {
        return position.func_72441_c(0.0D, 1337.0D, 0.0D);
    }

    private void packetSender(CPacketPlayer packet) {
        this.packets.add(packet);
        mc.field_71439_g.field_71174_a.func_147297_a(packet);
    }

    public static class IDtime {
        private final Vec3d pos;
        private final Timer timer;

        public IDtime(Vec3d pos, Timer timer) {
            this.pos = pos;
            (this.timer = timer).reset();
        }

        public Vec3d getPos() {
            return this.pos;
        }

        public Timer getTimer() {
            return this.timer;
        }
    }
}
