package me.dev.legacy.api.event.events;

import me.dev.legacy.api.util.Wrapper;
import me.zero.alpine.fork.event.type.Cancellable;

public class MinecraftEvent extends Cancellable {
    private MinecraftEvent.Era era;
    private final float partialTicks;

    public MinecraftEvent() {
        this.era = MinecraftEvent.Era.PRE;
        this.partialTicks = Wrapper.GetMC().func_184121_ak();
    }

    public MinecraftEvent(MinecraftEvent.Era p_Era) {
        this.era = MinecraftEvent.Era.PRE;
        this.partialTicks = Wrapper.GetMC().func_184121_ak();
        this.era = p_Era;
    }

    public MinecraftEvent.Era getEra() {
        return this.era;
    }

    public float getPartialTicks() {
        return this.partialTicks;
    }

    public static enum Era {
        PRE,
        PERI,
        POST;
    }
}
