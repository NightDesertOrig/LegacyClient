package me.dev.legacy.api.mixin.mixins;

import me.dev.legacy.MinecraftInstance;
import me.dev.legacy.modules.render.PlayerChams;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin({RenderLivingBase.class})
public class MixinRenderLivingBase extends Render implements MinecraftInstance {
    @Shadow
    protected ModelBase field_77045_g;

    @Inject(
            method = {"renderModel"},
            at = {@At("HEAD")},
            cancellable = true
    )
    public void renderModel(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, CallbackInfo ci) {
        if (entitylivingbaseIn instanceof EntityPlayer && entitylivingbaseIn != mc.field_71439_g && PlayerChams.INSTANCE.isEnabled() && ((Boolean) PlayerChams.INSTANCE.chams.getValue()).booleanValue()) {
            GL11.glPushAttrib(1048575);
            GL11.glDisable(3008);
            GL11.glDisable(3553);
            GL11.glDisable(2896);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glDepthMask(false);
            GL11.glLineWidth(1.5F);
            GL11.glEnable(2960);
            GL11.glClear(1024);
            GL11.glClearStencil(15);
            GL11.glStencilFunc(512, 1, 15);
            GL11.glStencilOp(7681, 7681, 7681);
            GL11.glPolygonMode(1028, 6913);
            GL11.glStencilFunc(512, 0, 15);
            GL11.glStencilOp(7681, 7681, 7681);
            GL11.glPolygonMode(1028, 6914);
            GL11.glStencilFunc(514, 1, 15);
            GL11.glStencilOp(7680, 7680, 7680);
            GL11.glPolygonMode(1028, 6913);
            GL11.glDisable(2929);
            GL11.glDepthMask(false);
            GL11.glEnable(10754);
            OpenGlHelper.func_77475_a(OpenGlHelper.field_77476_b, 240.0F, 240.0F);
            GL11.glColor4f(1.0F, 0.0F, 0.0F, 1.0F);
            GL11.glColor4d((double) ((float) ((Integer) PlayerChams.INSTANCE.invisibleRed.getValue()).intValue() / 255.0F), (double) ((float) ((Integer) PlayerChams.INSTANCE.invisibleGreen.getValue()).intValue() / 255.0F), (double) ((float) ((Integer) PlayerChams.INSTANCE.invisibleBlue.getValue()).intValue() / 255.0F), (double) ((float) ((Integer) PlayerChams.INSTANCE.alpha.getValue()).intValue() / 255.0F));
            this.field_77045_g.func_78088_a(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
            GL11.glEnable(2929);
            GL11.glDepthMask(true);
            GL11.glColor4d((double) ((float) ((Integer) PlayerChams.INSTANCE.visibleRed.getValue()).intValue() / 255.0F), (double) ((float) ((Integer) PlayerChams.INSTANCE.visibleGreen.getValue()).intValue() / 255.0F), (double) ((float) ((Integer) PlayerChams.INSTANCE.visibleBlue.getValue()).intValue() / 255.0F), (double) ((float) ((Integer) PlayerChams.INSTANCE.alpha.getValue()).intValue() / 255.0F));
            this.field_77045_g.func_78088_a(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
            GL11.glEnable(3042);
            GL11.glEnable(2896);
            GL11.glEnable(3553);
            GL11.glEnable(3008);
            GL11.glPopAttrib();
            ci.cancel();
        }

    }

    @Inject(
            method = {"renderLayers"},
            at = {@At("RETURN")}
    )
    public void renderLayers(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scaleIn, CallbackInfo ci) {
        if (entitylivingbaseIn instanceof EntityPlayer && PlayerChams.INSTANCE.isEnabled() && ((Boolean) PlayerChams.INSTANCE.wireframe.getValue()).booleanValue()) {
            PlayerChams.INSTANCE.onRenderModel(this.field_77045_g, entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleIn);
        }

    }

    protected MixinRenderLivingBase(RenderManager renderManager) {
        super(renderManager);
    }

    @Nullable
    protected ResourceLocation func_110775_a(Entity entity) {
        return null;
    }
}
