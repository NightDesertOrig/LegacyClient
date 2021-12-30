package me.dev.legacy.api.mixin.mixins;

import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({LayerBipedArmor.class})
public abstract class MixinLayerBipedArmor extends LayerArmorBase {
    public MixinLayerBipedArmor(RenderLivingBase rendererIn) {
        super(rendererIn);
    }
}
