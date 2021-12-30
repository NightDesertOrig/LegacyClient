package me.dev.legacy.modules.movement;

import me.dev.legacy.Legacy;
import me.dev.legacy.api.event.events.move.MoveEvent;
import me.dev.legacy.api.event.events.move.UpdateWalkingPlayerEvent;
import me.dev.legacy.api.util.EntityUtil;
import me.dev.legacy.api.util.Timer;
import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.modules.Module;
import me.dev.legacy.modules.ModuleManager;
import me.dev.legacy.modules.player.Freecam;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class Strafe extends Module {
    private static Strafe INSTANCE;
    private final Setting acceleration = this.register(new Setting("Speed", Integer.valueOf(1600), Integer.valueOf(1000), Integer.valueOf(2500)));
    private final Setting speedLimit = this.register(new Setting("MaxSpeed", 60.0F, 20.0F, 60.0F));
    private int potionSpeed = 1500;
    private int stage = 1;
    private double moveSpeed;
    private double lastDist;
    private int cooldownHops = 0;
    private boolean waitForGround = false;
    private final Timer timer = new Timer();
    private int hops = 0;

    public Strafe() {
        super("Strafe", "AirControl etc.", Module.Category.MOVEMENT, true, false, false);
        INSTANCE = this;
    }

    public static Strafe getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Strafe();
        }

        return INSTANCE;
    }

    public static double getBaseMoveSpeed() {
        double baseSpeed = 0.272D;
        if (mc.field_71439_g.func_70644_a(MobEffects.field_76424_c)) {
            int amplifier = ((PotionEffect) Objects.requireNonNull(mc.field_71439_g.func_70660_b(MobEffects.field_76424_c))).func_76458_c();
            baseSpeed *= 1.0D + 0.2D * (double) amplifier;
        }

        return baseSpeed;
    }

    public static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        } else {
            BigDecimal bigDecimal = (new BigDecimal(value)).setScale(places, RoundingMode.HALF_UP);
            return bigDecimal.doubleValue();
        }
    }

    public void onEnable() {
        if (!mc.field_71439_g.field_70122_E) {
            this.waitForGround = true;
        }

        this.hops = 0;
        this.timer.reset();
        this.moveSpeed = getBaseMoveSpeed();
    }

    public void onDisable() {
        this.hops = 0;
        this.moveSpeed = 0.0D;
        this.stage = 0;
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
        if (event.getStage() == 0) {
            this.lastDist = Math.sqrt((mc.field_71439_g.field_70165_t - mc.field_71439_g.field_70169_q) * (mc.field_71439_g.field_70165_t - mc.field_71439_g.field_70169_q) + (mc.field_71439_g.field_70161_v - mc.field_71439_g.field_70166_s) * (mc.field_71439_g.field_70161_v - mc.field_71439_g.field_70166_s));
        }

    }

    @SubscribeEvent
    public void onMove(MoveEvent event) {
        if (event.getStage() == 0 && !this.shouldReturn()) {
            if (!mc.field_71439_g.field_70122_E) {
                this.waitForGround = false;
            }

            float moveForward = mc.field_71439_g.field_71158_b.field_192832_b;
            float moveStrafe = mc.field_71439_g.field_71158_b.field_78902_a;
            float rotationYaw = mc.field_71439_g.field_70177_z;
            if (mc.field_71439_g.field_70122_E && Legacy.speedManager.getSpeedKpH() < (double) ((Float) this.speedLimit.getValue()).floatValue()) {
                this.stage = 2;
            }

            if (round(mc.field_71439_g.field_70163_u - (double) ((int) mc.field_71439_g.field_70163_u), 3) == round(1.0D, 3) && EntityUtil.isEntityMoving(mc.field_71439_g)) {
                EntityPlayerSP player = mc.field_71439_g;
                player.field_70181_x -= 0.0D;
                event.setY(event.getY() - 0.2D);
            }

            double motionX;
            if (this.stage == 1 && EntityUtil.isMoving()) {
                this.stage = 2;
                this.moveSpeed = (double) this.getMultiplier() * getBaseMoveSpeed() - 0.01D;
            } else if (this.stage == 2 && EntityUtil.isMoving()) {
                this.stage = 3;
                event.setY(mc.field_71439_g.field_70181_x = 0.4D);
                if (this.cooldownHops > 0) {
                    --this.cooldownHops;
                }

                ++this.hops;
                motionX = ((Integer) this.acceleration.getValue()).intValue() == 2149 ? 2.149802D : (double) ((Integer) this.acceleration.getValue()).intValue() / 1000.0D;
                this.moveSpeed *= motionX;
            } else if (this.stage == 3) {
                this.stage = 4;
                motionX = 0.66D * (this.lastDist - getBaseMoveSpeed());
                this.moveSpeed = this.lastDist - motionX;
            } else {
                if (mc.field_71441_e.func_184144_a(mc.field_71439_g, mc.field_71439_g.func_174813_aQ().func_72317_d(0.0D, mc.field_71439_g.field_70181_x, 0.0D)).size() > 0 || mc.field_71439_g.field_70124_G && this.stage > 0) {
                    this.stage = mc.field_71439_g.field_191988_bg == 0.0F && mc.field_71439_g.field_70702_br == 0.0F ? 0 : 1;
                }

                this.moveSpeed = this.lastDist - this.lastDist / 200.0D;
            }

            this.moveSpeed = Math.max(this.moveSpeed, getBaseMoveSpeed());
            if (moveForward == 0.0F && moveStrafe == 0.0F) {
                event.setX(0.0D);
                event.setZ(0.0D);
                this.moveSpeed = 0.0D;
            } else if (moveForward != 0.0F) {
                if (moveStrafe >= 1.0F) {
                    rotationYaw += moveForward > 0.0F ? -45.0F : 45.0F;
                    moveStrafe = 0.0F;
                } else if (moveStrafe <= -1.0F) {
                    rotationYaw += moveForward > 0.0F ? 45.0F : -45.0F;
                    moveStrafe = 0.0F;
                }

                if (moveForward > 0.0F) {
                    moveForward = 1.0F;
                } else if (moveForward < 0.0F) {
                    moveForward = -1.0F;
                }
            }

            motionX = Math.cos(Math.toRadians((double) (rotationYaw + 90.0F)));
            double motionZ = Math.sin(Math.toRadians((double) (rotationYaw + 90.0F)));
            if (this.cooldownHops == 0) {
                event.setX((double) moveForward * this.moveSpeed * motionX + (double) moveStrafe * this.moveSpeed * motionZ);
                event.setZ((double) moveForward * this.moveSpeed * motionZ - (double) moveStrafe * this.moveSpeed * motionX);
            }

            if (moveForward == 0.0F && moveStrafe == 0.0F) {
                this.timer.reset();
                event.setX(0.0D);
                event.setZ(0.0D);
            }

        }
    }

    private float getMultiplier() {
        float baseSpeed = 500.0F;
        if (mc.field_71439_g.func_70644_a(MobEffects.field_76424_c)) {
            int amplifier = ((PotionEffect) Objects.requireNonNull(mc.field_71439_g.func_70660_b(MobEffects.field_76424_c))).func_76458_c() + 1;
            baseSpeed = amplifier >= 2 ? (float) this.potionSpeed : (float) this.potionSpeed;
        }

        return baseSpeed / 100.0F;
    }

    private boolean shouldReturn() {
        ModuleManager var10000 = Legacy.moduleManager;
        boolean var1;
        if (!ModuleManager.isModuleEnabled(Freecam.class)) {
            var10000 = Legacy.moduleManager;
            if (!ModuleManager.isModuleEnabled(PacketFly.class)) {
                var1 = false;
                return var1;
            }
        }

        var1 = true;
        return var1;
    }
}
