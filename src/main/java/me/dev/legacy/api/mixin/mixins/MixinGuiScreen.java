package me.dev.legacy.api.mixin.mixins;

import me.dev.legacy.modules.misc.ShulkerViewer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({GuiScreen.class})
public class MixinGuiScreen extends Gui {
    @Inject(
            method = {"renderToolTip"},
            at = {@At("HEAD")},
            cancellable = true
    )
    public void renderToolTipHook(ItemStack stack, int x, int y, CallbackInfo info) {
        if (ShulkerViewer.getInstance().isOn() && stack.func_77973_b() instanceof ItemShulkerBox) {
            ShulkerViewer.getInstance().renderShulkerToolTip(stack, x, y, (String) null);
            info.cancel();
        }

    }
}
