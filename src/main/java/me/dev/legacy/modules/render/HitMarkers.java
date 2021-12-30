package me.dev.legacy.modules.render;

import me.dev.legacy.api.event.events.render.Render2DEvent;
import me.dev.legacy.api.util.RenderUtil;
import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.modules.Module;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.awt.*;

public final class HitMarkers extends Module {
    public final ResourceLocation image = new ResourceLocation("hitmarker.png");
    private int renderTicks = 100;
    public Setting red = this.register(new Setting("Red", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
    public Setting green = this.register(new Setting("Green", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
    public Setting blue = this.register(new Setting("Blue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
    public Setting alpha = this.register(new Setting("Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
    public Setting thickness = this.register(new Setting("Thickness", Integer.valueOf(2), Integer.valueOf(1), Integer.valueOf(6)));
    public Setting time = this.register(new Setting("Time", 20.0D, 1.0D, 50.0D));

    public HitMarkers() {
        super("HitMarkers", "hitmarker thingys", Module.Category.RENDER, false, false, false);
    }

    public void onRender2D(Render2DEvent event) {
        if ((double) this.renderTicks < ((Double) this.time.getValue()).doubleValue()) {
            new ScaledResolution(mc);
            this.drawHitMarkers();
        }

    }

    public void onEnable() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void onDisable() {
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    @SubscribeEvent
    public void onAttackEntity(AttackEntityEvent event) {
        if (event.getEntity().equals(mc.field_71439_g)) {
            this.renderTicks = 0;
        }
    }

    @SubscribeEvent
    public void onTickClientTick(TickEvent event) {
        ++this.renderTicks;
    }

    public void drawHitMarkers() {
        ScaledResolution resolution = new ScaledResolution(mc);
        RenderUtil.drawLine((float) resolution.func_78326_a() / 2.0F - 4.0F, (float) resolution.func_78328_b() / 2.0F - 4.0F, (float) resolution.func_78326_a() / 2.0F - 8.0F, (float) resolution.func_78328_b() / 2.0F - 8.0F, (float) ((Integer) this.thickness.getValue()).intValue(), (new Color(((Integer) this.red.getValue()).intValue(), ((Integer) this.green.getValue()).intValue(), ((Integer) this.blue.getValue()).intValue())).getRGB());
        RenderUtil.drawLine((float) resolution.func_78326_a() / 2.0F + 4.0F, (float) resolution.func_78328_b() / 2.0F - 4.0F, (float) resolution.func_78326_a() / 2.0F + 8.0F, (float) resolution.func_78328_b() / 2.0F - 8.0F, (float) ((Integer) this.thickness.getValue()).intValue(), (new Color(((Integer) this.red.getValue()).intValue(), ((Integer) this.green.getValue()).intValue(), ((Integer) this.blue.getValue()).intValue())).getRGB());
        RenderUtil.drawLine((float) resolution.func_78326_a() / 2.0F - 4.0F, (float) resolution.func_78328_b() / 2.0F + 4.0F, (float) resolution.func_78326_a() / 2.0F - 8.0F, (float) resolution.func_78328_b() / 2.0F + 8.0F, (float) ((Integer) this.thickness.getValue()).intValue(), (new Color(((Integer) this.red.getValue()).intValue(), ((Integer) this.green.getValue()).intValue(), ((Integer) this.blue.getValue()).intValue())).getRGB());
        RenderUtil.drawLine((float) resolution.func_78326_a() / 2.0F + 4.0F, (float) resolution.func_78328_b() / 2.0F + 4.0F, (float) resolution.func_78326_a() / 2.0F + 8.0F, (float) resolution.func_78328_b() / 2.0F + 8.0F, (float) ((Integer) this.thickness.getValue()).intValue(), (new Color(((Integer) this.red.getValue()).intValue(), ((Integer) this.green.getValue()).intValue(), ((Integer) this.blue.getValue()).intValue())).getRGB());
    }
}
