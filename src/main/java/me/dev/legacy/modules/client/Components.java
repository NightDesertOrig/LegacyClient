package me.dev.legacy.modules.client;

import me.dev.legacy.Legacy;
import me.dev.legacy.api.event.events.other.PacketEvent;
import me.dev.legacy.api.event.events.render.Render2DEvent;
import me.dev.legacy.api.util.EntityUtil;
import me.dev.legacy.api.util.MathUtil;
import me.dev.legacy.api.util.RenderUtil;
import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.modules.Module;
import net.minecraft.block.Block;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.DestroyBlockProgress;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Components extends Module {
    private static final ResourceLocation box = new ResourceLocation("textures/gui/container/shulker_box.png");
    private static final double HALF_PI = 1.5707963267948966D;
    public static ResourceLocation logo = new ResourceLocation("textures/legacy-logo.png");
    public Setting inventory = this.register(new Setting("Inventory", false));
    public Setting invX = this.register(new Setting("InvX", Integer.valueOf(564), Integer.valueOf(0), Integer.valueOf(1000), (v) -> {
        return ((Boolean) this.inventory.getValue()).booleanValue();
    }));
    public Setting invY = this.register(new Setting("InvY", Integer.valueOf(467), Integer.valueOf(0), Integer.valueOf(1000), (v) -> {
        return ((Boolean) this.inventory.getValue()).booleanValue();
    }));
    public Setting fineinvX = this.register(new Setting("InvFineX", Integer.valueOf(0), (v) -> {
        return ((Boolean) this.inventory.getValue()).booleanValue();
    }));
    public Setting fineinvY = this.register(new Setting("InvFineY", Integer.valueOf(0), (v) -> {
        return ((Boolean) this.inventory.getValue()).booleanValue();
    }));
    public Setting renderXCarry = this.register(new Setting("RenderXCarry", false, (v) -> {
        return ((Boolean) this.inventory.getValue()).booleanValue();
    }));
    public Setting invH = this.register(new Setting("InvH", Integer.valueOf(3), (v) -> {
        return ((Boolean) this.inventory.getValue()).booleanValue();
    }));
    public Setting holeHud = this.register(new Setting("HoleHUD", false));
    public Setting holeX = this.register(new Setting("HoleX", Integer.valueOf(279), Integer.valueOf(0), Integer.valueOf(1000), (v) -> {
        return ((Boolean) this.holeHud.getValue()).booleanValue();
    }));
    public Setting holeY = this.register(new Setting("HoleY", Integer.valueOf(485), Integer.valueOf(0), Integer.valueOf(1000), (v) -> {
        return ((Boolean) this.holeHud.getValue()).booleanValue();
    }));
    public Setting compass;
    public Setting compassX;
    public Setting compassY;
    public Setting scale;
    public Setting playerViewer;
    public Setting playerViewerX;
    public Setting playerViewerY;
    public Setting playerScale;
    public Setting imageLogo;
    public Setting imageX;
    public Setting imageY;
    public Setting imageWidth;
    public Setting imageHeight;
    public Setting clock;
    public Setting clockFill;
    public Setting clockX;
    public Setting clockY;
    public Setting clockRadius;
    public Setting clockLineWidth;
    public Setting clockSlices;
    public Setting clockLoops;
    private final Map hotbarMap;

    public Components() {
        super("Components", "HudComponents", Module.Category.CLIENT, false, false, true);
        this.compass = this.register(new Setting("Compass", Components.Compass.NONE));
        this.compassX = this.register(new Setting("CompX", Integer.valueOf(472), Integer.valueOf(0), Integer.valueOf(1000), (v) -> {
            return this.compass.getValue() != Components.Compass.NONE;
        }));
        this.compassY = this.register(new Setting("CompY", Integer.valueOf(424), Integer.valueOf(0), Integer.valueOf(1000), (v) -> {
            return this.compass.getValue() != Components.Compass.NONE;
        }));
        this.scale = this.register(new Setting("Scale", Integer.valueOf(3), Integer.valueOf(0), Integer.valueOf(10), (v) -> {
            return this.compass.getValue() != Components.Compass.NONE;
        }));
        this.playerViewer = this.register(new Setting("PlayerViewer", false));
        this.playerViewerX = this.register(new Setting("PlayerX", Integer.valueOf(752), Integer.valueOf(0), Integer.valueOf(1000), (v) -> {
            return ((Boolean) this.playerViewer.getValue()).booleanValue();
        }));
        this.playerViewerY = this.register(new Setting("PlayerY", Integer.valueOf(497), Integer.valueOf(0), Integer.valueOf(1000), (v) -> {
            return ((Boolean) this.playerViewer.getValue()).booleanValue();
        }));
        this.playerScale = this.register(new Setting("PlayerScale", 1.0F, 0.1F, 2.0F, (v) -> {
            return ((Boolean) this.playerViewer.getValue()).booleanValue();
        }));
        this.imageLogo = this.register(new Setting("ImageLogo", false));
        this.imageX = this.register(new Setting("ImageX", Integer.valueOf(2), Integer.valueOf(0), Integer.valueOf(1000), (v) -> {
            return ((Boolean) this.imageLogo.getValue()).booleanValue();
        }));
        this.imageY = this.register(new Setting("ImageY", Integer.valueOf(2), Integer.valueOf(0), Integer.valueOf(1000), (v) -> {
            return ((Boolean) this.imageLogo.getValue()).booleanValue();
        }));
        this.imageWidth = this.register(new Setting("ImageWidth", Integer.valueOf(100), Integer.valueOf(0), Integer.valueOf(1000), (v) -> {
            return ((Boolean) this.imageLogo.getValue()).booleanValue();
        }));
        this.imageHeight = this.register(new Setting("ImageHeight", Integer.valueOf(100), Integer.valueOf(0), Integer.valueOf(1000), (v) -> {
            return ((Boolean) this.imageLogo.getValue()).booleanValue();
        }));
        this.clock = this.register(new Setting("Clock", true));
        this.clockFill = this.register(new Setting("ClockFill", true));
        this.clockX = this.register(new Setting("ClockX", 2.0F, 0.0F, 1000.0F, (v) -> {
            return ((Boolean) this.clock.getValue()).booleanValue();
        }));
        this.clockY = this.register(new Setting("ClockY", 2.0F, 0.0F, 1000.0F, (v) -> {
            return ((Boolean) this.clock.getValue()).booleanValue();
        }));
        this.clockRadius = this.register(new Setting("ClockRadius", 6.0F, 0.0F, 100.0F, (v) -> {
            return ((Boolean) this.clock.getValue()).booleanValue();
        }));
        this.clockLineWidth = this.register(new Setting("ClockLineWidth", 1.0F, 0.0F, 5.0F, (v) -> {
            return ((Boolean) this.clock.getValue()).booleanValue();
        }));
        this.clockSlices = this.register(new Setting("ClockSlices", Integer.valueOf(360), Integer.valueOf(1), Integer.valueOf(720), (v) -> {
            return ((Boolean) this.clock.getValue()).booleanValue();
        }));
        this.clockLoops = this.register(new Setting("ClockLoops", Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(720), (v) -> {
            return ((Boolean) this.clock.getValue()).booleanValue();
        }));
        this.hotbarMap = new HashMap();
    }

    public static EntityPlayer getClosestEnemy() {
        EntityPlayer closestPlayer = null;
        Iterator var1 = mc.field_71441_e.field_73010_i.iterator();

        while (var1.hasNext()) {
            EntityPlayer player = (EntityPlayer) var1.next();
            if (player != mc.field_71439_g && !Legacy.friendManager.isFriend(player)) {
                if (closestPlayer == null) {
                    closestPlayer = player;
                } else if (mc.field_71439_g.func_70068_e(player) < mc.field_71439_g.func_70068_e(closestPlayer)) {
                    closestPlayer = player;
                }
            }
        }

        return closestPlayer;
    }

    private static double getPosOnCompass(Components.Direction dir) {
        double yaw = Math.toRadians((double) MathHelper.func_76142_g(mc.field_71439_g.field_70177_z));
        int index = dir.ordinal();
        return yaw + (double) index * 1.5707963267948966D;
    }

    private static void preboxrender() {
        GL11.glPushMatrix();
        GlStateManager.func_179094_E();
        GlStateManager.func_179118_c();
        GlStateManager.func_179086_m(256);
        GlStateManager.func_179147_l();
        GlStateManager.func_179131_c(255.0F, 255.0F, 255.0F, 255.0F);
    }

    private static void postboxrender() {
        GlStateManager.func_179084_k();
        GlStateManager.func_179097_i();
        GlStateManager.func_179140_f();
        GlStateManager.func_179126_j();
        GlStateManager.func_179141_d();
        GlStateManager.func_179121_F();
        GL11.glPopMatrix();
    }

    private static void preitemrender() {
        GL11.glPushMatrix();
        GL11.glDepthMask(true);
        GlStateManager.func_179086_m(256);
        GlStateManager.func_179097_i();
        GlStateManager.func_179126_j();
        RenderHelper.func_74519_b();
        GlStateManager.func_179152_a(1.0F, 1.0F, 0.01F);
    }

    private static void postitemrender() {
        GlStateManager.func_179152_a(1.0F, 1.0F, 1.0F);
        RenderHelper.func_74518_a();
        GlStateManager.func_179141_d();
        GlStateManager.func_179084_k();
        GlStateManager.func_179140_f();
        GlStateManager.func_179139_a(0.5D, 0.5D, 0.5D);
        GlStateManager.func_179097_i();
        GlStateManager.func_179126_j();
        GlStateManager.func_179152_a(2.0F, 2.0F, 2.0F);
        GL11.glPopMatrix();
    }

    public static void drawCompleteImage(int posX, int posY, int width, int height) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float) posX, (float) posY, 0.0F);
        GL11.glBegin(7);
        GL11.glTexCoord2f(0.0F, 0.0F);
        GL11.glVertex3f(0.0F, 0.0F, 0.0F);
        GL11.glTexCoord2f(0.0F, 1.0F);
        GL11.glVertex3f(0.0F, (float) height, 0.0F);
        GL11.glTexCoord2f(1.0F, 1.0F);
        GL11.glVertex3f((float) width, (float) height, 0.0F);
        GL11.glTexCoord2f(1.0F, 0.0F);
        GL11.glVertex3f((float) width, 0.0F, 0.0F);
        GL11.glEnd();
        GL11.glPopMatrix();
    }

    public void onRender2D(Render2DEvent event) {
        if (!fullNullCheck()) {
            if (((Boolean) this.playerViewer.getValue()).booleanValue()) {
                this.drawPlayer();
            }

            if (this.compass.getValue() != Components.Compass.NONE) {
                this.drawCompass();
            }

            if (((Boolean) this.holeHud.getValue()).booleanValue()) {
                this.drawOverlay(event.partialTicks);
            }

            if (((Boolean) this.inventory.getValue()).booleanValue()) {
                this.renderInventory();
            }

            if (((Boolean) this.imageLogo.getValue()).booleanValue()) {
                this.drawImageLogo();
            }

            if (((Boolean) this.clock.getValue()).booleanValue()) {
                RenderUtil.drawClock(((Float) this.clockX.getValue()).floatValue(), ((Float) this.clockY.getValue()).floatValue(), ((Float) this.clockRadius.getValue()).floatValue(), ((Integer) this.clockSlices.getValue()).intValue(), ((Integer) this.clockLoops.getValue()).intValue(), ((Float) this.clockLineWidth.getValue()).floatValue(), ((Boolean) this.clockFill.getValue()).booleanValue(), new Color(255, 0, 0, 255));
            }

        }
    }

    @SubscribeEvent
    public void onReceivePacket(PacketEvent.Receive event) {
    }

    public void drawImageLogo() {
        GlStateManager.func_179098_w();
        GlStateManager.func_179084_k();
        mc.func_110434_K().func_110577_a(logo);
        drawCompleteImage(((Integer) this.imageX.getValue()).intValue(), ((Integer) this.imageY.getValue()).intValue(), ((Integer) this.imageWidth.getValue()).intValue(), ((Integer) this.imageHeight.getValue()).intValue());
        mc.func_110434_K().func_147645_c(logo);
        GlStateManager.func_179147_l();
        GlStateManager.func_179090_x();
    }

    public void drawCompass() {
        ScaledResolution sr = new ScaledResolution(mc);
        if (this.compass.getValue() == Components.Compass.LINE) {
            float playerYaw = mc.field_71439_g.field_70177_z;
            float rotationYaw = MathUtil.wrap(playerYaw);
            RenderUtil.drawRect((float) ((Integer) this.compassX.getValue()).intValue(), (float) ((Integer) this.compassY.getValue()).intValue(), (float) (((Integer) this.compassX.getValue()).intValue() + 100), (float) (((Integer) this.compassY.getValue()).intValue() + this.renderer.getFontHeight()), 1963986960);
            RenderUtil.glScissor((float) ((Integer) this.compassX.getValue()).intValue(), (float) ((Integer) this.compassY.getValue()).intValue(), (float) (((Integer) this.compassX.getValue()).intValue() + 100), (float) (((Integer) this.compassY.getValue()).intValue() + this.renderer.getFontHeight()), sr);
            GL11.glEnable(3089);
            float zeroZeroYaw = MathUtil.wrap((float) (Math.atan2(0.0D - mc.field_71439_g.field_70161_v, 0.0D - mc.field_71439_g.field_70165_t) * 180.0D / 3.141592653589793D) - 90.0F);
            RenderUtil.drawLine((float) ((Integer) this.compassX.getValue()).intValue() - rotationYaw + 50.0F + zeroZeroYaw, (float) (((Integer) this.compassY.getValue()).intValue() + 2), (float) ((Integer) this.compassX.getValue()).intValue() - rotationYaw + 50.0F + zeroZeroYaw, (float) (((Integer) this.compassY.getValue()).intValue() + this.renderer.getFontHeight() - 2), 2.0F, -61424);
            RenderUtil.drawLine((float) ((Integer) this.compassX.getValue()).intValue() - rotationYaw + 50.0F + 45.0F, (float) (((Integer) this.compassY.getValue()).intValue() + 2), (float) ((Integer) this.compassX.getValue()).intValue() - rotationYaw + 50.0F + 45.0F, (float) (((Integer) this.compassY.getValue()).intValue() + this.renderer.getFontHeight() - 2), 2.0F, -1);
            RenderUtil.drawLine((float) ((Integer) this.compassX.getValue()).intValue() - rotationYaw + 50.0F - 45.0F, (float) (((Integer) this.compassY.getValue()).intValue() + 2), (float) ((Integer) this.compassX.getValue()).intValue() - rotationYaw + 50.0F - 45.0F, (float) (((Integer) this.compassY.getValue()).intValue() + this.renderer.getFontHeight() - 2), 2.0F, -1);
            RenderUtil.drawLine((float) ((Integer) this.compassX.getValue()).intValue() - rotationYaw + 50.0F + 135.0F, (float) (((Integer) this.compassY.getValue()).intValue() + 2), (float) ((Integer) this.compassX.getValue()).intValue() - rotationYaw + 50.0F + 135.0F, (float) (((Integer) this.compassY.getValue()).intValue() + this.renderer.getFontHeight() - 2), 2.0F, -1);
            RenderUtil.drawLine((float) ((Integer) this.compassX.getValue()).intValue() - rotationYaw + 50.0F - 135.0F, (float) (((Integer) this.compassY.getValue()).intValue() + 2), (float) ((Integer) this.compassX.getValue()).intValue() - rotationYaw + 50.0F - 135.0F, (float) (((Integer) this.compassY.getValue()).intValue() + this.renderer.getFontHeight() - 2), 2.0F, -1);
            this.renderer.drawStringWithShadow("n", (float) ((Integer) this.compassX.getValue()).intValue() - rotationYaw + 50.0F + 180.0F - (float) this.renderer.getStringWidth("n") / 2.0F, (float) ((Integer) this.compassY.getValue()).intValue(), -1);
            this.renderer.drawStringWithShadow("n", (float) ((Integer) this.compassX.getValue()).intValue() - rotationYaw + 50.0F - 180.0F - (float) this.renderer.getStringWidth("n") / 2.0F, (float) ((Integer) this.compassY.getValue()).intValue(), -1);
            this.renderer.drawStringWithShadow("e", (float) ((Integer) this.compassX.getValue()).intValue() - rotationYaw + 50.0F - 90.0F - (float) this.renderer.getStringWidth("e") / 2.0F, (float) ((Integer) this.compassY.getValue()).intValue(), -1);
            this.renderer.drawStringWithShadow("s", (float) ((Integer) this.compassX.getValue()).intValue() - rotationYaw + 50.0F - (float) this.renderer.getStringWidth("s") / 2.0F, (float) ((Integer) this.compassY.getValue()).intValue(), -1);
            this.renderer.drawStringWithShadow("w", (float) ((Integer) this.compassX.getValue()).intValue() - rotationYaw + 50.0F + 90.0F - (float) this.renderer.getStringWidth("w") / 2.0F, (float) ((Integer) this.compassY.getValue()).intValue(), -1);
            RenderUtil.drawLine((float) (((Integer) this.compassX.getValue()).intValue() + 50), (float) (((Integer) this.compassY.getValue()).intValue() + 1), (float) (((Integer) this.compassX.getValue()).intValue() + 50), (float) (((Integer) this.compassY.getValue()).intValue() + this.renderer.getFontHeight() - 1), 2.0F, -7303024);
            GL11.glDisable(3089);
        } else {
            double centerX = (double) ((Integer) this.compassX.getValue()).intValue();
            double centerY = (double) ((Integer) this.compassY.getValue()).intValue();
            Components.Direction[] var6 = Components.Direction.values();
            int var7 = var6.length;

            for (int var8 = 0; var8 < var7; ++var8) {
                Components.Direction dir = var6[var8];
                double rad = getPosOnCompass(dir);
                this.renderer.drawStringWithShadow(dir.name(), (float) (centerX + this.getX(rad)), (float) (centerY + this.getY(rad)), dir == Components.Direction.N ? -65536 : -1);
            }
        }

    }

    public void drawPlayer(EntityPlayer player, int x, int y) {
        EntityPlayer ent = player;
        GlStateManager.func_179094_E();
        GlStateManager.func_179124_c(1.0F, 1.0F, 1.0F);
        RenderHelper.func_74519_b();
        GlStateManager.func_179141_d();
        GlStateManager.func_179103_j(7424);
        GlStateManager.func_179141_d();
        GlStateManager.func_179126_j();
        GlStateManager.func_179114_b(0.0F, 0.0F, 5.0F, 0.0F);
        GlStateManager.func_179142_g();
        GlStateManager.func_179094_E();
        GlStateManager.func_179109_b((float) (((Integer) this.playerViewerX.getValue()).intValue() + 25), (float) (((Integer) this.playerViewerY.getValue()).intValue() + 25), 50.0F);
        GlStateManager.func_179152_a(-50.0F * ((Float) this.playerScale.getValue()).floatValue(), 50.0F * ((Float) this.playerScale.getValue()).floatValue(), 50.0F * ((Float) this.playerScale.getValue()).floatValue());
        GlStateManager.func_179114_b(180.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.func_179114_b(135.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.func_74519_b();
        GlStateManager.func_179114_b(-135.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.func_179114_b(-((float) Math.atan((double) ((float) ((Integer) this.playerViewerY.getValue()).intValue() / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.func_179109_b(0.0F, 0.0F, 0.0F);
        RenderManager rendermanager = mc.func_175598_ae();
        rendermanager.func_178631_a(180.0F);
        rendermanager.func_178633_a(false);

        try {
            rendermanager.func_188391_a(ent, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
        } catch (Exception var7) {
            ;
        }

        rendermanager.func_178633_a(true);
        GlStateManager.func_179121_F();
        RenderHelper.func_74518_a();
        GlStateManager.func_179101_C();
        GlStateManager.func_179138_g(OpenGlHelper.field_77476_b);
        GlStateManager.func_179090_x();
        GlStateManager.func_179138_g(OpenGlHelper.field_77478_a);
        GlStateManager.func_179143_c(515);
        GlStateManager.func_179117_G();
        GlStateManager.func_179097_i();
        GlStateManager.func_179121_F();
    }

    public void drawPlayer() {
        EntityPlayerSP ent = mc.field_71439_g;
        GlStateManager.func_179094_E();
        GlStateManager.func_179124_c(1.0F, 1.0F, 1.0F);
        RenderHelper.func_74519_b();
        GlStateManager.func_179141_d();
        GlStateManager.func_179103_j(7424);
        GlStateManager.func_179141_d();
        GlStateManager.func_179126_j();
        GlStateManager.func_179114_b(0.0F, 0.0F, 5.0F, 0.0F);
        GlStateManager.func_179142_g();
        GlStateManager.func_179094_E();
        GlStateManager.func_179109_b((float) (((Integer) this.playerViewerX.getValue()).intValue() + 25), (float) (((Integer) this.playerViewerY.getValue()).intValue() + 25), 50.0F);
        GlStateManager.func_179152_a(-50.0F * ((Float) this.playerScale.getValue()).floatValue(), 50.0F * ((Float) this.playerScale.getValue()).floatValue(), 50.0F * ((Float) this.playerScale.getValue()).floatValue());
        GlStateManager.func_179114_b(180.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.func_179114_b(135.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.func_74519_b();
        GlStateManager.func_179114_b(-135.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.func_179114_b(-((float) Math.atan((double) ((float) ((Integer) this.playerViewerY.getValue()).intValue() / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.func_179109_b(0.0F, 0.0F, 0.0F);
        RenderManager rendermanager = mc.func_175598_ae();
        rendermanager.func_178631_a(180.0F);
        rendermanager.func_178633_a(false);

        try {
            rendermanager.func_188391_a(ent, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
        } catch (Exception var4) {
            ;
        }

        rendermanager.func_178633_a(true);
        GlStateManager.func_179121_F();
        RenderHelper.func_74518_a();
        GlStateManager.func_179101_C();
        GlStateManager.func_179138_g(OpenGlHelper.field_77476_b);
        GlStateManager.func_179090_x();
        GlStateManager.func_179138_g(OpenGlHelper.field_77478_a);
        GlStateManager.func_179143_c(515);
        GlStateManager.func_179117_G();
        GlStateManager.func_179097_i();
        GlStateManager.func_179121_F();
    }

    private double getX(double rad) {
        return Math.sin(rad) * (double) (((Integer) this.scale.getValue()).intValue() * 10);
    }

    private double getY(double rad) {
        double epicPitch = (double) MathHelper.func_76131_a(mc.field_71439_g.field_70125_A + 30.0F, -90.0F, 90.0F);
        double pitchRadians = Math.toRadians(epicPitch);
        return Math.cos(rad) * Math.sin(pitchRadians) * (double) (((Integer) this.scale.getValue()).intValue() * 10);
    }

    public void drawOverlay(float partialTicks) {
        float yaw = 0.0F;
        int dir = MathHelper.func_76128_c((double) (mc.field_71439_g.field_70177_z * 4.0F / 360.0F) + 0.5D) & 3;
        switch (dir) {
            case 1:
                yaw = 90.0F;
                break;
            case 2:
                yaw = -180.0F;
                break;
            case 3:
                yaw = -90.0F;
        }

        BlockPos northPos = this.traceToBlock(partialTicks, yaw);
        Block north = this.getBlock(northPos);
        int damage;
        if (north != null && north != Blocks.field_150350_a) {
            damage = this.getBlockDamage(northPos);
            if (damage != 0) {
                RenderUtil.drawRect((float) (((Integer) this.holeX.getValue()).intValue() + 16), (float) ((Integer) this.holeY.getValue()).intValue(), (float) (((Integer) this.holeX.getValue()).intValue() + 32), (float) (((Integer) this.holeY.getValue()).intValue() + 16), 1627324416);
            }

            this.drawBlock(north, (float) (((Integer) this.holeX.getValue()).intValue() + 16), (float) ((Integer) this.holeY.getValue()).intValue());
        }

        BlockPos southPos;
        Block south;
        if ((south = this.getBlock(southPos = this.traceToBlock(partialTicks, yaw - 180.0F))) != null && south != Blocks.field_150350_a) {
            damage = this.getBlockDamage(southPos);
            if (damage != 0) {
                RenderUtil.drawRect((float) (((Integer) this.holeX.getValue()).intValue() + 16), (float) (((Integer) this.holeY.getValue()).intValue() + 32), (float) (((Integer) this.holeX.getValue()).intValue() + 32), (float) (((Integer) this.holeY.getValue()).intValue() + 48), 1627324416);
            }

            this.drawBlock(south, (float) (((Integer) this.holeX.getValue()).intValue() + 16), (float) (((Integer) this.holeY.getValue()).intValue() + 32));
        }

        BlockPos eastPos;
        Block east;
        if ((east = this.getBlock(eastPos = this.traceToBlock(partialTicks, yaw + 90.0F))) != null && east != Blocks.field_150350_a) {
            damage = this.getBlockDamage(eastPos);
            if (damage != 0) {
                RenderUtil.drawRect((float) (((Integer) this.holeX.getValue()).intValue() + 32), (float) (((Integer) this.holeY.getValue()).intValue() + 16), (float) (((Integer) this.holeX.getValue()).intValue() + 48), (float) (((Integer) this.holeY.getValue()).intValue() + 32), 1627324416);
            }

            this.drawBlock(east, (float) (((Integer) this.holeX.getValue()).intValue() + 32), (float) (((Integer) this.holeY.getValue()).intValue() + 16));
        }

        BlockPos westPos;
        Block west;
        if ((west = this.getBlock(westPos = this.traceToBlock(partialTicks, yaw - 90.0F))) != null && west != Blocks.field_150350_a) {
            damage = this.getBlockDamage(westPos);
            if (damage != 0) {
                RenderUtil.drawRect((float) ((Integer) this.holeX.getValue()).intValue(), (float) (((Integer) this.holeY.getValue()).intValue() + 16), (float) (((Integer) this.holeX.getValue()).intValue() + 16), (float) (((Integer) this.holeY.getValue()).intValue() + 32), 1627324416);
            }

            this.drawBlock(west, (float) ((Integer) this.holeX.getValue()).intValue(), (float) (((Integer) this.holeY.getValue()).intValue() + 16));
        }

    }

    public void drawOverlay(float partialTicks, Entity player, int x, int y) {
        float yaw = 0.0F;
        int dir = MathHelper.func_76128_c((double) (player.field_70177_z * 4.0F / 360.0F) + 0.5D) & 3;
        switch (dir) {
            case 1:
                yaw = 90.0F;
                break;
            case 2:
                yaw = -180.0F;
                break;
            case 3:
                yaw = -90.0F;
        }

        BlockPos northPos = this.traceToBlock(partialTicks, yaw, player);
        Block north = this.getBlock(northPos);
        int damage;
        if (north != null && north != Blocks.field_150350_a) {
            damage = this.getBlockDamage(northPos);
            if (damage != 0) {
                RenderUtil.drawRect((float) (x + 16), (float) y, (float) (x + 32), (float) (y + 16), 1627324416);
            }

            this.drawBlock(north, (float) (x + 16), (float) y);
        }

        BlockPos southPos;
        Block south;
        if ((south = this.getBlock(southPos = this.traceToBlock(partialTicks, yaw - 180.0F, player))) != null && south != Blocks.field_150350_a) {
            damage = this.getBlockDamage(southPos);
            if (damage != 0) {
                RenderUtil.drawRect((float) (x + 16), (float) (y + 32), (float) (x + 32), (float) (y + 48), 1627324416);
            }

            this.drawBlock(south, (float) (x + 16), (float) (y + 32));
        }

        BlockPos eastPos;
        Block east;
        if ((east = this.getBlock(eastPos = this.traceToBlock(partialTicks, yaw + 90.0F, player))) != null && east != Blocks.field_150350_a) {
            damage = this.getBlockDamage(eastPos);
            if (damage != 0) {
                RenderUtil.drawRect((float) (x + 32), (float) (y + 16), (float) (x + 48), (float) (y + 32), 1627324416);
            }

            this.drawBlock(east, (float) (x + 32), (float) (y + 16));
        }

        BlockPos westPos;
        Block west;
        if ((west = this.getBlock(westPos = this.traceToBlock(partialTicks, yaw - 90.0F, player))) != null && west != Blocks.field_150350_a) {
            damage = this.getBlockDamage(westPos);
            if (damage != 0) {
                RenderUtil.drawRect((float) x, (float) (y + 16), (float) (x + 16), (float) (y + 32), 1627324416);
            }

            this.drawBlock(west, (float) x, (float) (y + 16));
        }

    }

    private int getBlockDamage(BlockPos pos) {
        Iterator var2 = mc.field_71438_f.field_72738_E.values().iterator();

        DestroyBlockProgress destBlockProgress;
        do {
            if (!var2.hasNext()) {
                return 0;
            }

            destBlockProgress = (DestroyBlockProgress) var2.next();
        } while (destBlockProgress.func_180246_b().func_177958_n() != pos.func_177958_n() || destBlockProgress.func_180246_b().func_177956_o() != pos.func_177956_o() || destBlockProgress.func_180246_b().func_177952_p() != pos.func_177952_p());

        return destBlockProgress.func_73106_e();
    }

    private BlockPos traceToBlock(float partialTicks, float yaw) {
        Vec3d pos = EntityUtil.interpolateEntity(mc.field_71439_g, partialTicks);
        Vec3d dir = MathUtil.direction(yaw);
        return new BlockPos(pos.field_72450_a + dir.field_72450_a, pos.field_72448_b, pos.field_72449_c + dir.field_72449_c);
    }

    private BlockPos traceToBlock(float partialTicks, float yaw, Entity player) {
        Vec3d pos = EntityUtil.interpolateEntity(player, partialTicks);
        Vec3d dir = MathUtil.direction(yaw);
        return new BlockPos(pos.field_72450_a + dir.field_72450_a, pos.field_72448_b, pos.field_72449_c + dir.field_72449_c);
    }

    private Block getBlock(BlockPos pos) {
        Block block = mc.field_71441_e.func_180495_p(pos).func_177230_c();
        return block != Blocks.field_150357_h && block != Blocks.field_150343_Z ? Blocks.field_150350_a : block;
    }

    private void drawBlock(Block block, float x, float y) {
        ItemStack stack = new ItemStack(block);
        GlStateManager.func_179094_E();
        GlStateManager.func_179147_l();
        GlStateManager.func_179120_a(770, 771, 1, 0);
        RenderHelper.func_74520_c();
        GlStateManager.func_179109_b(x, y, 0.0F);
        mc.func_175599_af().field_77023_b = 501.0F;
        mc.func_175599_af().func_180450_b(stack, 0, 0);
        mc.func_175599_af().field_77023_b = 0.0F;
        RenderHelper.func_74518_a();
        GlStateManager.func_179084_k();
        GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.func_179121_F();
    }

    public void renderInventory() {
        this.boxrender(((Integer) this.invX.getValue()).intValue() + ((Integer) this.fineinvX.getValue()).intValue(), ((Integer) this.invY.getValue()).intValue() + ((Integer) this.fineinvY.getValue()).intValue());
        this.itemrender(mc.field_71439_g.field_71071_by.field_70462_a, ((Integer) this.invX.getValue()).intValue() + ((Integer) this.fineinvX.getValue()).intValue(), ((Integer) this.invY.getValue()).intValue() + ((Integer) this.fineinvY.getValue()).intValue());
    }

    private void boxrender(int x, int y) {
        preboxrender();
        mc.field_71446_o.func_110577_a(box);
        RenderUtil.drawTexturedRect(x, y, 0, 0, 176, 16, 500);
        RenderUtil.drawTexturedRect(x, y + 16, 0, 16, 176, 54 + ((Integer) this.invH.getValue()).intValue(), 500);
        RenderUtil.drawTexturedRect(x, y + 16 + 54, 0, 160, 176, 8, 500);
        postboxrender();
    }

    private void itemrender(NonNullList items, int x, int y) {
        int iX;
        int i;
        for (i = 0; i < items.size() - 9; ++i) {
            iX = x + i % 9 * 18 + 8;
            int iY = y + i / 9 * 18 + 18;
            ItemStack itemStack = (ItemStack) items.get(i + 9);
            preitemrender();
            mc.func_175599_af().field_77023_b = 501.0F;
            RenderUtil.itemRender.func_180450_b(itemStack, iX, iY);
            RenderUtil.itemRender.func_180453_a(mc.field_71466_p, itemStack, iX, iY, (String) null);
            mc.func_175599_af().field_77023_b = 0.0F;
            postitemrender();
        }

        if (((Boolean) this.renderXCarry.getValue()).booleanValue()) {
            for (i = 1; i < 5; ++i) {
                iX = x + (i + 4) % 9 * 18 + 8;
                ItemStack itemStack = ((Slot) mc.field_71439_g.field_71069_bz.field_75151_b.get(i)).func_75211_c();
                if (itemStack != null && !itemStack.field_190928_g) {
                    preitemrender();
                    mc.func_175599_af().field_77023_b = 501.0F;
                    RenderUtil.itemRender.func_180450_b(itemStack, iX, y + 1);
                    RenderUtil.itemRender.func_180453_a(mc.field_71466_p, itemStack, iX, y + 1, (String) null);
                    mc.func_175599_af().field_77023_b = 0.0F;
                    postitemrender();
                }
            }
        }

    }

    private static enum Direction {
        N,
        W,
        S,
        E;
    }

    public static enum Compass {
        NONE,
        CIRCLE,
        LINE;
    }

    public static enum TargetHudDesign {
        NORMAL,
        COMPACT;
    }
}
