package me.dev.legacy.modules.render;

import me.dev.legacy.Legacy;
import me.dev.legacy.api.event.events.render.Render3DEvent;
import me.dev.legacy.api.util.ColorHolder;
import me.dev.legacy.api.util.DamageUtil;
import me.dev.legacy.api.util.EntityUtil;
import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.modules.Module;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Iterator;
import java.util.Objects;

public class Nametags extends Module {
    private static Nametags INSTANCE = new Nametags();
    private final Setting rect = this.register(new Setting("Rectangle", true));
    private final Setting armor = this.register(new Setting("Armor", true));
    private final Setting reversed = this.register(new Setting("ArmorReversed", false, (v) -> {
        return ((Boolean) this.armor.getValue()).booleanValue();
    }));
    private final Setting health = this.register(new Setting("Health", true));
    private final Setting ping = this.register(new Setting("Ping", true));
    private final Setting gamemode = this.register(new Setting("Gamemode", false));
    private final Setting entityID = this.register(new Setting("EntityID", false));
    private final Setting heldStackName = this.register(new Setting("StackName", true));
    private final Setting max = this.register(new Setting("Max", true));
    private final Setting maxText = this.register(new Setting("NoMaxText", false, (v) -> {
        return ((Boolean) this.max.getValue()).booleanValue();
    }));
    private final Setting Mred = this.register(new Setting("Max-Red", Integer.valueOf(178), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.max.getValue()).booleanValue();
    }));
    private final Setting Mgreen = this.register(new Setting("Max-Green", Integer.valueOf(52), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.max.getValue()).booleanValue();
    }));
    private final Setting Mblue = this.register(new Setting("Max-Blue", Integer.valueOf(57), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.max.getValue()).booleanValue();
    }));
    private final Setting size = this.register(new Setting("Size", 0.3F, 0.1F, 20.0F));
    private final Setting scaleing = this.register(new Setting("Scale", false));
    private final Setting smartScale = this.register(new Setting("SmartScale", false, (v) -> {
        return ((Boolean) this.scaleing.getValue()).booleanValue();
    }));
    private final Setting factor = this.register(new Setting("Factor", 0.3F, 0.1F, 1.0F, (v) -> {
        return ((Boolean) this.scaleing.getValue()).booleanValue();
    }));
    private final Setting textcolor = this.register(new Setting("TextColor", true));
    private final Setting NCRainbow = this.register(new Setting("Text-Rainbow", false, (v) -> {
        return ((Boolean) this.textcolor.getValue()).booleanValue();
    }));
    private final Setting NCred = this.register(new Setting("Text-Red", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.textcolor.getValue()).booleanValue();
    }));
    private final Setting NCgreen = this.register(new Setting("Text-Green", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.textcolor.getValue()).booleanValue();
    }));
    private final Setting NCblue = this.register(new Setting("Text-Blue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.textcolor.getValue()).booleanValue();
    }));
    private final Setting outline = this.register(new Setting("Outline", true));
    private final Setting ORainbow = this.register(new Setting("Outline-Rainbow", false, (v) -> {
        return ((Boolean) this.outline.getValue()).booleanValue();
    }));
    private final Setting Owidth = this.register(new Setting("Outline-Width", 1.3F, 0.0F, 5.0F, (v) -> {
        return ((Boolean) this.outline.getValue()).booleanValue();
    }));
    private final Setting Ored = this.register(new Setting("Outline-Red", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.outline.getValue()).booleanValue();
    }));
    private final Setting Ogreen = this.register(new Setting("Outline-Green", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.outline.getValue()).booleanValue();
    }));
    private final Setting Oblue = this.register(new Setting("Outline-Blue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.outline.getValue()).booleanValue();
    }));
    private final Setting friendcolor = this.register(new Setting("FriendColor", true));
    private final Setting FCRainbow = this.register(new Setting("Friend-Rainbow", false, (v) -> {
        return ((Boolean) this.friendcolor.getValue()).booleanValue();
    }));
    private final Setting FCred = this.register(new Setting("Friend-Red", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.friendcolor.getValue()).booleanValue();
    }));
    private final Setting FCgreen = this.register(new Setting("Friend-Green", Integer.valueOf(213), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.friendcolor.getValue()).booleanValue();
    }));
    private final Setting FCblue = this.register(new Setting("Friend-Blue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.friendcolor.getValue()).booleanValue();
    }));
    private final Setting FORainbow = this.register(new Setting("FriendOutline-Rainbow", false, (v) -> {
        return ((Boolean) this.outline.getValue()).booleanValue() && ((Boolean) this.friendcolor.getValue()).booleanValue();
    }));
    private final Setting FOred = this.register(new Setting("FriendOutline-Red", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.outline.getValue()).booleanValue() && ((Boolean) this.friendcolor.getValue()).booleanValue();
    }));
    private final Setting FOgreen = this.register(new Setting("FriendOutline-Green", Integer.valueOf(213), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.outline.getValue()).booleanValue() && ((Boolean) this.friendcolor.getValue()).booleanValue();
    }));
    private final Setting FOblue = this.register(new Setting("FriendOutline-Blue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.outline.getValue()).booleanValue() && ((Boolean) this.friendcolor.getValue()).booleanValue();
    }));
    private final Setting sneakcolor = this.register(new Setting("Sneak", false));
    private final Setting sneak = this.register(new Setting("EnableSneak", true, (v) -> {
        return ((Boolean) this.sneakcolor.getValue()).booleanValue();
    }));
    private final Setting SCRainbow = this.register(new Setting("Sneak-Rainbow", false, (v) -> {
        return ((Boolean) this.sneakcolor.getValue()).booleanValue();
    }));
    private final Setting SCred = this.register(new Setting("Sneak-Red", Integer.valueOf(245), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.sneakcolor.getValue()).booleanValue();
    }));
    private final Setting SCgreen = this.register(new Setting("Sneak-Green", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.sneakcolor.getValue()).booleanValue();
    }));
    private final Setting SCblue = this.register(new Setting("Sneak-Blue", Integer.valueOf(122), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.sneakcolor.getValue()).booleanValue();
    }));
    private final Setting SORainbow = this.register(new Setting("SneakOutline-Rainbow", false, (v) -> {
        return ((Boolean) this.outline.getValue()).booleanValue() && ((Boolean) this.sneakcolor.getValue()).booleanValue();
    }));
    private final Setting SOred = this.register(new Setting("SneakOutline-Red", Integer.valueOf(245), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.outline.getValue()).booleanValue() && ((Boolean) this.sneakcolor.getValue()).booleanValue();
    }));
    private final Setting SOgreen = this.register(new Setting("SneakOutline-Green", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.outline.getValue()).booleanValue() && ((Boolean) this.sneakcolor.getValue()).booleanValue();
    }));
    private final Setting SOblue = this.register(new Setting("SneakOutline-Blue", Integer.valueOf(122), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.outline.getValue()).booleanValue() && ((Boolean) this.sneakcolor.getValue()).booleanValue();
    }));
    private final Setting invisiblescolor = this.register(new Setting("InvisiblesColor", false));
    private final Setting invisibles = this.register(new Setting("EnableInvisibles", true, (v) -> {
        return ((Boolean) this.invisiblescolor.getValue()).booleanValue();
    }));
    private final Setting ICRainbow = this.register(new Setting("Invisible-Rainbow", false, (v) -> {
        return ((Boolean) this.invisiblescolor.getValue()).booleanValue();
    }));
    private final Setting ICred = this.register(new Setting("Invisible-Red", Integer.valueOf(148), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.invisiblescolor.getValue()).booleanValue();
    }));
    private final Setting ICgreen = this.register(new Setting("Invisible-Green", Integer.valueOf(148), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.invisiblescolor.getValue()).booleanValue();
    }));
    private final Setting ICblue = this.register(new Setting("Invisible-Blue", Integer.valueOf(148), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.invisiblescolor.getValue()).booleanValue();
    }));
    private final Setting IORainbow = this.register(new Setting("InvisibleOutline-Rainbow", false, (v) -> {
        return ((Boolean) this.outline.getValue()).booleanValue() && ((Boolean) this.invisiblescolor.getValue()).booleanValue();
    }));
    private final Setting IOred = this.register(new Setting("InvisibleOutline-Red", Integer.valueOf(148), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.outline.getValue()).booleanValue() && ((Boolean) this.invisiblescolor.getValue()).booleanValue();
    }));
    private final Setting IOgreen = this.register(new Setting("InvisibleOutline-Green", Integer.valueOf(148), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.outline.getValue()).booleanValue() && ((Boolean) this.invisiblescolor.getValue()).booleanValue();
    }));
    private final Setting IOblue = this.register(new Setting("InvisibleOutline-Blue", Integer.valueOf(148), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.outline.getValue()).booleanValue() && ((Boolean) this.invisiblescolor.getValue()).booleanValue();
    }));

    public Nametags() {
        super("Nametags", "Renders info about the player on a NameTag", Module.Category.RENDER, false, false, false);
    }

    public static Nametags getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Nametags();
        }

        return INSTANCE;
    }

    public void onRender3D(Render3DEvent event) {
        Iterator var2 = mc.field_71441_e.field_73010_i.iterator();

        while (true) {
            EntityPlayer player;
            do {
                do {
                    do {
                        do {
                            if (!var2.hasNext()) {
                                return;
                            }

                            player = (EntityPlayer) var2.next();
                        } while (player == null);
                    } while (player.equals(mc.field_71439_g));
                } while (!player.func_70089_S());
            } while (player.func_82150_aj() && !((Boolean) this.invisibles.getValue()).booleanValue());

            double x = this.interpolate(player.field_70142_S, player.field_70165_t, event.getPartialTicks()) - mc.func_175598_ae().field_78725_b;
            double y = this.interpolate(player.field_70137_T, player.field_70163_u, event.getPartialTicks()) - mc.func_175598_ae().field_78726_c;
            double z = this.interpolate(player.field_70136_U, player.field_70161_v, event.getPartialTicks()) - mc.func_175598_ae().field_78723_d;
            this.renderNameTag(player, x, y, z, event.getPartialTicks());
        }
    }

    private void renderNameTag(EntityPlayer player, double x, double y, double z, float delta) {
        double tempY = y + (player.func_70093_af() ? 0.5D : 0.7D);
        Entity camera = mc.func_175606_aa();

        assert camera != null;

        double originalPositionX = camera.field_70165_t;
        double originalPositionY = camera.field_70163_u;
        double originalPositionZ = camera.field_70161_v;
        camera.field_70165_t = this.interpolate(camera.field_70169_q, camera.field_70165_t, delta);
        camera.field_70163_u = this.interpolate(camera.field_70167_r, camera.field_70163_u, delta);
        camera.field_70161_v = this.interpolate(camera.field_70166_s, camera.field_70161_v, delta);
        String displayTag = this.getDisplayTag(player);
        double distance = camera.func_70011_f(x + mc.func_175598_ae().field_78730_l, y + mc.func_175598_ae().field_78731_m, z + mc.func_175598_ae().field_78728_n);
        int width = this.renderer.getStringWidth(displayTag) / 2;
        double scale = (0.0018D + (double) ((Float) this.size.getValue()).floatValue() * distance * (double) ((Float) this.factor.getValue()).floatValue()) / 1000.0D;
        if (distance <= 8.0D && ((Boolean) this.smartScale.getValue()).booleanValue()) {
            scale = 0.0245D;
        }

        if (!((Boolean) this.scaleing.getValue()).booleanValue()) {
            scale = (double) ((Float) this.size.getValue()).floatValue() / 100.0D;
        }

        GlStateManager.func_179094_E();
        RenderHelper.func_74519_b();
        GlStateManager.func_179088_q();
        GlStateManager.func_179136_a(1.0F, -1500000.0F);
        GlStateManager.func_179140_f();
        GlStateManager.func_179109_b((float) x, (float) tempY + 1.4F, (float) z);
        GlStateManager.func_179114_b(-mc.func_175598_ae().field_78735_i, 0.0F, 1.0F, 0.0F);
        float var10001 = mc.field_71474_y.field_74320_O == 2 ? -1.0F : 1.0F;
        GlStateManager.func_179114_b(mc.func_175598_ae().field_78732_j, var10001, 0.0F, 0.0F);
        GlStateManager.func_179139_a(-scale, -scale, scale);
        GlStateManager.func_179097_i();
        GlStateManager.func_179147_l();
        GlStateManager.func_179147_l();
        if (((Boolean) this.rect.getValue()).booleanValue()) {
            this.drawRect((float) (-width - 2), (float) (-(mc.field_71466_p.field_78288_b + 1)), (float) width + 2.0F, 1.5F, 1426063360);
        }

        if (((Boolean) this.outline.getValue()).booleanValue()) {
            this.drawOutlineRect((float) (-width - 2), (float) (-(mc.field_71466_p.field_78288_b + 1)), (float) width + 2.0F, 1.5F, this.getOutlineColor(player));
        }

        GlStateManager.func_179084_k();
        ItemStack renderMainHand = player.func_184614_ca().func_77946_l();
        if (renderMainHand.func_77962_s() && (renderMainHand.func_77973_b() instanceof ItemTool || renderMainHand.func_77973_b() instanceof ItemArmor)) {
            renderMainHand.field_77994_a = 1;
        }

        int count;
        if (((Boolean) this.heldStackName.getValue()).booleanValue() && !renderMainHand.field_190928_g && renderMainHand.func_77973_b() != Items.field_190931_a) {
            String stackName = renderMainHand.func_82833_r();
            count = this.renderer.getStringWidth(stackName) / 2;
            GL11.glPushMatrix();
            GL11.glScalef(0.75F, 0.75F, 0.0F);
            this.renderer.drawStringWithShadow(stackName, (float) (-count), -(this.getBiggestArmorTag(player) + 20.0F), -1);
            GL11.glScalef(1.5F, 1.5F, 1.0F);
            GL11.glPopMatrix();
        }

        if (((Boolean) this.armor.getValue()).booleanValue()) {
            GlStateManager.func_179094_E();
            int xOffset = -6;
            count = 0;
            Iterator var27 = player.field_71071_by.field_70460_b.iterator();

            while (var27.hasNext()) {
                ItemStack armourStack = (ItemStack) var27.next();
                if (armourStack != null) {
                    xOffset -= 8;
                    if (armourStack.func_77973_b() != Items.field_190931_a) {
                        ++count;
                    }
                }
            }

            xOffset -= 8;
            ItemStack renderOffhand = player.func_184592_cb().func_77946_l();
            if (renderOffhand.func_77962_s() && (renderOffhand.func_77973_b() instanceof ItemTool || renderOffhand.func_77973_b() instanceof ItemArmor)) {
                renderOffhand.field_77994_a = 1;
            }

            this.renderItemStack(renderOffhand, xOffset, -26);
            xOffset += 16;
            ItemStack armourStack;
            ItemStack renderStack1;
            int index;
            if (((Boolean) this.reversed.getValue()).booleanValue()) {
                for (index = 0; index <= 3; ++index) {
                    armourStack = (ItemStack) player.field_71071_by.field_70460_b.get(index);
                    if (armourStack != null && armourStack.func_77973_b() != Items.field_190931_a) {
                        renderStack1 = armourStack.func_77946_l();
                        this.renderItemStack(armourStack, xOffset, -26);
                        xOffset += 16;
                    }
                }
            } else {
                for (index = 3; index >= 0; --index) {
                    armourStack = (ItemStack) player.field_71071_by.field_70460_b.get(index);
                    if (armourStack != null && armourStack.func_77973_b() != Items.field_190931_a) {
                        renderStack1 = armourStack.func_77946_l();
                        this.renderItemStack(armourStack, xOffset, -26);
                        xOffset += 16;
                    }
                }
            }

            this.renderItemStack(renderMainHand, xOffset, -26);
            GlStateManager.func_179121_F();
        }

        this.renderer.drawStringWithShadow(displayTag, (float) (-width), (float) (-(this.renderer.getFontHeight() - 1)), this.getDisplayColor(player));
        camera.field_70165_t = originalPositionX;
        camera.field_70163_u = originalPositionY;
        camera.field_70161_v = originalPositionZ;
        GlStateManager.func_179126_j();
        GlStateManager.func_179084_k();
        GlStateManager.func_179113_r();
        GlStateManager.func_179136_a(1.0F, 1500000.0F);
        GlStateManager.func_179121_F();
    }

    private int getDisplayColor(EntityPlayer player) {
        int displaycolor = ColorHolder.toHex(((Integer) this.NCred.getValue()).intValue(), ((Integer) this.NCgreen.getValue()).intValue(), ((Integer) this.NCblue.getValue()).intValue());
        if (Legacy.friendManager.isFriend(player)) {
            return ColorHolder.toHex(((Integer) this.FCred.getValue()).intValue(), ((Integer) this.FCgreen.getValue()).intValue(), ((Integer) this.FCblue.getValue()).intValue());
        } else {
            if (player.func_82150_aj() && ((Boolean) this.invisibles.getValue()).booleanValue()) {
                displaycolor = ColorHolder.toHex(((Integer) this.ICred.getValue()).intValue(), ((Integer) this.ICgreen.getValue()).intValue(), ((Integer) this.ICblue.getValue()).intValue());
            } else if (player.func_70093_af() && ((Boolean) this.sneak.getValue()).booleanValue()) {
                displaycolor = ColorHolder.toHex(((Integer) this.SCred.getValue()).intValue(), ((Integer) this.SCgreen.getValue()).intValue(), ((Integer) this.SCblue.getValue()).intValue());
            }

            return displaycolor;
        }
    }

    private int getOutlineColor(EntityPlayer player) {
        int outlinecolor = ColorHolder.toHex(((Integer) this.Ored.getValue()).intValue(), ((Integer) this.Ogreen.getValue()).intValue(), ((Integer) this.Oblue.getValue()).intValue());
        if (Legacy.friendManager.isFriend(player)) {
            outlinecolor = ColorHolder.toHex(((Integer) this.FOred.getValue()).intValue(), ((Integer) this.FOgreen.getValue()).intValue(), ((Integer) this.FOblue.getValue()).intValue());
        } else if (player.func_82150_aj() && ((Boolean) this.invisibles.getValue()).booleanValue()) {
            outlinecolor = ColorHolder.toHex(((Integer) this.IOred.getValue()).intValue(), ((Integer) this.IOgreen.getValue()).intValue(), ((Integer) this.IOblue.getValue()).intValue());
        } else if (player.func_70093_af() && ((Boolean) this.sneak.getValue()).booleanValue()) {
            outlinecolor = ColorHolder.toHex(((Integer) this.SOred.getValue()).intValue(), ((Integer) this.SOgreen.getValue()).intValue(), ((Integer) this.SOblue.getValue()).intValue());
        }

        return outlinecolor;
    }

    private void renderItemStack(ItemStack stack, int x, int y) {
        GlStateManager.func_179094_E();
        GlStateManager.func_179132_a(true);
        GlStateManager.func_179086_m(256);
        RenderHelper.func_74519_b();
        mc.func_175599_af().field_77023_b = -150.0F;
        GlStateManager.func_179118_c();
        GlStateManager.func_179126_j();
        GlStateManager.func_179129_p();
        mc.func_175599_af().func_180450_b(stack, x, y);
        mc.func_175599_af().func_175030_a(mc.field_71466_p, stack, x, y);
        mc.func_175599_af().field_77023_b = 0.0F;
        RenderHelper.func_74518_a();
        GlStateManager.func_179089_o();
        GlStateManager.func_179141_d();
        GlStateManager.func_179152_a(0.5F, 0.5F, 0.5F);
        GlStateManager.func_179097_i();
        this.renderEnchantmentText(stack, x, y);
        GlStateManager.func_179126_j();
        GlStateManager.func_179152_a(2.0F, 2.0F, 2.0F);
        GlStateManager.func_179121_F();
    }

    private void renderEnchantmentText(ItemStack stack, int x, int y) {
        int enchantmentY = y - 8;
        if (stack.func_77973_b() == Items.field_151153_ao && stack.func_77962_s()) {
            this.renderer.drawStringWithShadow("god", (float) (x * 2), (float) enchantmentY, -3977919);
            enchantmentY -= 8;
        }

        NBTTagList enchants = stack.func_77986_q();
        int percent;
        if (enchants.func_74745_c() > 2 && ((Boolean) this.max.getValue()).booleanValue()) {
            if (((Boolean) this.maxText.getValue()).booleanValue()) {
                this.renderer.drawStringWithShadow("", (float) (x * 2), (float) enchantmentY, ColorHolder.toHex(((Integer) this.Mred.getValue()).intValue(), ((Integer) this.Mgreen.getValue()).intValue(), ((Integer) this.Mblue.getValue()).intValue()));
                enchantmentY -= 8;
            } else {
                this.renderer.drawStringWithShadow("max", (float) (x * 2), (float) enchantmentY, ColorHolder.toHex(((Integer) this.Mred.getValue()).intValue(), ((Integer) this.Mgreen.getValue()).intValue(), ((Integer) this.Mblue.getValue()).intValue()));
                enchantmentY -= 8;
            }
        } else {
            for (percent = 0; percent < enchants.func_74745_c(); ++percent) {
                short id = enchants.func_150305_b(percent).func_74765_d("id");
                short level = enchants.func_150305_b(percent).func_74765_d("lvl");
                Enchantment enc = Enchantment.func_185262_c(id);
                if (enc != null) {
                    String encName = enc.func_190936_d() ? TextFormatting.RED + enc.func_77316_c(level).substring(11).substring(0, 1).toLowerCase() : enc.func_77316_c(level).substring(0, 1).toLowerCase();
                    encName = encName + level;
                    this.renderer.drawStringWithShadow(encName, (float) (x * 2), (float) enchantmentY, -1);
                    enchantmentY -= 8;
                }
            }
        }

        if (DamageUtil.hasDurability(stack)) {
            percent = DamageUtil.getRoundedDamage(stack);
            String color;
            if (percent >= 60) {
                color = "§a";
            } else if (percent >= 25) {
                color = "§e";
            } else {
                color = "§c";
            }

            this.renderer.drawStringWithShadow(color + percent + "%", (float) (x * 2), (float) enchantmentY, -1);
        }

    }

    private float getBiggestArmorTag(EntityPlayer player) {
        float enchantmentY = 0.0F;
        boolean arm = false;
        Iterator var4 = player.field_71071_by.field_70460_b.iterator();

        ItemStack renderOffHand;
        float encY;
        NBTTagList enchants;
        int index;
        short id;
        Enchantment enc;
        while (var4.hasNext()) {
            renderOffHand = (ItemStack) var4.next();
            encY = 0.0F;
            if (renderOffHand != null) {
                enchants = renderOffHand.func_77986_q();

                for (index = 0; index < enchants.func_74745_c(); ++index) {
                    id = enchants.func_150305_b(index).func_74765_d("id");
                    enc = Enchantment.func_185262_c(id);
                    if (enc != null) {
                        encY += 8.0F;
                        arm = true;
                    }
                }
            }

            if (encY > enchantmentY) {
                enchantmentY = encY;
            }
        }

        ItemStack renderMainHand = player.func_184614_ca().func_77946_l();
        if (renderMainHand.func_77962_s()) {
            float encY = 0.0F;
            NBTTagList enchants = renderMainHand.func_77986_q();

            for (int index = 0; index < enchants.func_74745_c(); ++index) {
                short id = enchants.func_150305_b(index).func_74765_d("id");
                Enchantment enc = Enchantment.func_185262_c(id);
                if (enc != null) {
                    encY += 8.0F;
                    arm = true;
                }
            }

            if (encY > enchantmentY) {
                enchantmentY = encY;
            }
        }

        renderOffHand = player.func_184592_cb().func_77946_l();
        if (renderOffHand.func_77962_s()) {
            encY = 0.0F;
            enchants = renderOffHand.func_77986_q();

            for (index = 0; index < enchants.func_74745_c(); ++index) {
                id = enchants.func_150305_b(index).func_74765_d("id");
                enc = Enchantment.func_185262_c(id);
                if (enc != null) {
                    encY += 8.0F;
                    arm = true;
                }
            }

            if (encY > enchantmentY) {
                enchantmentY = encY;
            }
        }

        return (float) (arm ? 0 : 20) + enchantmentY;
    }

    private String getDisplayTag(EntityPlayer player) {
        String name = player.func_145748_c_().func_150254_d();
        if (name.contains(mc.func_110432_I().func_111285_a())) {
            name = "You";
        }

        if (!((Boolean) this.health.getValue()).booleanValue()) {
            return name;
        } else {
            float health = EntityUtil.getHealth(player);
            String color;
            if (health > 18.0F) {
                color = "§a";
            } else if (health > 16.0F) {
                color = "§2";
            } else if (health > 12.0F) {
                color = "§e";
            } else if (health > 8.0F) {
                color = "§c";
            } else if (health > 5.0F) {
                color = "§4";
            } else {
                color = "§4";
            }

            String pingStr = "";
            if (((Boolean) this.ping.getValue()).booleanValue()) {
                try {
                    int responseTime = ((NetHandlerPlayClient) Objects.requireNonNull(mc.func_147114_u())).func_175102_a(player.func_110124_au()).func_178853_c();
                    pingStr = pingStr + responseTime + "ms ";
                } catch (Exception var8) {
                    ;
                }
            }

            String idString = "";
            if (((Boolean) this.entityID.getValue()).booleanValue()) {
                idString = idString + "ID: " + player.func_145782_y() + " ";
            }

            String gameModeStr = "";
            if (((Boolean) this.gamemode.getValue()).booleanValue()) {
                if (player.func_184812_l_()) {
                    gameModeStr = gameModeStr + "[C] ";
                } else if (!player.func_175149_v() && !player.func_82150_aj()) {
                    gameModeStr = gameModeStr + "[S] ";
                } else {
                    gameModeStr = gameModeStr + "[I] ";
                }
            }

            if (Math.floor((double) health) == (double) health) {
                name = name + color + " " + (health > 0.0F ? (int) Math.floor((double) health) : "dead");
            } else {
                name = name + color + " " + (health > 0.0F ? (int) health : "dead");
            }

            return " " + pingStr + idString + gameModeStr + name + " ";
        }
    }

    private double interpolate(double previous, double current, float delta) {
        return previous + (current - previous) * (double) delta;
    }

    public void drawOutlineRect(float x, float y, float w, float h, int color) {
        float alpha = (float) (color >> 24 & 255) / 255.0F;
        float red = (float) (color >> 16 & 255) / 255.0F;
        float green = (float) (color >> 8 & 255) / 255.0F;
        float blue = (float) (color & 255) / 255.0F;
        Tessellator tessellator = Tessellator.func_178181_a();
        BufferBuilder bufferbuilder = tessellator.func_178180_c();
        GlStateManager.func_179147_l();
        GlStateManager.func_179090_x();
        GlStateManager.func_187441_d(((Float) this.Owidth.getValue()).floatValue());
        GlStateManager.func_179120_a(770, 771, 1, 0);
        bufferbuilder.func_181668_a(2, DefaultVertexFormats.field_181706_f);
        bufferbuilder.func_181662_b((double) x, (double) h, 0.0D).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b((double) w, (double) h, 0.0D).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b((double) w, (double) y, 0.0D).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b((double) x, (double) y, 0.0D).func_181666_a(red, green, blue, alpha).func_181675_d();
        tessellator.func_78381_a();
        GlStateManager.func_179098_w();
        GlStateManager.func_179084_k();
    }

    public void drawRect(float x, float y, float w, float h, int color) {
        float alpha = (float) (color >> 24 & 255) / 255.0F;
        float red = (float) (color >> 16 & 255) / 255.0F;
        float green = (float) (color >> 8 & 255) / 255.0F;
        float blue = (float) (color & 255) / 255.0F;
        Tessellator tessellator = Tessellator.func_178181_a();
        BufferBuilder bufferbuilder = tessellator.func_178180_c();
        GlStateManager.func_179147_l();
        GlStateManager.func_179090_x();
        GlStateManager.func_187441_d(((Float) this.Owidth.getValue()).floatValue());
        GlStateManager.func_179120_a(770, 771, 1, 0);
        bufferbuilder.func_181668_a(7, DefaultVertexFormats.field_181706_f);
        bufferbuilder.func_181662_b((double) x, (double) h, 0.0D).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b((double) w, (double) h, 0.0D).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b((double) w, (double) y, 0.0D).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b((double) x, (double) y, 0.0D).func_181666_a(red, green, blue, alpha).func_181675_d();
        tessellator.func_78381_a();
        GlStateManager.func_179098_w();
        GlStateManager.func_179084_k();
    }

    public void onUpdate() {
        if (((Boolean) this.outline.getValue()).equals(false)) {
            this.rect.setValue(true);
        } else if (((Boolean) this.rect.getValue()).equals(false)) {
            this.outline.setValue(true);
        }

        if (((Boolean) this.ORainbow.getValue()).booleanValue()) {
            this.OutlineRainbow();
        }

        if (((Boolean) this.NCRainbow.getValue()).booleanValue()) {
            this.TextRainbow();
        }

        if (((Boolean) this.FCRainbow.getValue()).booleanValue()) {
            this.FriendRainbow();
        }

        if (((Boolean) this.SCRainbow.getValue()).booleanValue()) {
            this.SneakColorRainbow();
        }

        if (((Boolean) this.ICRainbow.getValue()).booleanValue()) {
            this.InvisibleRainbow();
        }

        if (((Boolean) this.FORainbow.getValue()).booleanValue()) {
            this.FriendOutlineRainbow();
        }

        if (((Boolean) this.IORainbow.getValue()).booleanValue()) {
            this.InvisibleOutlineRainbow();
        }

        if (((Boolean) this.SORainbow.getValue()).booleanValue()) {
            this.SneakOutlineRainbow();
        }

    }

    public void OutlineRainbow() {
        float[] tick_color = new float[]{(float) (System.currentTimeMillis() % 11520L) / 11520.0F};
        int color_rgb_o = Color.HSBtoRGB(tick_color[0], 0.8F, 0.8F);
        this.Ored.setValue(color_rgb_o >> 16 & 255);
        this.Ogreen.setValue(color_rgb_o >> 8 & 255);
        this.Oblue.setValue(color_rgb_o & 255);
    }

    public void TextRainbow() {
        float[] tick_color = new float[]{(float) (System.currentTimeMillis() % 11520L) / 11520.0F};
        int color_rgb_o = Color.HSBtoRGB(tick_color[0], 0.8F, 0.8F);
        this.NCred.setValue(color_rgb_o >> 16 & 255);
        this.NCgreen.setValue(color_rgb_o >> 8 & 255);
        this.NCblue.setValue(color_rgb_o & 255);
    }

    public void FriendRainbow() {
        float[] tick_color = new float[]{(float) (System.currentTimeMillis() % 11520L) / 11520.0F};
        int color_rgb_o = Color.HSBtoRGB(tick_color[0], 0.8F, 0.8F);
        this.FCred.setValue(color_rgb_o >> 16 & 255);
        this.FCgreen.setValue(color_rgb_o >> 8 & 255);
        this.FCblue.setValue(color_rgb_o & 255);
    }

    public void SneakColorRainbow() {
        float[] tick_color = new float[]{(float) (System.currentTimeMillis() % 11520L) / 11520.0F};
        int color_rgb_o = Color.HSBtoRGB(tick_color[0], 0.8F, 0.8F);
        this.SCred.setValue(color_rgb_o >> 16 & 255);
        this.SCgreen.setValue(color_rgb_o >> 8 & 255);
        this.SCblue.setValue(color_rgb_o & 255);
    }

    public void InvisibleRainbow() {
        float[] tick_color = new float[]{(float) (System.currentTimeMillis() % 11520L) / 11520.0F};
        int color_rgb_o = Color.HSBtoRGB(tick_color[0], 0.8F, 0.8F);
        this.ICred.setValue(color_rgb_o >> 16 & 255);
        this.ICgreen.setValue(color_rgb_o >> 8 & 255);
        this.ICblue.setValue(color_rgb_o & 255);
    }

    public void InvisibleOutlineRainbow() {
        float[] tick_color = new float[]{(float) (System.currentTimeMillis() % 11520L) / 11520.0F};
        int color_rgb_o = Color.HSBtoRGB(tick_color[0], 0.8F, 0.8F);
        this.IOred.setValue(color_rgb_o >> 16 & 255);
        this.IOgreen.setValue(color_rgb_o >> 8 & 255);
        this.IOblue.setValue(color_rgb_o & 255);
    }

    public void FriendOutlineRainbow() {
        float[] tick_color = new float[]{(float) (System.currentTimeMillis() % 11520L) / 11520.0F};
        int color_rgb_o = Color.HSBtoRGB(tick_color[0], 0.8F, 0.8F);
        this.FOred.setValue(color_rgb_o >> 16 & 255);
        this.FOgreen.setValue(color_rgb_o >> 8 & 255);
        this.FOblue.setValue(color_rgb_o & 255);
    }

    public void SneakOutlineRainbow() {
        float[] tick_color = new float[]{(float) (System.currentTimeMillis() % 11520L) / 11520.0F};
        int color_rgb_o = Color.HSBtoRGB(tick_color[0], 0.8F, 0.8F);
        this.SOred.setValue(color_rgb_o >> 16 & 255);
        this.SOgreen.setValue(color_rgb_o >> 8 & 255);
        this.SOblue.setValue(color_rgb_o & 255);
    }
}
