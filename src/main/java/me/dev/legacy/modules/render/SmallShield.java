package me.dev.legacy.modules.render;

import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.modules.Module;
import net.minecraft.client.renderer.ItemRenderer;

public class SmallShield extends Module {
    public Setting offhand = this.register(new Setting("Height", 0.7F, 0.1F, 1.0F));
    ItemRenderer itemRenderer;

    public SmallShield() {
        super("SmallShield", "Low Hands.", Module.Category.RENDER, true, false, false);
        this.itemRenderer = mc.field_71460_t.field_78516_c;
    }

    public void onUpdate() {
        this.itemRenderer.field_187471_h = ((Float) this.offhand.getValue()).floatValue();
    }
}
