package me.dev.legacy.modules.movement;

import me.dev.legacy.api.event.events.move.PushEvent;
import me.dev.legacy.api.event.events.other.PacketEvent;
import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.modules.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Velocity extends Module {
    public Setting noPush = this.register(new Setting("NoPush", true));
    public Setting horizontal = this.register(new Setting("Horizontal", 0.0F, 0.0F, 100.0F));
    public Setting vertical = this.register(new Setting("Vertical", 0.0F, 0.0F, 100.0F));
    public Setting explosions = this.register(new Setting("Explosions", true));
    public Setting bobbers = this.register(new Setting("Bobbers", true));
    public Setting water = this.register(new Setting("Water", false));
    public Setting blocks = this.register(new Setting("Blocks", false));
    public Setting ice = this.register(new Setting("Ice", false));
    private static Velocity INSTANCE = new Velocity();

    public Velocity() {
        super("Velocity", "Allows you to control your velocity", Module.Category.MOVEMENT, true, false, false);
        this.setInstance();
    }

    private void setInstance() {
        INSTANCE = this;
    }

    public static Velocity getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new Velocity();
        }

        return INSTANCE;
    }

    public void onUpdate() {
    }

    public void onDisable() {
    }

    @SubscribeEvent
    public void onPacketReceived(PacketEvent.Receive event) {
        if (event.getStage() == 0 && mc.field_71439_g != null) {
            if (event.getPacket() instanceof SPacketEntityVelocity) {
                SPacketEntityVelocity velocity = (SPacketEntityVelocity) event.getPacket();
                if (velocity.func_149412_c() == mc.field_71439_g.field_145783_c) {
                    if (((Float) this.horizontal.getValue()).floatValue() == 0.0F && ((Float) this.vertical.getValue()).floatValue() == 0.0F) {
                        event.setCanceled(true);
                        return;
                    }

                    velocity.field_149415_b *= ((Integer) this.horizontal.getValue()).intValue();
                    velocity.field_149416_c *= ((Integer) this.vertical.getValue()).intValue();
                    velocity.field_149414_d *= ((Integer) this.horizontal.getValue()).intValue();
                }
            }

            if (event.getPacket() instanceof SPacketEntityStatus && ((Boolean) this.bobbers.getValue()).booleanValue()) {
                SPacketEntityStatus packet = (SPacketEntityStatus) event.getPacket();
                if (packet.func_149160_c() == 31) {
                    Entity entity = packet.func_149161_a(mc.field_71441_e);
                    if (entity instanceof EntityFishHook) {
                        EntityFishHook fishHook = (EntityFishHook) entity;
                        if (fishHook.field_146043_c == mc.field_71439_g) {
                            event.setCanceled(true);
                        }
                    }
                }
            }

            if (((Boolean) this.explosions.getValue()).booleanValue() && event.getPacket() instanceof SPacketExplosion) {
                if (((Float) this.horizontal.getValue()).floatValue() == 0.0F && ((Float) this.vertical.getValue()).floatValue() == 0.0F) {
                    event.setCanceled(true);
                    return;
                }

                SPacketExplosion sPacketExplosion;
                SPacketExplosion velocity2 = sPacketExplosion = (SPacketExplosion) event.getPacket();
                sPacketExplosion.field_149152_f *= ((Float) this.horizontal.getValue()).floatValue();
                velocity2.field_149153_g *= ((Float) this.vertical.getValue()).floatValue();
                velocity2.field_149159_h *= ((Float) this.horizontal.getValue()).floatValue();
            }
        }

    }

    @SubscribeEvent
    public void onPush(PushEvent event) {
        if (event.getStage() == 0 && ((Boolean) this.noPush.getValue()).booleanValue() && event.entity.equals(mc.field_71439_g)) {
            if (((Float) this.horizontal.getValue()).floatValue() == 0.0F && ((Float) this.vertical.getValue()).floatValue() == 0.0F) {
                event.setCanceled(true);
                return;
            }

            event.x = -event.x * (double) ((Float) this.horizontal.getValue()).floatValue();
            event.y = -event.y * (double) ((Float) this.vertical.getValue()).floatValue();
            event.z = -event.z * (double) ((Float) this.horizontal.getValue()).floatValue();
        } else if (event.getStage() == 1 && ((Boolean) this.blocks.getValue()).booleanValue()) {
            event.setCanceled(true);
        } else if (event.getStage() == 2 && ((Boolean) this.water.getValue()).booleanValue() && mc.field_71439_g != null && mc.field_71439_g.equals(event.entity)) {
            event.setCanceled(true);
        }

    }
}
