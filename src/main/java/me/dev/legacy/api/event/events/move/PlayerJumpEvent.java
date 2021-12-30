package me.dev.legacy.api.event.events.move;

import me.dev.legacy.api.event.EventStage;

public class PlayerJumpEvent extends EventStage {
    public double motionX;
    public double motionY;

    public PlayerJumpEvent(double motionX, double motionY) {
        this.motionX = motionX;
        this.motionY = motionY;
    }
}
