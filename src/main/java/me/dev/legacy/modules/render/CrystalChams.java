package me.dev.legacy.modules.render;

import me.dev.legacy.api.event.events.other.PacketEvent;
import me.dev.legacy.api.event.events.render.RenderEntityModelEvent;
import me.dev.legacy.api.util.EntityUtil;
import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.modules.Module;
import me.dev.legacy.modules.client.ClickGui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CrystalChams extends Module {
    public Setting animateScale = this.register(new Setting("AnimateScale", false));
    public Setting chams = this.register(new Setting("Chams", false));
    public Setting throughWalls = this.register(new Setting("ThroughWalls", true));
    public Setting wireframeThroughWalls = this.register(new Setting("WireThroughWalls", true));
    public Setting glint = this.register(new Setting("Glint", false, (v) -> {
        return ((Boolean) this.chams.getValue()).booleanValue();
    }));
    public Setting wireframe = this.register(new Setting("Wireframe", false));
    public Setting scale = this.register(new Setting("Scale", 1.0F, 0.1F, 10.0F));
    public Setting lineWidth = this.register(new Setting("LineWidth", 1.0F, 0.1F, 3.0F));
    public Setting colorSync = this.register(new Setting("Sync", false));
    public Setting rainbow = this.register(new Setting("Rainbow", false));
    public Setting saturation = this.register(new Setting("Saturation", Integer.valueOf(50), Integer.valueOf(0), Integer.valueOf(100), (v) -> {
        return ((Boolean) this.rainbow.getValue()).booleanValue();
    }));
    public Setting brightness = this.register(new Setting("Brightness", Integer.valueOf(100), Integer.valueOf(0), Integer.valueOf(100), (v) -> {
        return ((Boolean) this.rainbow.getValue()).booleanValue();
    }));
    public Setting speed = this.register(new Setting("Speed", Integer.valueOf(40), Integer.valueOf(1), Integer.valueOf(100), (v) -> {
        return ((Boolean) this.rainbow.getValue()).booleanValue();
    }));
    public Setting xqz = this.register(new Setting("XQZ", false, (v) -> {
        return !((Boolean) this.rainbow.getValue()).booleanValue() && ((Boolean) this.throughWalls.getValue()).booleanValue();
    }));
    public Setting red = this.register(new Setting("Red", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return !((Boolean) this.rainbow.getValue()).booleanValue();
    }));
    public Setting green = this.register(new Setting("Green", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return !((Boolean) this.rainbow.getValue()).booleanValue();
    }));
    public Setting blue = this.register(new Setting("Blue", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return !((Boolean) this.rainbow.getValue()).booleanValue();
    }));
    public Setting alpha = this.register(new Setting("Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
    public Setting hiddenRed = this.register(new Setting("Hidden Red", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.xqz.getValue()).booleanValue() && !((Boolean) this.rainbow.getValue()).booleanValue();
    }));
    public Setting hiddenGreen = this.register(new Setting("Hidden Green", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.xqz.getValue()).booleanValue() && !((Boolean) this.rainbow.getValue()).booleanValue();
    }));
    public Setting hiddenBlue = this.register(new Setting("Hidden Blue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.xqz.getValue()).booleanValue() && !((Boolean) this.rainbow.getValue()).booleanValue();
    }));
    public Setting hiddenAlpha = this.register(new Setting("Hidden Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.xqz.getValue()).booleanValue() && !((Boolean) this.rainbow.getValue()).booleanValue();
    }));
    public Map scaleMap = new ConcurrentHashMap();
    public static CrystalChams INSTANCE;

    public CrystalChams() {
        super("CrystalChams", "Modifies crystal rendering in different ways", Module.Category.RENDER, true, false, false);
        INSTANCE = this;
    }

    public void onUpdate() {
        Iterator var1 = mc.field_71441_e.field_72996_f.iterator();

        while (var1.hasNext()) {
            Entity crystal = (Entity) var1.next();
            if (crystal instanceof EntityEnderCrystal) {
                if (!this.scaleMap.containsKey(crystal)) {
                    this.scaleMap.put((EntityEnderCrystal) crystal, 3.125E-4F);
                } else {
                    this.scaleMap.put((EntityEnderCrystal) crystal, ((Float) this.scaleMap.get(crystal)).floatValue() + 3.125E-4F);
                }

                if (((Float) this.scaleMap.get(crystal)).floatValue() >= 0.0625F * ((Float) this.scale.getValue()).floatValue()) {
                    this.scaleMap.remove(crystal);
                }
            }
        }

    }

    @SubscribeEvent
    public void onReceivePacket(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketDestroyEntities) {
            SPacketDestroyEntities packet = (SPacketDestroyEntities) event.getPacket();
            int[] var3 = packet.func_149098_c();
            int var4 = var3.length;

            for (int var5 = 0; var5 < var4; ++var5) {
                int id = var3[var5];
                Entity entity = mc.field_71441_e.func_73045_a(id);
                if (entity instanceof EntityEnderCrystal) {
                    this.scaleMap.remove(entity);
                }
            }
        }

    }

    public void onRenderModel(RenderEntityModelEvent event) {
        if (event.getStage() == 0 && event.entity instanceof EntityEnderCrystal && ((Boolean) this.wireframe.getValue()).booleanValue()) {
            Color color = ((Boolean) this.colorSync.getValue()).booleanValue() ? ClickGui.getInstance().getCurrentColor() : EntityUtil.getColor(event.entity, ((Integer) this.red.getValue()).intValue(), ((Integer) this.green.getValue()).intValue(), ((Integer) this.blue.getValue()).intValue(), ((Integer) this.alpha.getValue()).intValue(), false);
            boolean fancyGraphics = mc.field_71474_y.field_74347_j;
            mc.field_71474_y.field_74347_j = false;
            float gamma = mc.field_71474_y.field_74333_Y;
            mc.field_71474_y.field_74333_Y = 10000.0F;
            GL11.glPushMatrix();
            GL11.glPushAttrib(1048575);
            GL11.glPolygonMode(1032, 6913);
            GL11.glDisable(3553);
            GL11.glDisable(2896);
            if (((Boolean) this.wireframeThroughWalls.getValue()).booleanValue()) {
                GL11.glDisable(2929);
            }

            GL11.glEnable(2848);
            GL11.glEnable(3042);
            GlStateManager.func_179112_b(770, 771);
            GlStateManager.func_179131_c((float) color.getRed() / 255.0F, (float) color.getGreen() / 255.0F, (float) color.getBlue() / 255.0F, (float) color.getAlpha() / 255.0F);
            GlStateManager.func_187441_d(((Float) this.lineWidth.getValue()).floatValue());
            event.modelBase.func_78088_a(event.entity, event.limbSwing, event.limbSwingAmount, event.age, event.headYaw, event.headPitch, event.scale);
            GL11.glPopAttrib();
            GL11.glPopMatrix();
        }
    }
}
