package me.dev.legacy.modules.render;

import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.modules.Module;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

public class PlayerChams extends Module {
    public static PlayerChams INSTANCE;
    Setting page;
    public final Setting chams;
    public final Setting wireframe;
    public final Setting scale;
    public final Setting lineWidth;
    public final Setting alpha;
    public final Setting visibleRed;
    public final Setting visibleGreen;
    public final Setting visibleBlue;
    public final Setting invisibleRed;
    public final Setting invisibleGreen;
    public final Setting invisibleBlue;

    public PlayerChams() {
        super("PlayerChams", "draws chams", Module.Category.RENDER, true, false, false);
        this.page = this.register(new Setting("Page", PlayerChams.Sets.OTHER));
        this.chams = this.register(new Setting("Chams", false, (v) -> {
            return this.page.getValue() == PlayerChams.Sets.OTHER;
        }));
        this.wireframe = this.register(new Setting("Wireframe", false, (v) -> {
            return this.page.getValue() == PlayerChams.Sets.OTHER;
        }));
        this.scale = this.register(new Setting("Scale", 1.0F, 0.1F, 1.1F, (v) -> {
            return this.page.getValue() == PlayerChams.Sets.OTHER;
        }));
        this.lineWidth = this.register(new Setting("Linewidth", 1.0F, 0.1F, 3.0F, (v) -> {
            return this.page.getValue() == PlayerChams.Sets.OTHER;
        }));
        this.alpha = this.register(new Setting("Alpha", Integer.valueOf(100), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
            return this.page.getValue() == PlayerChams.Sets.COLOR;
        }));
        this.visibleRed = this.register(new Setting("Visible Red", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
            return this.page.getValue() == PlayerChams.Sets.COLOR;
        }));
        this.visibleGreen = this.register(new Setting("Visible Green", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
            return this.page.getValue() == PlayerChams.Sets.COLOR;
        }));
        this.visibleBlue = this.register(new Setting("Visible Blue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
            return this.page.getValue() == PlayerChams.Sets.COLOR;
        }));
        this.invisibleRed = this.register(new Setting("Invisible Red", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
            return this.page.getValue() == PlayerChams.Sets.COLOR;
        }));
        this.invisibleGreen = this.register(new Setting("Invisible Green", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
            return this.page.getValue() == PlayerChams.Sets.COLOR;
        }));
        this.invisibleBlue = this.register(new Setting("Invisible Blue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
            return this.page.getValue() == PlayerChams.Sets.COLOR;
        }));
        INSTANCE = this;
    }

    public final void onRenderModel(ModelBase base, Entity entity, float limbSwing, float limbSwingAmount, float age, float headYaw, float headPitch, float scale) {
        if (!(entity instanceof EntityPlayer)) {
            GL11.glPushAttrib(1048575);
            GL11.glPolygonMode(1032, 6913);
            GL11.glDisable(3553);
            GL11.glDisable(2896);
            GL11.glDisable(2929);
            GL11.glColorMaterial(1032, 5634);
            GL11.glEnable(2848);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glLineWidth(((Float) this.lineWidth.getValue()).floatValue());
            GL11.glDepthMask(false);
            GL11.glColor4d((double) ((float) ((Integer) INSTANCE.invisibleRed.getValue()).intValue() / 255.0F), (double) ((float) ((Integer) INSTANCE.invisibleGreen.getValue()).intValue() / 255.0F), (double) ((float) ((Integer) INSTANCE.invisibleBlue.getValue()).intValue() / 255.0F), (double) ((float) ((Integer) INSTANCE.alpha.getValue()).intValue() / 255.0F));
            base.func_78088_a(entity, limbSwing, limbSwingAmount, age, headYaw, headPitch, scale);
            GL11.glEnable(2929);
            GL11.glDepthMask(true);
            GL11.glColor4d((double) ((float) ((Integer) INSTANCE.visibleRed.getValue()).intValue() / 255.0F), (double) ((float) ((Integer) INSTANCE.visibleGreen.getValue()).intValue() / 255.0F), (double) ((float) ((Integer) INSTANCE.visibleBlue.getValue()).intValue() / 255.0F), (double) ((float) ((Integer) INSTANCE.alpha.getValue()).intValue() / 255.0F));
            base.func_78088_a(entity, limbSwing, limbSwingAmount, age, headYaw, headPitch, scale);
            GL11.glEnable(3042);
            GL11.glPopAttrib();
        }
    }

    public static enum Sets {
        COLOR,
        OTHER;
    }
}
