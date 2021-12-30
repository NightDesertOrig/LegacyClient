package me.dev.legacy.api.mixin.mixins;

import me.dev.legacy.api.event.events.render.RenderEntityModelEvent;
import me.dev.legacy.api.util.EntityUtil;
import me.dev.legacy.api.util.RenderUtil;
import me.dev.legacy.modules.client.ClickGui;
import me.dev.legacy.modules.render.CrystalChams;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderEnderCrystal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.awt.*;

@Mixin({RenderEnderCrystal.class})
public class MixinRenderEnderCrystal {
    @Shadow
    @Final
    private static ResourceLocation field_110787_a;
    private static final ResourceLocation glint = new ResourceLocation("textures/glint.png");

    @Redirect(
            method = {"doRender"},
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/model/ModelBase;render(Lnet/minecraft/entity/Entity;FFFFFF)V"
            )
    )
    public void renderModelBaseHook(ModelBase model, Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (CrystalChams.INSTANCE.isEnabled()) {
            if (((Boolean) CrystalChams.INSTANCE.animateScale.getValue()).booleanValue() && CrystalChams.INSTANCE.scaleMap.containsKey((EntityEnderCrystal) entity)) {
                GlStateManager.func_179152_a(((Float) CrystalChams.INSTANCE.scaleMap.get((EntityEnderCrystal) entity)).floatValue(), ((Float) CrystalChams.INSTANCE.scaleMap.get((EntityEnderCrystal) entity)).floatValue(), ((Float) CrystalChams.INSTANCE.scaleMap.get((EntityEnderCrystal) entity)).floatValue());
            } else {
                GlStateManager.func_179152_a(((Float) CrystalChams.INSTANCE.scale.getValue()).floatValue(), ((Float) CrystalChams.INSTANCE.scale.getValue()).floatValue(), ((Float) CrystalChams.INSTANCE.scale.getValue()).floatValue());
            }
        }

        if (CrystalChams.INSTANCE.isEnabled() && ((Boolean) CrystalChams.INSTANCE.wireframe.getValue()).booleanValue()) {
            RenderEntityModelEvent event = new RenderEntityModelEvent(0, model, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            CrystalChams.INSTANCE.onRenderModel(event);
        }

        if (CrystalChams.INSTANCE.isEnabled() && ((Boolean) CrystalChams.INSTANCE.chams.getValue()).booleanValue()) {
            GL11.glPushAttrib(1048575);
            GL11.glDisable(3008);
            GL11.glDisable(3553);
            GL11.glDisable(2896);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glLineWidth(1.5F);
            GL11.glEnable(2960);
            Color hiddenColor;
            Color visibleColor;
            if (((Boolean) CrystalChams.INSTANCE.rainbow.getValue()).booleanValue()) {
                visibleColor = ((Boolean) CrystalChams.INSTANCE.colorSync.getValue()).booleanValue() ? ClickGui.getInstance().getCurrentColor() : new Color(RenderUtil.getRainbow(((Integer) CrystalChams.INSTANCE.speed.getValue()).intValue() * 100, 0, (float) ((Integer) CrystalChams.INSTANCE.saturation.getValue()).intValue() / 100.0F, (float) ((Integer) CrystalChams.INSTANCE.brightness.getValue()).intValue() / 100.0F));
                hiddenColor = EntityUtil.getColor(entity, visibleColor.getRed(), visibleColor.getGreen(), visibleColor.getBlue(), ((Integer) CrystalChams.INSTANCE.alpha.getValue()).intValue(), true);
                if (((Boolean) CrystalChams.INSTANCE.throughWalls.getValue()).booleanValue()) {
                    GL11.glDisable(2929);
                    GL11.glDepthMask(false);
                }

                GL11.glEnable(10754);
                GL11.glColor4f((float) hiddenColor.getRed() / 255.0F, (float) hiddenColor.getGreen() / 255.0F, (float) hiddenColor.getBlue() / 255.0F, (float) ((Integer) CrystalChams.INSTANCE.alpha.getValue()).intValue() / 255.0F);
                model.func_78088_a(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                if (((Boolean) CrystalChams.INSTANCE.throughWalls.getValue()).booleanValue()) {
                    GL11.glEnable(2929);
                    GL11.glDepthMask(true);
                }
            } else if (((Boolean) CrystalChams.INSTANCE.xqz.getValue()).booleanValue() && ((Boolean) CrystalChams.INSTANCE.throughWalls.getValue()).booleanValue()) {
                hiddenColor = ((Boolean) CrystalChams.INSTANCE.colorSync.getValue()).booleanValue() ? EntityUtil.getColor(entity, ((Integer) CrystalChams.INSTANCE.hiddenRed.getValue()).intValue(), ((Integer) CrystalChams.INSTANCE.hiddenGreen.getValue()).intValue(), ((Integer) CrystalChams.INSTANCE.hiddenBlue.getValue()).intValue(), ((Integer) CrystalChams.INSTANCE.hiddenAlpha.getValue()).intValue(), true) : EntityUtil.getColor(entity, ((Integer) CrystalChams.INSTANCE.hiddenRed.getValue()).intValue(), ((Integer) CrystalChams.INSTANCE.hiddenGreen.getValue()).intValue(), ((Integer) CrystalChams.INSTANCE.hiddenBlue.getValue()).intValue(), ((Integer) CrystalChams.INSTANCE.hiddenAlpha.getValue()).intValue(), true);
                visibleColor = ((Boolean) CrystalChams.INSTANCE.colorSync.getValue()).booleanValue() ? EntityUtil.getColor(entity, ((Integer) CrystalChams.INSTANCE.red.getValue()).intValue(), ((Integer) CrystalChams.INSTANCE.green.getValue()).intValue(), ((Integer) CrystalChams.INSTANCE.blue.getValue()).intValue(), ((Integer) CrystalChams.INSTANCE.alpha.getValue()).intValue(), true) : EntityUtil.getColor(entity, ((Integer) CrystalChams.INSTANCE.red.getValue()).intValue(), ((Integer) CrystalChams.INSTANCE.green.getValue()).intValue(), ((Integer) CrystalChams.INSTANCE.blue.getValue()).intValue(), ((Integer) CrystalChams.INSTANCE.alpha.getValue()).intValue(), true);
                if (((Boolean) CrystalChams.INSTANCE.throughWalls.getValue()).booleanValue()) {
                    GL11.glDisable(2929);
                    GL11.glDepthMask(false);
                }

                GL11.glEnable(10754);
                GL11.glColor4f((float) hiddenColor.getRed() / 255.0F, (float) hiddenColor.getGreen() / 255.0F, (float) hiddenColor.getBlue() / 255.0F, (float) ((Integer) CrystalChams.INSTANCE.alpha.getValue()).intValue() / 255.0F);
                model.func_78088_a(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                if (((Boolean) CrystalChams.INSTANCE.throughWalls.getValue()).booleanValue()) {
                    GL11.glEnable(2929);
                    GL11.glDepthMask(true);
                }

                GL11.glColor4f((float) visibleColor.getRed() / 255.0F, (float) visibleColor.getGreen() / 255.0F, (float) visibleColor.getBlue() / 255.0F, (float) ((Integer) CrystalChams.INSTANCE.alpha.getValue()).intValue() / 255.0F);
                model.func_78088_a(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            } else {
                visibleColor = ((Boolean) CrystalChams.INSTANCE.colorSync.getValue()).booleanValue() ? ClickGui.getInstance().getCurrentColor() : EntityUtil.getColor(entity, ((Integer) CrystalChams.INSTANCE.red.getValue()).intValue(), ((Integer) CrystalChams.INSTANCE.green.getValue()).intValue(), ((Integer) CrystalChams.INSTANCE.blue.getValue()).intValue(), ((Integer) CrystalChams.INSTANCE.alpha.getValue()).intValue(), true);
                if (((Boolean) CrystalChams.INSTANCE.throughWalls.getValue()).booleanValue()) {
                    GL11.glDisable(2929);
                    GL11.glDepthMask(false);
                }

                GL11.glEnable(10754);
                GL11.glColor4f((float) visibleColor.getRed() / 255.0F, (float) visibleColor.getGreen() / 255.0F, (float) visibleColor.getBlue() / 255.0F, (float) ((Integer) CrystalChams.INSTANCE.alpha.getValue()).intValue() / 255.0F);
                model.func_78088_a(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                if (((Boolean) CrystalChams.INSTANCE.throughWalls.getValue()).booleanValue()) {
                    GL11.glEnable(2929);
                    GL11.glDepthMask(true);
                }
            }

            GL11.glEnable(3042);
            GL11.glEnable(2896);
            GL11.glEnable(3553);
            GL11.glEnable(3008);
            GL11.glPopAttrib();
            if (((Boolean) CrystalChams.INSTANCE.glint.getValue()).booleanValue()) {
                GL11.glDisable(2929);
                GL11.glDepthMask(false);
                GlStateManager.func_179141_d();
                GlStateManager.func_179131_c(1.0F, 0.0F, 0.0F, 0.13F);
                model.func_78088_a(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                GlStateManager.func_179118_c();
                GL11.glEnable(2929);
                GL11.glDepthMask(true);
            }
        } else {
            model.func_78088_a(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        }

        if (CrystalChams.INSTANCE.isEnabled()) {
            if (((Boolean) CrystalChams.INSTANCE.animateScale.getValue()).booleanValue() && CrystalChams.INSTANCE.scaleMap.containsKey((EntityEnderCrystal) entity)) {
                GlStateManager.func_179152_a(1.0F / ((Float) CrystalChams.INSTANCE.scaleMap.get((EntityEnderCrystal) entity)).floatValue(), 1.0F / ((Float) CrystalChams.INSTANCE.scaleMap.get((EntityEnderCrystal) entity)).floatValue(), 1.0F / ((Float) CrystalChams.INSTANCE.scaleMap.get((EntityEnderCrystal) entity)).floatValue());
            } else {
                GlStateManager.func_179152_a(1.0F / ((Float) CrystalChams.INSTANCE.scale.getValue()).floatValue(), 1.0F / ((Float) CrystalChams.INSTANCE.scale.getValue()).floatValue(), 1.0F / ((Float) CrystalChams.INSTANCE.scale.getValue()).floatValue());
            }
        }

    }
}
