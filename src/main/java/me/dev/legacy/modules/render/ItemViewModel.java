package me.dev.legacy.modules.render;

import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.modules.Module;

public class ItemViewModel extends Module {
    public static ItemViewModel INSTANCE;
    public final Setting translateX = this.register(new Setting("TranslateX", Integer.valueOf(0), Integer.valueOf(-100), Integer.valueOf(100)));
    public final Setting translateY = this.register(new Setting("TranslateY", Integer.valueOf(0), Integer.valueOf(-100), Integer.valueOf(100)));
    public final Setting translateZ = this.register(new Setting("TranslateZ", Integer.valueOf(0), Integer.valueOf(-100), Integer.valueOf(100)));
    public final Setting rotateX = this.register(new Setting("RotateX", Integer.valueOf(0), Integer.valueOf(-100), Integer.valueOf(100)));
    public final Setting rotateY = this.register(new Setting("RotateY", Integer.valueOf(0), Integer.valueOf(-100), Integer.valueOf(100)));
    public final Setting rotateZ = this.register(new Setting("RotateZ", Integer.valueOf(0), Integer.valueOf(-100), Integer.valueOf(100)));
    public final Setting scaleX = this.register(new Setting("ScaleX", Integer.valueOf(100), Integer.valueOf(0), Integer.valueOf(100)));
    public final Setting scaleY = this.register(new Setting("ScaleY", Integer.valueOf(100), Integer.valueOf(0), Integer.valueOf(100)));
    public final Setting scaleZ = this.register(new Setting("ScaleZ", Integer.valueOf(100), Integer.valueOf(0), Integer.valueOf(100)));

    public ItemViewModel() {
        super("ItemViewModel", "Changes to the viewmodel.", Module.Category.RENDER, false, false, false);
        INSTANCE = this;
    }
}
