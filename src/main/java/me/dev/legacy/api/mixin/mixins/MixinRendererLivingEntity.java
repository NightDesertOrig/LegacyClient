package me.dev.legacy.api.mixin.mixins;

import me.dev.legacy.Legacy;
import me.dev.legacy.modules.ModuleManager;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({RenderLivingBase.class})
public abstract class MixinRendererLivingEntity extends Render {
    protected ModelBase entityModel;

    protected MixinRendererLivingEntity() {
        super((RenderManager) null);
    }

    @Inject(
            method = {"doRender"},
            at = {@At("HEAD")}
    )
    public void doRenderPre(EntityLivingBase entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo info) {
        ModuleManager var10000 = Legacy.moduleManager;
        if (ModuleManager.isModuleEnabled("TexturedChams") && entity != null) {
            GL11.glEnable(32823);
            GL11.glPolygonOffset(1.0F, -1100000.0F);
        }

    }

    @Inject(
            method = {"doRender"},
            at = {@At("RETURN")}
    )
    public void doRenderPost(EntityLivingBase entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo info) {
        ModuleManager var10000 = Legacy.moduleManager;
        if (ModuleManager.isModuleEnabled("TexturedChams") && entity != null) {
            GL11.glPolygonOffset(1.0F, 1000000.0F);
            GL11.glDisable(32823);
        }

    }
}
