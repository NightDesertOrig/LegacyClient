package me.dev.legacy.modules.movement;

import me.dev.legacy.api.event.events.other.PacketEvent;
import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.modules.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.item.ItemBoat;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketInput;
import net.minecraft.network.play.client.CPacketPlayer.Rotation;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketVehicleMove;
import net.minecraft.network.play.server.SPacketMoveVehicle;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Comparator;

public class BoatFly extends Module {
    public Setting speed = this.register(new Setting("Speed", 3.0D, 1.0D, 10.0D));
    public Setting verticalSpeed = this.register(new Setting("VerticalSpeed", 3.0D, 1.0D, 10.0D));
    public Setting placeBypass = this.register(new Setting("PlaceBypass", true));
    public Setting bypass = this.register(new Setting("Bypass", true));
    public Setting noKick = this.register(new Setting("No-Kick", true));
    public Setting packet = this.register(new Setting("Packet", true));
    public Setting packets = this.register(new Setting("Packets", Integer.valueOf(3), Integer.valueOf(1), Integer.valueOf(5), (v) -> {
        return ((Boolean) this.packet.getValue()).booleanValue();
    }));
    public Setting interact = this.register(new Setting("Delay", Integer.valueOf(2), Integer.valueOf(1), Integer.valueOf(20)));
    public static BoatFly INSTANCE;
    private EntityBoat target;
    private int teleportID;
    private int packetCounter = 0;

    public BoatFly() {
        super("BoatFly", "/fly but boat", Module.Category.MOVEMENT, true, false, false);
        INSTANCE = this;
    }

    public void onUpdate() {
        if (mc.field_71439_g != null) {
            if (mc.field_71441_e != null && mc.field_71439_g.func_184187_bx() != null) {
                if (mc.field_71439_g.func_184187_bx() instanceof EntityBoat) {
                    this.target = (EntityBoat) mc.field_71439_g.field_184239_as;
                }

                mc.field_71439_g.func_184187_bx().func_189654_d(true);
                mc.field_71439_g.func_184187_bx().field_70181_x = 0.0D;
                if (mc.field_71474_y.field_74314_A.func_151470_d()) {
                    mc.field_71439_g.func_184187_bx().field_70122_E = false;
                    mc.field_71439_g.func_184187_bx().field_70181_x = ((Double) this.verticalSpeed.getValue()).doubleValue() / 10.0D;
                }

                if (mc.field_71474_y.field_151444_V.func_151470_d()) {
                    mc.field_71439_g.func_184187_bx().field_70122_E = false;
                    mc.field_71439_g.func_184187_bx().field_70181_x = -(((Double) this.verticalSpeed.getValue()).doubleValue() / 10.0D);
                }

                double[] normalDir = this.directionSpeed(((Double) this.speed.getValue()).doubleValue() / 2.0D);
                if (mc.field_71439_g.field_71158_b.field_78902_a == 0.0F && mc.field_71439_g.field_71158_b.field_192832_b == 0.0F) {
                    mc.field_71439_g.func_184187_bx().field_70159_w = 0.0D;
                    mc.field_71439_g.func_184187_bx().field_70179_y = 0.0D;
                } else {
                    mc.field_71439_g.func_184187_bx().field_70159_w = normalDir[0];
                    mc.field_71439_g.func_184187_bx().field_70179_y = normalDir[1];
                }

                if (((Boolean) this.noKick.getValue()).booleanValue()) {
                    if (mc.field_71474_y.field_74314_A.func_151470_d()) {
                        if (mc.field_71439_g.field_70173_aa % 8 < 2) {
                            mc.field_71439_g.func_184187_bx().field_70181_x = -0.03999999910593033D;
                        }
                    } else if (mc.field_71439_g.field_70173_aa % 8 < 4) {
                        mc.field_71439_g.func_184187_bx().field_70181_x = -0.07999999821186066D;
                    }
                }

                this.handlePackets(mc.field_71439_g.func_184187_bx().field_70159_w, mc.field_71439_g.func_184187_bx().field_70181_x, mc.field_71439_g.func_184187_bx().field_70179_y);
            }
        }
    }

    public void handlePackets(double x, double y, double z) {
        if (((Boolean) this.packet.getValue()).booleanValue()) {
            Vec3d vec = new Vec3d(x, y, z);
            if (mc.field_71439_g.func_184187_bx() == null) {
                return;
            }

            Vec3d position = mc.field_71439_g.func_184187_bx().func_174791_d().func_178787_e(vec);
            mc.field_71439_g.func_184187_bx().func_70107_b(position.field_72450_a, position.field_72448_b, position.field_72449_c);
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketVehicleMove(mc.field_71439_g.func_184187_bx()));

            for (int i = 0; i < ((Integer) this.packets.getValue()).intValue(); ++i) {
                mc.field_71439_g.field_71174_a.func_147297_a(new CPacketConfirmTeleport(this.teleportID++));
            }
        }

    }

    private void NCPPacketTrick() {
        this.packetCounter = 0;
        mc.field_71439_g.func_184187_bx().func_184210_p();
        Entity l_Entity = (Entity) mc.field_71441_e.field_72996_f.stream().filter((p_Entity) -> {
            return p_Entity instanceof EntityBoat;
        }).min(Comparator.comparing((p_Entity) -> {
            return mc.field_71439_g.func_70032_d(p_Entity);
        })).orElse((Object) null);
        if (l_Entity != null) {
            mc.field_71442_b.func_187097_a(mc.field_71439_g, l_Entity, EnumHand.MAIN_HAND);
        }

    }

    @SubscribeEvent
    public void onSendPacket(PacketEvent.Send event) {
        if (mc.field_71441_e != null && mc.field_71439_g != null && mc.field_71439_g.func_184187_bx() instanceof EntityBoat) {
            if (((Boolean) this.bypass.getValue()).booleanValue() && event.getPacket() instanceof CPacketInput && !mc.field_71474_y.field_74311_E.func_151470_d() && !mc.field_71439_g.func_184187_bx().field_70122_E) {
                ++this.packetCounter;
                if (this.packetCounter == 3) {
                    this.NCPPacketTrick();
                }
            }

            if (((Boolean) this.bypass.getValue()).booleanValue() && event.getPacket() instanceof SPacketPlayerPosLook || event.getPacket() instanceof SPacketMoveVehicle) {
                event.setCanceled(true);
            }
        }

        if (event.getPacket() instanceof CPacketVehicleMove && mc.field_71439_g.func_184218_aH() && mc.field_71439_g.field_70173_aa % ((Integer) this.interact.getValue()).intValue() == 0) {
            mc.field_71442_b.func_187097_a(mc.field_71439_g, mc.field_71439_g.field_184239_as, EnumHand.OFF_HAND);
        }

        if ((event.getPacket() instanceof Rotation || event.getPacket() instanceof CPacketInput) && mc.field_71439_g.func_184218_aH()) {
            event.setCanceled(true);
        }

        if (((Boolean) this.placeBypass.getValue()).booleanValue() && event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock && mc.field_71439_g.func_184614_ca().func_77973_b() instanceof ItemBoat || mc.field_71439_g.func_184592_cb().func_77973_b() instanceof ItemBoat) {
            event.setCanceled(true);
        }

    }

    @SubscribeEvent
    public void onReceivePacket(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketMoveVehicle && mc.field_71439_g.func_184218_aH()) {
            event.setCanceled(true);
        }

        if (event.getPacket() instanceof SPacketPlayerPosLook) {
            this.teleportID = ((SPacketPlayerPosLook) event.getPacket()).field_186966_g;
        }

    }

    private double[] directionSpeed(double speed) {
        float forward = mc.field_71439_g.field_71158_b.field_192832_b;
        float side = mc.field_71439_g.field_71158_b.field_78902_a;
        float yaw = mc.field_71439_g.field_70126_B + (mc.field_71439_g.field_70177_z - mc.field_71439_g.field_70126_B) * mc.func_184121_ak();
        if (forward != 0.0F) {
            if (side > 0.0F) {
                yaw += (float) (forward > 0.0F ? -45 : 45);
            } else if (side < 0.0F) {
                yaw += (float) (forward > 0.0F ? 45 : -45);
            }

            side = 0.0F;
            if (forward > 0.0F) {
                forward = 1.0F;
            } else if (forward < 0.0F) {
                forward = -1.0F;
            }
        }

        double sin = Math.sin(Math.toRadians((double) (yaw + 90.0F)));
        double cos = Math.cos(Math.toRadians((double) (yaw + 90.0F)));
        double posX = (double) forward * speed * cos + (double) side * speed * sin;
        double posZ = (double) forward * speed * sin - (double) side * speed * cos;
        return new double[]{posX, posZ};
    }
}
