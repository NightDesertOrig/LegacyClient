package me.dev.legacy.modules.render;

import me.dev.legacy.api.event.events.render.Render3DEvent;
import me.dev.legacy.api.util.ColorUtil;
import me.dev.legacy.api.util.RenderUtil;
import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.modules.Module;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;

import java.awt.*;

public class BlockHighlight extends Module {
    private final Setting lineWidth = this.register(new Setting("LineWidth", 1.0F, 0.1F, 5.0F));
    private final Setting alpha = this.register(new Setting("Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
    private final Setting red = this.register(new Setting("Red", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
    private final Setting green = this.register(new Setting("Green", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
    private final Setting blue = this.register(new Setting("Blue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
    private final Setting rainbow = this.register(new Setting("Rainbow", false));
    private final Setting rainbowhue = this.register(new Setting("RainbowHue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.rainbow.getValue()).booleanValue();
    }));

    public BlockHighlight() {
        super("BlockHighlight", "Highlights the block u look at.", Module.Category.RENDER, false, false, false);
    }

    public void onRender3D(Render3DEvent event) {
        RayTraceResult ray = mc.field_71476_x;
        if (ray != null && ray.field_72313_a == Type.BLOCK) {
            BlockPos blockpos = ray.func_178782_a();
            RenderUtil.drawBlockOutline(blockpos, ((Boolean) this.rainbow.getValue()).booleanValue() ? ColorUtil.rainbow(((Integer) this.rainbowhue.getValue()).intValue()) : new Color(((Integer) this.red.getValue()).intValue(), ((Integer) this.green.getValue()).intValue(), ((Integer) this.blue.getValue()).intValue(), ((Integer) this.alpha.getValue()).intValue()), ((Float) this.lineWidth.getValue()).floatValue(), false);
        }

    }
}
