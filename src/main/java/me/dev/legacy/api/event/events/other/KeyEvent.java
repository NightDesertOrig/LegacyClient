package me.dev.legacy.api.event.events.other;

import me.dev.legacy.api.event.EventStage;

public class KeyEvent extends EventStage {
    public boolean info;
    public boolean pressed;

    public KeyEvent(int stage) {
        super(stage);
        this.info = this.info;
        this.pressed = this.pressed;
    }
}
