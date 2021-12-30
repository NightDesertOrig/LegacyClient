package me.dev.legacy.modules.render;

import me.dev.legacy.api.event.events.render.PerspectiveEvent;
import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.modules.Module;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Aspect extends Module {
    private Setting aspect;

    public Aspect() {
        super("Aspect", "esdxzdfwa", Module.Category.RENDER, true, false, false);
        this.aspect = this.register(new Setting("Aspect", (double) mc.field_71443_c / (double) mc.field_71440_d, 0.0D, 3.0D));
    }

    @SubscribeEvent
    public void onPerspectiveEvent(PerspectiveEvent event) {
        event.setAspect(((Double) this.aspect.getValue()).floatValue());
    }
}
