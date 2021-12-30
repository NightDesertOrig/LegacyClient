package me.dev.legacy.modules.render;

import me.dev.legacy.api.event.events.render.Render3DEvent;
import me.dev.legacy.api.util.BlockUtil;
import me.dev.legacy.api.util.ColorUtil;
import me.dev.legacy.api.util.RenderUtil;
import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.modules.Module;
import me.dev.legacy.modules.client.ClickGui;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import java.awt.*;

public class HoleESP extends Module {
    public Setting renderOwn = this.register(new Setting("RenderOwn", true));
    public Setting fov = this.register(new Setting("InFov", true));
    public Setting rainbow = this.register(new Setting("Rainbow", false));
    private final Setting range = this.register(new Setting("RangeX", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(10)));
    private final Setting rangeY = this.register(new Setting("RangeY", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(10)));
    public Setting box = this.register(new Setting("Box", true));
    public Setting gradientBox = this.register(new Setting("Gradient", false, (v) -> {
        return ((Boolean) this.box.getValue()).booleanValue();
    }));
    public Setting invertGradientBox = this.register(new Setting("ReverseGradient", false, (v) -> {
        return ((Boolean) this.gradientBox.getValue()).booleanValue();
    }));
    public Setting outline = this.register(new Setting("Outline", true));
    public Setting gradientOutline = this.register(new Setting("GradientOutline", false, (v) -> {
        return ((Boolean) this.outline.getValue()).booleanValue();
    }));
    public Setting invertGradientOutline = this.register(new Setting("ReverseOutline", false, (v) -> {
        return ((Boolean) this.gradientOutline.getValue()).booleanValue();
    }));
    public Setting height = this.register(new Setting("Height", 0.0D, -2.0D, 2.0D));
    private Setting red = this.register(new Setting("Red", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255)));
    private Setting green = this.register(new Setting("Green", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
    private Setting blue = this.register(new Setting("Blue", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255)));
    private Setting alpha = this.register(new Setting("Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
    private Setting boxAlpha = this.register(new Setting("BoxAlpha", Integer.valueOf(125), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.box.getValue()).booleanValue();
    }));
    private Setting lineWidth = this.register(new Setting("LineWidth", 1.0F, 0.1F, 5.0F, (v) -> {
        return ((Boolean) this.outline.getValue()).booleanValue();
    }));
    public Setting safeColor = this.register(new Setting("BedrockColor", false));
    private Setting safeRed = this.register(new Setting("BedrockRed", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.safeColor.getValue()).booleanValue();
    }));
    private Setting safeGreen = this.register(new Setting("BedrockGreen", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.safeColor.getValue()).booleanValue();
    }));
    private Setting safeBlue = this.register(new Setting("BedrockBlue", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.safeColor.getValue()).booleanValue();
    }));
    private Setting safeAlpha = this.register(new Setting("BedrockAlpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.safeColor.getValue()).booleanValue();
    }));
    public Setting customOutline = this.register(new Setting("CustomLine", false, (v) -> {
        return ((Boolean) this.outline.getValue()).booleanValue();
    }));
    private Setting cRed = this.register(new Setting("OL-Red", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.customOutline.getValue()).booleanValue() && ((Boolean) this.outline.getValue()).booleanValue();
    }));
    private Setting cGreen = this.register(new Setting("OL-Green", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.customOutline.getValue()).booleanValue() && ((Boolean) this.outline.getValue()).booleanValue();
    }));
    private Setting cBlue = this.register(new Setting("OL-Blue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.customOutline.getValue()).booleanValue() && ((Boolean) this.outline.getValue()).booleanValue();
    }));
    private Setting cAlpha = this.register(new Setting("OL-Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.customOutline.getValue()).booleanValue() && ((Boolean) this.outline.getValue()).booleanValue();
    }));
    private Setting safecRed = this.register(new Setting("OL-SafeRed", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.customOutline.getValue()).booleanValue() && ((Boolean) this.outline.getValue()).booleanValue() && ((Boolean) this.safeColor.getValue()).booleanValue();
    }));
    private Setting safecGreen = this.register(new Setting("OL-SafeGreen", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.customOutline.getValue()).booleanValue() && ((Boolean) this.outline.getValue()).booleanValue() && ((Boolean) this.safeColor.getValue()).booleanValue();
    }));
    private Setting safecBlue = this.register(new Setting("OL-SafeBlue", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.customOutline.getValue()).booleanValue() && ((Boolean) this.outline.getValue()).booleanValue() && ((Boolean) this.safeColor.getValue()).booleanValue();
    }));
    private Setting safecAlpha = this.register(new Setting("OL-SafeAlpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.customOutline.getValue()).booleanValue() && ((Boolean) this.outline.getValue()).booleanValue() && ((Boolean) this.safeColor.getValue()).booleanValue();
    }));
    private static HoleESP INSTANCE = new HoleESP();
    private int currentAlpha = 0;

    public HoleESP() {
        super("HoleESP", "Shows safe spots.", Module.Category.RENDER, false, false, false);
        this.setInstance();
    }

    private void setInstance() {
        INSTANCE = this;
    }

    public static HoleESP getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HoleESP();
        }

        return INSTANCE;
    }

    public void onRender3D(Render3DEvent event) {
        assert mc.field_175622_Z != null;

        Vec3i playerPos = new Vec3i(mc.field_175622_Z.field_70165_t, mc.field_175622_Z.field_70163_u, mc.field_175622_Z.field_70161_v);

        for (int x = playerPos.func_177958_n() - ((Integer) this.range.getValue()).intValue(); x < playerPos.func_177958_n() + ((Integer) this.range.getValue()).intValue(); ++x) {
            for (int z = playerPos.func_177952_p() - ((Integer) this.range.getValue()).intValue(); z < playerPos.func_177952_p() + ((Integer) this.range.getValue()).intValue(); ++z) {
                for (int y = playerPos.func_177956_o() + ((Integer) this.rangeY.getValue()).intValue(); y > playerPos.func_177956_o() - ((Integer) this.rangeY.getValue()).intValue(); --y) {
                    BlockPos pos = new BlockPos(x, y, z);
                    if (mc.field_71441_e.func_180495_p(pos).func_177230_c().equals(Blocks.field_150350_a) && mc.field_71441_e.func_180495_p(pos.func_177982_a(0, 1, 0)).func_177230_c().equals(Blocks.field_150350_a) && mc.field_71441_e.func_180495_p(pos.func_177982_a(0, 2, 0)).func_177230_c().equals(Blocks.field_150350_a) && (!pos.equals(new BlockPos(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v)) || ((Boolean) this.renderOwn.getValue()).booleanValue()) && (BlockUtil.isPosInFov(pos).booleanValue() || !((Boolean) this.fov.getValue()).booleanValue())) {
                        if (mc.field_71441_e.func_180495_p(pos.func_177978_c()).func_177230_c() == Blocks.field_150357_h && mc.field_71441_e.func_180495_p(pos.func_177974_f()).func_177230_c() == Blocks.field_150357_h && mc.field_71441_e.func_180495_p(pos.func_177976_e()).func_177230_c() == Blocks.field_150357_h && mc.field_71441_e.func_180495_p(pos.func_177968_d()).func_177230_c() == Blocks.field_150357_h && mc.field_71441_e.func_180495_p(pos.func_177977_b()).func_177230_c() == Blocks.field_150357_h) {
                            RenderUtil.drawBoxESP(pos, ((Boolean) this.rainbow.getValue()).booleanValue() ? ColorUtil.rainbow(((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()) : new Color(((Integer) this.safeRed.getValue()).intValue(), ((Integer) this.safeGreen.getValue()).intValue(), ((Integer) this.safeBlue.getValue()).intValue(), ((Integer) this.safeAlpha.getValue()).intValue()), ((Boolean) this.customOutline.getValue()).booleanValue(), new Color(((Integer) this.safecRed.getValue()).intValue(), ((Integer) this.safecGreen.getValue()).intValue(), ((Integer) this.safecBlue.getValue()).intValue(), ((Integer) this.safecAlpha.getValue()).intValue()), ((Float) this.lineWidth.getValue()).floatValue(), ((Boolean) this.outline.getValue()).booleanValue(), ((Boolean) this.box.getValue()).booleanValue(), ((Integer) this.boxAlpha.getValue()).intValue(), true, ((Double) this.height.getValue()).doubleValue(), ((Boolean) this.gradientBox.getValue()).booleanValue(), ((Boolean) this.gradientOutline.getValue()).booleanValue(), ((Boolean) this.invertGradientBox.getValue()).booleanValue(), ((Boolean) this.invertGradientOutline.getValue()).booleanValue(), this.currentAlpha);
                        } else if (BlockUtil.isBlockUnSafe(mc.field_71441_e.func_180495_p(pos.func_177977_b()).func_177230_c()) && BlockUtil.isBlockUnSafe(mc.field_71441_e.func_180495_p(pos.func_177974_f()).func_177230_c()) && BlockUtil.isBlockUnSafe(mc.field_71441_e.func_180495_p(pos.func_177976_e()).func_177230_c()) && BlockUtil.isBlockUnSafe(mc.field_71441_e.func_180495_p(pos.func_177968_d()).func_177230_c()) && BlockUtil.isBlockUnSafe(mc.field_71441_e.func_180495_p(pos.func_177978_c()).func_177230_c())) {
                            RenderUtil.drawBoxESP(pos, ((Boolean) this.rainbow.getValue()).booleanValue() ? ColorUtil.rainbow(((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()) : new Color(((Integer) this.red.getValue()).intValue(), ((Integer) this.green.getValue()).intValue(), ((Integer) this.blue.getValue()).intValue(), ((Integer) this.alpha.getValue()).intValue()), ((Boolean) this.customOutline.getValue()).booleanValue(), new Color(((Integer) this.cRed.getValue()).intValue(), ((Integer) this.cGreen.getValue()).intValue(), ((Integer) this.cBlue.getValue()).intValue(), ((Integer) this.cAlpha.getValue()).intValue()), ((Float) this.lineWidth.getValue()).floatValue(), ((Boolean) this.outline.getValue()).booleanValue(), ((Boolean) this.box.getValue()).booleanValue(), ((Integer) this.boxAlpha.getValue()).intValue(), true, ((Double) this.height.getValue()).doubleValue(), ((Boolean) this.gradientBox.getValue()).booleanValue(), ((Boolean) this.gradientOutline.getValue()).booleanValue(), ((Boolean) this.invertGradientBox.getValue()).booleanValue(), ((Boolean) this.invertGradientOutline.getValue()).booleanValue(), this.currentAlpha);
                        }
                    }
                }
            }
        }

    }
}
