package me.dev.legacy.impl.gui.components;

import me.dev.legacy.Legacy;
import me.dev.legacy.api.AbstractModule;
import me.dev.legacy.api.util.ColorUtil;
import me.dev.legacy.api.util.RenderUtil;
import me.dev.legacy.impl.gui.LegacyGui;
import me.dev.legacy.impl.gui.components.items.Item;
import me.dev.legacy.impl.gui.components.items.buttons.Button;
import me.dev.legacy.modules.client.ClickGui;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.init.SoundEvents;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

public class Component extends AbstractModule {
    public static int[] counter1 = new int[]{1};
    private final ArrayList items = new ArrayList();
    public boolean drag;
    private int x;
    private int y;
    private int x2;
    private int y2;
    private int width;
    private int height;
    private final int barHeight;
    private boolean open;
    private boolean hidden = false;

    public Component(String name, int x, int y, boolean open) {
        super(name);
        this.x = x;
        this.y = y;
        this.width = 88;
        this.barHeight = 15;
        this.height = 18;
        this.open = open;
        this.setupItems();
    }

    public void setupItems() {
    }

    private void drag(int mouseX, int mouseY) {
        if (this.drag) {
            this.x = this.x2 + mouseX;
            this.y = this.y2 + mouseY;
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drag(mouseX, mouseY);
        counter1 = new int[]{1};
        float totalItemHeight = this.open ? this.getTotalItemHeight() - 2.0F : 0.0F;
        int color = ColorUtil.toARGB(((Integer) ClickGui.getInstance().endcolorred.getValue()).intValue(), ((Integer) ClickGui.getInstance().endcolorgreen.getValue()).intValue(), ((Integer) ClickGui.getInstance().endcolorblue.getValue()).intValue(), 255);
        int startcolor = ColorUtil.toARGB(((Integer) ClickGui.getInstance().startcolorred.getValue()).intValue(), ((Integer) ClickGui.getInstance().startcolorgreen.getValue()).intValue(), ((Integer) ClickGui.getInstance().startcolorblue.getValue()).intValue(), 255);
        Gui.func_73734_a(this.x, this.y - 1, this.x + this.width, this.y + this.height - 6, ((Boolean) ClickGui.getInstance().rainbow.getValue()).booleanValue() ? ColorUtil.rainbow(((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB() : color);
        if (this.open) {
            RenderUtil.drawRect((float) this.x, (float) this.y + 12.5F, (float) (this.x + this.width), (float) (this.y + this.height) + totalItemHeight, 1996488704);
        }

        RenderUtil.drawSidewaysGradientRect(this.x, this.y - 5, this.x + this.width + 20, this.y + this.height - 6, ((Boolean) ClickGui.getInstance().rainbow.getValue()).booleanValue() ? ColorUtil.rainbow(((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB() : color, startcolor);
        int color1 = ColorUtil.toARGB(((Integer) ClickGui.getInstance().testcolorr.getValue()).intValue(), ((Integer) ClickGui.getInstance().testcolorgreen.getValue()).intValue(), ((Integer) ClickGui.getInstance().testcolorblue.getValue()).intValue(), 255);
        int color2 = ColorUtil.toARGB(((Integer) ClickGui.getInstance().fontcolorr.getValue()).intValue(), ((Integer) ClickGui.getInstance().fontcolorg.getValue()).intValue(), ((Integer) ClickGui.getInstance().fontcolorb.getValue()).intValue(), 255);
        if (this.open) {
            RenderUtil.drawRect((float) this.x, (float) this.y + 12.5F, (float) (this.x + this.width + 20), (float) (this.y + this.height) + totalItemHeight, color1);
            if (((Boolean) ClickGui.getInstance().outline.getValue()).booleanValue()) {
                GlStateManager.func_179090_x();
                GlStateManager.func_179147_l();
                GlStateManager.func_179118_c();
                GlStateManager.func_187428_a(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
                GlStateManager.func_179103_j(7425);
                GL11.glBegin(2);
                Color outlineColor = ((Boolean) ClickGui.getInstance().rainbow.getValue()).booleanValue() ? Legacy.colorManager.getColor() : new Color(ColorUtil.toARGB(165, 23, 55, 255));
                GL11.glColor4f((float) outlineColor.getRed(), (float) outlineColor.getGreen(), (float) outlineColor.getBlue(), (float) outlineColor.getAlpha());
                GL11.glVertex3f((float) this.x, (float) this.y - 1.5F - 4.0F, 0.0F);
                GL11.glVertex3f((float) (this.x + this.width + 20), (float) this.y - 1.5F - 4.0F, 0.0F);
                GL11.glVertex3f((float) (this.x + this.width + 20), (float) (this.y + this.height) + totalItemHeight, 5.0F);
                GL11.glVertex3f((float) this.x, (float) (this.y + this.height) + totalItemHeight, 5.0F);
                GL11.glEnd();
                GlStateManager.func_179103_j(7424);
                GlStateManager.func_179084_k();
                GlStateManager.func_179141_d();
                GlStateManager.func_179098_w();
            }
        }

        Legacy.textManager.drawStringWithShadow(this.getName(), (float) this.x + 3.0F + 30.0F, (float) this.y - 4.0F - (float) LegacyGui.getClickGui().getTextOffset() - 2.0F, color2);
        if (this.open) {
            float y = (float) (this.getY() + this.getHeight()) - 3.0F;
            Iterator var10 = this.getItems().iterator();

            while (var10.hasNext()) {
                Item item = (Item) var10.next();
                ++counter1[0];
                if (!item.isHidden()) {
                    item.setLocation((float) this.x + 2.0F, y);
                    item.setWidth(this.getWidth() - 4);
                    item.drawScreen(mouseX, mouseY, partialTicks);
                    y += (float) item.getHeight() + 1.5F;
                }
            }
        }

    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0 && this.isHovering(mouseX, mouseY)) {
            this.x2 = this.x - mouseX;
            this.y2 = this.y - mouseY;
            LegacyGui.getClickGui().getComponents().forEach((component) -> {
                if (component.drag) {
                    component.drag = false;
                }

            });
            this.drag = true;
        } else if (mouseButton == 1 && this.isHovering(mouseX, mouseY)) {
            this.open = !this.open;
            mc.func_147118_V().func_147682_a(PositionedSoundRecord.func_184371_a(SoundEvents.field_187909_gi, 1.0F));
        } else if (this.open) {
            this.getItems().forEach((item) -> {
                item.mouseClicked(mouseX, mouseY, mouseButton);
            });
        }
    }

    public void mouseReleased(int mouseX, int mouseY, int releaseButton) {
        if (releaseButton == 0) {
            this.drag = false;
        }

        if (this.open) {
            this.getItems().forEach((item) -> {
                item.mouseReleased(mouseX, mouseY, releaseButton);
            });
        }
    }

    public void onKeyTyped(char typedChar, int keyCode) {
        if (this.open) {
            this.getItems().forEach((item) -> {
                item.onKeyTyped(typedChar, keyCode);
            });
        }
    }

    public void addButton(Button button) {
        this.items.add(button);
    }

    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return this.height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean isHidden() {
        return this.hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public boolean isOpen() {
        return this.open;
    }

    public final ArrayList getItems() {
        return this.items;
    }

    private boolean isHovering(int mouseX, int mouseY) {
        return mouseX >= this.getX() && mouseX <= this.getX() + this.getWidth() && mouseY >= this.getY() && mouseY <= this.getY() + this.getHeight() - (this.open ? 2 : 0);
    }

    private float getTotalItemHeight() {
        float height = 0.0F;

        Item item;
        for (Iterator var2 = this.getItems().iterator(); var2.hasNext(); height += (float) item.getHeight() + 1.5F) {
            item = (Item) var2.next();
        }

        return height;
    }
}
