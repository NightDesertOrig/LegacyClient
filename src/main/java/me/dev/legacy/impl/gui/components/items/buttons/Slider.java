package me.dev.legacy.impl.gui.components.items.buttons;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.dev.legacy.Legacy;
import me.dev.legacy.api.util.ColorUtil;
import me.dev.legacy.api.util.RenderUtil;
import me.dev.legacy.impl.gui.LegacyGui;
import me.dev.legacy.impl.gui.components.Component;
import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.modules.client.ClickGui;
import org.lwjgl.input.Mouse;

import java.util.Iterator;

public class Slider extends Button {
    private final Number min;
    private final Number max;
    private final int difference;
    public Setting setting;

    public Slider(Setting setting) {
        super(setting.getName());
        this.setting = setting;
        this.min = (Number) setting.getMin();
        this.max = (Number) setting.getMax();
        this.difference = this.max.intValue() - this.min.intValue();
        this.width = 15;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        int color2 = ColorUtil.toARGB(((Integer) ClickGui.getInstance().fontcolorr.getValue()).intValue(), ((Integer) ClickGui.getInstance().fontcolorb.getValue()).intValue(), ((Integer) ClickGui.getInstance().fontcolorb.getValue()).intValue(), 255);
        this.dragSetting(mouseX, mouseY);
        RenderUtil.drawRect(this.x + 20.0F, this.y, this.x + (float) this.width + 7.4F + 20.0F, this.y + (float) this.height - 0.5F, !this.isHovering(mouseX, mouseY) ? 290805077 : -2007673515);
        RenderUtil.drawRect(this.x, this.y, ((Number) this.setting.getValue()).floatValue() <= this.min.floatValue() ? this.x : this.x + ((float) this.width + 7.4F + 20.0F) * this.partialMultiplier(), this.y + (float) this.height - 0.5F, !this.isHovering(mouseX, mouseY) ? Legacy.colorManager.getColorWithAlpha(((Integer) ((ClickGui) Legacy.moduleManager.getModuleByClass(ClickGui.class)).hoverAlpha.getValue()).intValue()) : Legacy.colorManager.getColorWithAlpha(((Integer) ((ClickGui) Legacy.moduleManager.getModuleByClass(ClickGui.class)).alpha.getValue()).intValue()));
        Legacy.textManager.drawStringWithShadow(this.getName() + " " + ChatFormatting.GRAY + (this.setting.getValue() instanceof Float ? this.setting.getValue() : ((Number) this.setting.getValue()).doubleValue()), this.x + 2.3F, this.y - 1.7F - (float) LegacyGui.getClickGui().getTextOffset(), color2);
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.isHovering(mouseX, mouseY)) {
            this.setSettingFromX(mouseX);
        }

    }

    public boolean isHovering(int mouseX, int mouseY) {
        Iterator var3 = LegacyGui.getClickGui().getComponents().iterator();

        while (var3.hasNext()) {
            Component component = (Component) var3.next();
            if (component.drag) {
                return false;
            }
        }

        return (float) mouseX >= this.getX() && (float) mouseX <= this.getX() + (float) this.getWidth() + 8.0F && (float) mouseY >= this.getY() && (float) mouseY <= this.getY() + (float) this.height;
    }

    public void update() {
        this.setHidden(!this.setting.isVisible());
    }

    private void dragSetting(int mouseX, int mouseY) {
        if (this.isHovering(mouseX, mouseY) && Mouse.isButtonDown(0)) {
            this.setSettingFromX(mouseX);
        }

    }

    public int getHeight() {
        return 14;
    }

    private void setSettingFromX(int mouseX) {
        float percent = ((float) mouseX - this.x) / ((float) this.width + 7.4F);
        if (this.setting.getValue() instanceof Double) {
            double result = ((Double) this.setting.getMin()).doubleValue() + (double) ((float) this.difference * percent);
            this.setting.setValue((double) Math.round(10.0D * result) / 10.0D);
        } else if (this.setting.getValue() instanceof Float) {
            float result = ((Float) this.setting.getMin()).floatValue() + (float) this.difference * percent;
            this.setting.setValue((float) Math.round(10.0F * result) / 10.0F);
        } else if (this.setting.getValue() instanceof Integer) {
            this.setting.setValue(((Integer) this.setting.getMin()).intValue() + (int) ((float) this.difference * percent));
        }

    }

    private float middle() {
        return this.max.floatValue() - this.min.floatValue();
    }

    private float part() {
        return ((Number) this.setting.getValue()).floatValue() - this.min.floatValue();
    }

    private float partialMultiplier() {
        return this.part() / this.middle();
    }
}
