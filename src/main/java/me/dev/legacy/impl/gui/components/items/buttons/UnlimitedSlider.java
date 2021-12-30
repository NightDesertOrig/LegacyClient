package me.dev.legacy.impl.gui.components.items.buttons;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.dev.legacy.Legacy;
import me.dev.legacy.api.util.ColorUtil;
import me.dev.legacy.api.util.RenderUtil;
import me.dev.legacy.impl.gui.LegacyGui;
import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.modules.client.ClickGui;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

public class UnlimitedSlider extends Button {
    public Setting setting;

    public UnlimitedSlider(Setting setting) {
        super(setting.getName());
        this.setting = setting;
        this.width = 15;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        int color2 = ColorUtil.toARGB(((Integer) ClickGui.getInstance().fontcolorr.getValue()).intValue(), ((Integer) ClickGui.getInstance().fontcolorb.getValue()).intValue(), ((Integer) ClickGui.getInstance().fontcolorb.getValue()).intValue(), 255);
        RenderUtil.drawRect(this.x, this.y, this.x + (float) this.width + 7.4F + 20.0F, this.y + (float) this.height - 0.5F, !this.isHovering(mouseX, mouseY) ? Legacy.colorManager.getColorWithAlpha(((Integer) ((ClickGui) Legacy.moduleManager.getModuleByClass(ClickGui.class)).hoverAlpha.getValue()).intValue()) : Legacy.colorManager.getColorWithAlpha(((Integer) ((ClickGui) Legacy.moduleManager.getModuleByClass(ClickGui.class)).alpha.getValue()).intValue()));
        Legacy.textManager.drawStringWithShadow(" - " + this.setting.getName() + " " + ChatFormatting.GRAY + this.setting.getValue() + ChatFormatting.WHITE + " +", this.x + 2.3F, this.y - 1.7F - (float) LegacyGui.getClickGui().getTextOffset(), this.getState() ? -2 : color2);
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.isHovering(mouseX, mouseY)) {
            mc.func_147118_V().func_147682_a(PositionedSoundRecord.func_184371_a(SoundEvents.field_187909_gi, 1.0F));
            if (this.isRight(mouseX)) {
                if (this.setting.getValue() instanceof Double) {
                    this.setting.setValue(((Double) this.setting.getValue()).doubleValue() + 1.0D);
                } else if (this.setting.getValue() instanceof Float) {
                    this.setting.setValue(((Float) this.setting.getValue()).floatValue() + 1.0F);
                } else if (this.setting.getValue() instanceof Integer) {
                    this.setting.setValue(((Integer) this.setting.getValue()).intValue() + 1);
                }
            } else if (this.setting.getValue() instanceof Double) {
                this.setting.setValue(((Double) this.setting.getValue()).doubleValue() - 1.0D);
            } else if (this.setting.getValue() instanceof Float) {
                this.setting.setValue(((Float) this.setting.getValue()).floatValue() - 1.0F);
            } else if (this.setting.getValue() instanceof Integer) {
                this.setting.setValue(((Integer) this.setting.getValue()).intValue() - 1);
            }
        }

    }

    public void update() {
        this.setHidden(!this.setting.isVisible());
    }

    public int getHeight() {
        return 14;
    }

    public void toggle() {
    }

    public boolean getState() {
        return true;
    }

    public boolean isRight(int x) {
        return (float) x > this.x + ((float) this.width + 7.4F) / 2.0F;
    }
}
