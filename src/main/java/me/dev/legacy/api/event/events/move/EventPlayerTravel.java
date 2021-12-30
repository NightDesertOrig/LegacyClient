package me.dev.legacy.api.event.events.move;

import me.dev.legacy.api.event.events.MinecraftEvent;

public class EventPlayerTravel extends MinecraftEvent {
    public float Strafe;
    public float Vertical;
    public float Forward;

    public EventPlayerTravel(float p_Strafe, float p_Vertical, float p_Forward) {
        this.Strafe = p_Strafe;
        this.Vertical = p_Vertical;
        this.Forward = p_Forward;
    }
}
