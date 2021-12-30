package me.dev.legacy.api.mixin.mixins;

import com.google.common.base.Predicate;
import me.dev.legacy.MinecraftInstance;
import me.dev.legacy.api.event.events.render.PerspectiveEvent;
import me.dev.legacy.modules.misc.NoHitBox;
import me.dev.legacy.modules.render.NoRender;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.util.glu.Project;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin({EntityRenderer.class})
public abstract class MixinEntityRenderer {
    private boolean injection = true;
    @Shadow
    public ItemStack field_190566_ab;
    @Shadow
    @Final
    public Minecraft field_78531_r;

    @Shadow
    public abstract void func_78473_a(float var1);

    @Redirect(
            method = {"getMouseOver"},
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/multiplayer/WorldClient;getEntitiesInAABBexcluding(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/AxisAlignedBB;Lcom/google/common/base/Predicate;)Ljava/util/List;"
            )
    )
    public List getEntitiesInAABBexcluding(WorldClient worldClient, Entity entityIn, AxisAlignedBB boundingBox, Predicate predicate) {
        return (List) (!NoHitBox.getINSTANCE().isOn() || (!(Minecraft.func_71410_x().field_71439_g.func_184614_ca().func_77973_b() instanceof ItemPickaxe) || !((Boolean) NoHitBox.getINSTANCE().pickaxe.getValue()).booleanValue()) && (Minecraft.func_71410_x().field_71439_g.func_184614_ca().func_77973_b() != Items.field_185158_cP || !((Boolean) NoHitBox.getINSTANCE().crystal.getValue()).booleanValue()) && (Minecraft.func_71410_x().field_71439_g.func_184614_ca().func_77973_b() != Items.field_151153_ao || !((Boolean) NoHitBox.getINSTANCE().gapple.getValue()).booleanValue()) && Minecraft.func_71410_x().field_71439_g.func_184614_ca().func_77973_b() != Items.field_151033_d && Minecraft.func_71410_x().field_71439_g.func_184614_ca().func_77973_b() != Items.field_151142_bV ? worldClient.func_175674_a(entityIn, boundingBox, predicate) : new ArrayList());
    }

    @Redirect(
            method = {"setupCameraTransform"},
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V"
            )
    )
    private void onSetupCameraTransform(float fovy, float aspect, float zNear, float zFar) {
        PerspectiveEvent event = new PerspectiveEvent((float) MinecraftInstance.mc.field_71443_c / (float) MinecraftInstance.mc.field_71440_d);
        MinecraftForge.EVENT_BUS.post(event);
        Project.gluPerspective(fovy, event.getAspect(), zNear, zFar);
    }

    @Redirect(
            method = {"renderWorldPass"},
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V"
            )
    )
    private void onRenderWorldPass(float fovy, float aspect, float zNear, float zFar) {
        PerspectiveEvent event = new PerspectiveEvent((float) MinecraftInstance.mc.field_71443_c / (float) MinecraftInstance.mc.field_71440_d);
        MinecraftForge.EVENT_BUS.post(event);
        Project.gluPerspective(fovy, event.getAspect(), zNear, zFar);
    }

    @Redirect(
            method = {"renderCloudsCheck"},
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V"
            )
    )
    private void onRenderCloudsCheck(float fovy, float aspect, float zNear, float zFar) {
        PerspectiveEvent event = new PerspectiveEvent((float) MinecraftInstance.mc.field_71443_c / (float) MinecraftInstance.mc.field_71440_d);
        MinecraftForge.EVENT_BUS.post(event);
        Project.gluPerspective(fovy, event.getAspect(), zNear, zFar);
    }

    @Inject(
            method = {"renderItemActivation"},
            at = {@At("HEAD")},
            cancellable = true
    )
    public void renderItemActivationHook(CallbackInfo info) {
        if (this.field_190566_ab != null && NoRender.getInstance().isOn() && ((Boolean) NoRender.getInstance().totemPops.getValue()).booleanValue() && this.field_190566_ab.func_77973_b() == Items.field_190929_cY) {
            info.cancel();
        }

    }

    @Inject(
            method = {"updateLightmap"},
            at = {@At("HEAD")},
            cancellable = true
    )
    private void updateLightmap(float partialTicks, CallbackInfo info) {
        if (NoRender.getInstance().isOn() && (NoRender.getInstance().skylight.getValue() == NoRender.Skylight.ENTITY || NoRender.getInstance().skylight.getValue() == NoRender.Skylight.ALL)) {
            info.cancel();
        }

    }

    @Redirect(
            method = {"setupCameraTransform"},
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/entity/EntityPlayerSP;prevTimeInPortal:F"
            )
    )
    public float prevTimeInPortalHook(EntityPlayerSP entityPlayerSP) {
        return NoRender.getInstance().isOn() && ((Boolean) NoRender.getInstance().nausea.getValue()).booleanValue() ? -3.4028235E38F : entityPlayerSP.field_71080_cy;
    }

    @Inject(
            method = {"setupFog"},
            at = {@At("HEAD")},
            cancellable = true
    )
    public void setupFogHook(int startCoords, float partialTicks, CallbackInfo info) {
        if (NoRender.getInstance().isOn() && NoRender.getInstance().fog.getValue() == NoRender.Fog.NOFOG) {
            info.cancel();
        }

    }

    @Redirect(
            method = {"setupFog"},
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/ActiveRenderInfo;getBlockStateAtEntityViewpoint(Lnet/minecraft/world/World;Lnet/minecraft/entity/Entity;F)Lnet/minecraft/block/state/IBlockState;"
            )
    )
    public IBlockState getBlockStateAtEntityViewpointHook(World worldIn, Entity entityIn, float p_186703_2_) {
        return NoRender.getInstance().isOn() && NoRender.getInstance().fog.getValue() == NoRender.Fog.AIR ? Blocks.field_150350_a.field_176228_M : ActiveRenderInfo.func_186703_a(worldIn, entityIn, p_186703_2_);
    }

    @Inject(
            method = {"hurtCameraEffect"},
            at = {@At("HEAD")},
            cancellable = true
    )
    public void hurtCameraEffectHook(float ticks, CallbackInfo info) {
        if (NoRender.getInstance().isOn() && ((Boolean) NoRender.getInstance().hurtcam.getValue()).booleanValue()) {
            info.cancel();
        }

    }
}
