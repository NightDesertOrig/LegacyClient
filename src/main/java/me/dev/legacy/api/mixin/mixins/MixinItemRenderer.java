package me.dev.legacy.api.mixin.mixins;

import me.dev.legacy.modules.render.ItemViewModel;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ItemRenderer.class})
public class MixinItemRenderer {
    @Inject(
            method = {"renderItemSide"},
            at = {@At("HEAD")}
    )
    public void renderItemSide(EntityLivingBase entitylivingbaseIn, ItemStack heldStack, TransformType transform, boolean leftHanded, CallbackInfo ci) {
        if (ItemViewModel.INSTANCE.isEnabled()) {
            GlStateManager.func_179152_a((float) ((Integer) ItemViewModel.INSTANCE.scaleX.getValue()).intValue() / 100.0F, (float) ((Integer) ItemViewModel.INSTANCE.scaleY.getValue()).intValue() / 100.0F, (float) ((Integer) ItemViewModel.INSTANCE.scaleZ.getValue()).intValue() / 100.0F);
            if (transform == TransformType.FIRST_PERSON_RIGHT_HAND) {
                GlStateManager.func_179109_b((float) ((Integer) ItemViewModel.INSTANCE.translateX.getValue()).intValue() / 100.0F, (float) ((Integer) ItemViewModel.INSTANCE.translateY.getValue()).intValue() / 100.0F, (float) ((Integer) ItemViewModel.INSTANCE.translateZ.getValue()).intValue() / 100.0F);
                GlStateManager.func_179114_b((float) ((Integer) ItemViewModel.INSTANCE.rotateX.getValue()).intValue(), 1.0F, 0.0F, 0.0F);
                GlStateManager.func_179114_b((float) ((Integer) ItemViewModel.INSTANCE.rotateY.getValue()).intValue(), 0.0F, 1.0F, 0.0F);
                GlStateManager.func_179114_b((float) ((Integer) ItemViewModel.INSTANCE.rotateZ.getValue()).intValue(), 0.0F, 0.0F, 1.0F);
            } else if (transform == TransformType.FIRST_PERSON_LEFT_HAND) {
                GlStateManager.func_179109_b((float) (-((Integer) ItemViewModel.INSTANCE.translateX.getValue()).intValue()) / 100.0F, (float) ((Integer) ItemViewModel.INSTANCE.translateY.getValue()).intValue() / 100.0F, (float) ((Integer) ItemViewModel.INSTANCE.translateZ.getValue()).intValue() / 100.0F);
                GlStateManager.func_179114_b((float) (-((Integer) ItemViewModel.INSTANCE.rotateX.getValue()).intValue()), 1.0F, 0.0F, 0.0F);
                GlStateManager.func_179114_b((float) ((Integer) ItemViewModel.INSTANCE.rotateY.getValue()).intValue(), 0.0F, 1.0F, 0.0F);
                GlStateManager.func_179114_b((float) ((Integer) ItemViewModel.INSTANCE.rotateZ.getValue()).intValue(), 0.0F, 0.0F, 1.0F);
            }
        }

    }
}
