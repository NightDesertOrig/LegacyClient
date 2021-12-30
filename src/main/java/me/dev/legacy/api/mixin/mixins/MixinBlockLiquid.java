package me.dev.legacy.api.mixin.mixins;

import me.dev.legacy.modules.player.LiquidInteract;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({BlockLiquid.class})
public class MixinBlockLiquid extends Block {
    protected MixinBlockLiquid(Material materialIn) {
        super(materialIn);
    }

    @Inject(
            method = {"canCollideCheck"},
            at = {@At("HEAD")},
            cancellable = true
    )
    public void canCollideCheckHook(IBlockState blockState, boolean hitIfLiquid, CallbackInfoReturnable info) {
        info.setReturnValue(hitIfLiquid && ((Integer) blockState.func_177229_b(BlockLiquid.field_176367_b)).intValue() == 0 || LiquidInteract.getInstance().isOn());
    }
}
