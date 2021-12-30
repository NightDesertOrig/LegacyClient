package me.dev.legacy.modules.render;

import me.dev.legacy.api.event.events.render.Render3DEvent;
import me.dev.legacy.api.util.EntityUtil;
import me.dev.legacy.api.util.RenderUtil;
import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.modules.Module;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Iterator;

public class ESP extends Module {
    private static ESP INSTANCE = new ESP();
    private final Setting items = this.register(new Setting("Items", false));
    private final Setting xporbs = this.register(new Setting("XpOrbs", false));
    private final Setting xpbottles = this.register(new Setting("XpBottles", false));
    private final Setting pearl = this.register(new Setting("Pearls", false));
    private final Setting red = this.register(new Setting("Red", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
    private final Setting green = this.register(new Setting("Green", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
    private final Setting blue = this.register(new Setting("Blue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
    private final Setting boxAlpha = this.register(new Setting("BoxAlpha", Integer.valueOf(120), Integer.valueOf(0), Integer.valueOf(255)));
    private final Setting alpha = this.register(new Setting("Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));

    public ESP() {
        super("ESP", "Renders a nice ESP.", Module.Category.RENDER, false, false, false);
        this.setInstance();
    }

    public static ESP getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ESP();
        }

        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    public void onRender3D(Render3DEvent event) {
        AxisAlignedBB bb;
        Vec3d interp;
        int i;
        Iterator var5;
        Entity entity;
        if (((Boolean) this.items.getValue()).booleanValue()) {
            i = 0;
            var5 = mc.field_71441_e.field_72996_f.iterator();

            while (var5.hasNext()) {
                entity = (Entity) var5.next();
                if (entity instanceof EntityItem && mc.field_71439_g.func_70068_e(entity) < 2500.0D) {
                    interp = EntityUtil.getInterpolatedRenderPos(entity, mc.func_184121_ak());
                    bb = new AxisAlignedBB(entity.func_174813_aQ().field_72340_a - 0.05D - entity.field_70165_t + interp.field_72450_a, entity.func_174813_aQ().field_72338_b - 0.0D - entity.field_70163_u + interp.field_72448_b, entity.func_174813_aQ().field_72339_c - 0.05D - entity.field_70161_v + interp.field_72449_c, entity.func_174813_aQ().field_72336_d + 0.05D - entity.field_70165_t + interp.field_72450_a, entity.func_174813_aQ().field_72337_e + 0.1D - entity.field_70163_u + interp.field_72448_b, entity.func_174813_aQ().field_72334_f + 0.05D - entity.field_70161_v + interp.field_72449_c);
                    GlStateManager.func_179094_E();
                    GlStateManager.func_179147_l();
                    GlStateManager.func_179097_i();
                    GlStateManager.func_179120_a(770, 771, 0, 1);
                    GlStateManager.func_179090_x();
                    GlStateManager.func_179132_a(false);
                    GL11.glEnable(2848);
                    GL11.glHint(3154, 4354);
                    GL11.glLineWidth(1.0F);
                    RenderGlobal.func_189696_b(bb, (float) ((Integer) this.red.getValue()).intValue() / 255.0F, (float) ((Integer) this.green.getValue()).intValue() / 255.0F, (float) ((Integer) this.blue.getValue()).intValue() / 255.0F, (float) ((Integer) this.boxAlpha.getValue()).intValue() / 255.0F);
                    GL11.glDisable(2848);
                    GlStateManager.func_179132_a(true);
                    GlStateManager.func_179126_j();
                    GlStateManager.func_179098_w();
                    GlStateManager.func_179084_k();
                    GlStateManager.func_179121_F();
                    RenderUtil.drawBlockOutline(bb, new Color(((Integer) this.red.getValue()).intValue(), ((Integer) this.green.getValue()).intValue(), ((Integer) this.blue.getValue()).intValue(), ((Integer) this.alpha.getValue()).intValue()), 1.0F);
                    ++i;
                    if (i >= 50) {
                        break;
                    }
                }
            }
        }

        if (((Boolean) this.xporbs.getValue()).booleanValue()) {
            i = 0;
            var5 = mc.field_71441_e.field_72996_f.iterator();

            while (var5.hasNext()) {
                entity = (Entity) var5.next();
                if (entity instanceof EntityXPOrb && mc.field_71439_g.func_70068_e(entity) < 2500.0D) {
                    interp = EntityUtil.getInterpolatedRenderPos(entity, mc.func_184121_ak());
                    bb = new AxisAlignedBB(entity.func_174813_aQ().field_72340_a - 0.05D - entity.field_70165_t + interp.field_72450_a, entity.func_174813_aQ().field_72338_b - 0.0D - entity.field_70163_u + interp.field_72448_b, entity.func_174813_aQ().field_72339_c - 0.05D - entity.field_70161_v + interp.field_72449_c, entity.func_174813_aQ().field_72336_d + 0.05D - entity.field_70165_t + interp.field_72450_a, entity.func_174813_aQ().field_72337_e + 0.1D - entity.field_70163_u + interp.field_72448_b, entity.func_174813_aQ().field_72334_f + 0.05D - entity.field_70161_v + interp.field_72449_c);
                    GlStateManager.func_179094_E();
                    GlStateManager.func_179147_l();
                    GlStateManager.func_179097_i();
                    GlStateManager.func_179120_a(770, 771, 0, 1);
                    GlStateManager.func_179090_x();
                    GlStateManager.func_179132_a(false);
                    GL11.glEnable(2848);
                    GL11.glHint(3154, 4354);
                    GL11.glLineWidth(1.0F);
                    RenderGlobal.func_189696_b(bb, (float) ((Integer) this.red.getValue()).intValue() / 255.0F, (float) ((Integer) this.green.getValue()).intValue() / 255.0F, (float) ((Integer) this.blue.getValue()).intValue() / 255.0F, (float) ((Integer) this.boxAlpha.getValue()).intValue() / 255.0F);
                    GL11.glDisable(2848);
                    GlStateManager.func_179132_a(true);
                    GlStateManager.func_179126_j();
                    GlStateManager.func_179098_w();
                    GlStateManager.func_179084_k();
                    GlStateManager.func_179121_F();
                    RenderUtil.drawBlockOutline(bb, new Color(((Integer) this.red.getValue()).intValue(), ((Integer) this.green.getValue()).intValue(), ((Integer) this.blue.getValue()).intValue(), ((Integer) this.alpha.getValue()).intValue()), 1.0F);
                    ++i;
                    if (i >= 50) {
                        break;
                    }
                }
            }
        }

        if (((Boolean) this.pearl.getValue()).booleanValue()) {
            i = 0;
            var5 = mc.field_71441_e.field_72996_f.iterator();

            while (var5.hasNext()) {
                entity = (Entity) var5.next();
                if (entity instanceof EntityEnderPearl && mc.field_71439_g.func_70068_e(entity) < 2500.0D) {
                    interp = EntityUtil.getInterpolatedRenderPos(entity, mc.func_184121_ak());
                    bb = new AxisAlignedBB(entity.func_174813_aQ().field_72340_a - 0.05D - entity.field_70165_t + interp.field_72450_a, entity.func_174813_aQ().field_72338_b - 0.0D - entity.field_70163_u + interp.field_72448_b, entity.func_174813_aQ().field_72339_c - 0.05D - entity.field_70161_v + interp.field_72449_c, entity.func_174813_aQ().field_72336_d + 0.05D - entity.field_70165_t + interp.field_72450_a, entity.func_174813_aQ().field_72337_e + 0.1D - entity.field_70163_u + interp.field_72448_b, entity.func_174813_aQ().field_72334_f + 0.05D - entity.field_70161_v + interp.field_72449_c);
                    GlStateManager.func_179094_E();
                    GlStateManager.func_179147_l();
                    GlStateManager.func_179097_i();
                    GlStateManager.func_179120_a(770, 771, 0, 1);
                    GlStateManager.func_179090_x();
                    GlStateManager.func_179132_a(false);
                    GL11.glEnable(2848);
                    GL11.glHint(3154, 4354);
                    GL11.glLineWidth(1.0F);
                    RenderGlobal.func_189696_b(bb, (float) ((Integer) this.red.getValue()).intValue() / 255.0F, (float) ((Integer) this.green.getValue()).intValue() / 255.0F, (float) ((Integer) this.blue.getValue()).intValue() / 255.0F, (float) ((Integer) this.boxAlpha.getValue()).intValue() / 255.0F);
                    GL11.glDisable(2848);
                    GlStateManager.func_179132_a(true);
                    GlStateManager.func_179126_j();
                    GlStateManager.func_179098_w();
                    GlStateManager.func_179084_k();
                    GlStateManager.func_179121_F();
                    RenderUtil.drawBlockOutline(bb, new Color(((Integer) this.red.getValue()).intValue(), ((Integer) this.green.getValue()).intValue(), ((Integer) this.blue.getValue()).intValue(), ((Integer) this.alpha.getValue()).intValue()), 1.0F);
                    ++i;
                    if (i >= 50) {
                        break;
                    }
                }
            }
        }

        if (((Boolean) this.xpbottles.getValue()).booleanValue()) {
            i = 0;
            var5 = mc.field_71441_e.field_72996_f.iterator();

            while (var5.hasNext()) {
                entity = (Entity) var5.next();
                if (entity instanceof EntityExpBottle && mc.field_71439_g.func_70068_e(entity) < 2500.0D) {
                    interp = EntityUtil.getInterpolatedRenderPos(entity, mc.func_184121_ak());
                    bb = new AxisAlignedBB(entity.func_174813_aQ().field_72340_a - 0.05D - entity.field_70165_t + interp.field_72450_a, entity.func_174813_aQ().field_72338_b - 0.0D - entity.field_70163_u + interp.field_72448_b, entity.func_174813_aQ().field_72339_c - 0.05D - entity.field_70161_v + interp.field_72449_c, entity.func_174813_aQ().field_72336_d + 0.05D - entity.field_70165_t + interp.field_72450_a, entity.func_174813_aQ().field_72337_e + 0.1D - entity.field_70163_u + interp.field_72448_b, entity.func_174813_aQ().field_72334_f + 0.05D - entity.field_70161_v + interp.field_72449_c);
                    GlStateManager.func_179094_E();
                    GlStateManager.func_179147_l();
                    GlStateManager.func_179097_i();
                    GlStateManager.func_179120_a(770, 771, 0, 1);
                    GlStateManager.func_179090_x();
                    GlStateManager.func_179132_a(false);
                    GL11.glEnable(2848);
                    GL11.glHint(3154, 4354);
                    GL11.glLineWidth(1.0F);
                    RenderGlobal.func_189696_b(bb, (float) ((Integer) this.red.getValue()).intValue() / 255.0F, (float) ((Integer) this.green.getValue()).intValue() / 255.0F, (float) ((Integer) this.blue.getValue()).intValue() / 255.0F, (float) ((Integer) this.boxAlpha.getValue()).intValue() / 255.0F);
                    GL11.glDisable(2848);
                    GlStateManager.func_179132_a(true);
                    GlStateManager.func_179126_j();
                    GlStateManager.func_179098_w();
                    GlStateManager.func_179084_k();
                    GlStateManager.func_179121_F();
                    RenderUtil.drawBlockOutline(bb, new Color(((Integer) this.red.getValue()).intValue(), ((Integer) this.green.getValue()).intValue(), ((Integer) this.blue.getValue()).intValue(), ((Integer) this.alpha.getValue()).intValue()), 1.0F);
                    ++i;
                    if (i >= 50) {
                        break;
                    }
                }
            }
        }

    }
}
