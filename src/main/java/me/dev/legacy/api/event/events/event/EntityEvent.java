package me.dev.legacy.api.event.events.event;

import me.dev.legacy.api.event.EventStage;
import net.minecraft.entity.Entity;

public class EntityEvent extends EventStage {
    private Entity entity;

    public EntityEvent(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return this.entity;
    }

    public static class EntityCollision extends EntityEvent {
        double x;
        double y;
        double z;

        public EntityCollision(Entity entity, double x, double y, double z) {
            super(entity);
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public double getX() {
            return this.x;
        }

        public double getY() {
            return this.y;
        }

        public double getZ() {
            return this.z;
        }

        public void setX(double x) {
            this.x = x;
        }

        public void setY(double y) {
            this.y = y;
        }

        public void setZ(double z) {
            this.z = z;
        }
    }
}
