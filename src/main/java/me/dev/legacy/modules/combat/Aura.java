package me.dev.legacy.modules.combat;

import me.dev.legacy.Legacy;
import me.dev.legacy.api.event.events.move.UpdateWalkingPlayerEvent;
import me.dev.legacy.api.util.DamageUtil;
import me.dev.legacy.api.util.EntityUtil;
import me.dev.legacy.api.util.MathUtil;
import me.dev.legacy.api.util.Timer;
import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.modules.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Iterator;

public class Aura extends Module {
    public static Entity target;
    private final Timer timer = new Timer();
    public Setting range = this.register(new Setting("Range", 6.0F, 0.1F, 7.0F));
    public Setting delay = this.register(new Setting("HitDelay", true));
    public Setting rotate = this.register(new Setting("Rotate", false));
    public Setting onlySharp = this.register(new Setting("SwordOnly", true));
    public Setting raytrace = this.register(new Setting("Raytrace", 6.0F, 0.1F, 7.0F, "Wall Range."));
    public Setting players = this.register(new Setting("Players", true));
    public Setting mobs = this.register(new Setting("Mobs", false));
    public Setting animals = this.register(new Setting("Animals", false));
    public Setting vehicles = this.register(new Setting("Entities", false));
    public Setting projectiles = this.register(new Setting("Projectiles", false));
    public Setting tps = this.register(new Setting("TpsSync", true));
    public Setting packet = this.register(new Setting("Packet", false));

    public Aura() {
        super("Aura", "Kills aura.", Module.Category.COMBAT, true, false, false);
    }

    public void onTick() {
        if (!((Boolean) this.rotate.getValue()).booleanValue()) {
            this.doKillaura();
        }

    }

    @SubscribeEvent
    public void onUpdateWalkingPlayerEvent(UpdateWalkingPlayerEvent event) {
        if (event.getStage() == 0 && ((Boolean) this.rotate.getValue()).booleanValue()) {
            this.doKillaura();
        }

    }

    private void doKillaura() {
        if (((Boolean) this.onlySharp.getValue()).booleanValue() && !EntityUtil.holdingWeapon(mc.field_71439_g)) {
            target = null;
        } else {
            int wait = !((Boolean) this.delay.getValue()).booleanValue() ? 0 : (int) ((float) DamageUtil.getCooldownByWeapon(mc.field_71439_g) * (((Boolean) this.tps.getValue()).booleanValue() ? Legacy.serverManager.getTpsFactor() : 1.0F));
            if (this.timer.passedMs((long) wait)) {
                target = this.getTarget();
                if (target != null) {
                    if (((Boolean) this.rotate.getValue()).booleanValue()) {
                        Legacy.rotationManager.lookAtEntity(target);
                    }

                    EntityUtil.attackEntity(target, ((Boolean) this.packet.getValue()).booleanValue(), true);
                    this.timer.reset();
                }
            }
        }
    }

    private Entity getTarget() {
        Entity target = null;
        double distance = (double) ((Float) this.range.getValue()).floatValue();
        double maxHealth = 36.0D;
        Iterator var6 = mc.field_71441_e.field_73010_i.iterator();

        while (var6.hasNext()) {
            Entity entity = (Entity) var6.next();
            if ((((Boolean) this.players.getValue()).booleanValue() && entity instanceof EntityPlayer || ((Boolean) this.animals.getValue()).booleanValue() && EntityUtil.isPassive(entity) || ((Boolean) this.mobs.getValue()).booleanValue() && EntityUtil.isMobAggressive(entity) || ((Boolean) this.vehicles.getValue()).booleanValue() && EntityUtil.isVehicle(entity) || ((Boolean) this.projectiles.getValue()).booleanValue() && EntityUtil.isProjectile(entity)) && (!(entity instanceof EntityLivingBase) || !EntityUtil.isntValid(entity, distance)) && (mc.field_71439_g.func_70685_l(entity) || EntityUtil.canEntityFeetBeSeen(entity) || mc.field_71439_g.func_70068_e(entity) <= MathUtil.square((double) ((Float) this.raytrace.getValue()).floatValue()))) {
                if (target == null) {
                    target = entity;
                    distance = mc.field_71439_g.func_70068_e(entity);
                    maxHealth = (double) EntityUtil.getHealth(entity);
                } else {
                    if (entity instanceof EntityPlayer && DamageUtil.isArmorLow((EntityPlayer) entity, 18)) {
                        target = entity;
                        break;
                    }

                    if (mc.field_71439_g.func_70068_e(entity) < distance) {
                        target = entity;
                        distance = mc.field_71439_g.func_70068_e(entity);
                        maxHealth = (double) EntityUtil.getHealth(entity);
                    }

                    if ((double) EntityUtil.getHealth(entity) < maxHealth) {
                        target = entity;
                        distance = mc.field_71439_g.func_70068_e(entity);
                        maxHealth = (double) EntityUtil.getHealth(entity);
                    }
                }
            }
        }

        return target;
    }

    public String getDisplayInfo() {
        return target instanceof EntityPlayer ? target.func_70005_c_() : null;
    }
}
