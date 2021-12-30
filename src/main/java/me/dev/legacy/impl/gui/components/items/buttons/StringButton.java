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
import net.minecraft.util.ChatAllowedCharacters;

public class StringButton extends Button {
    private final Setting setting;
    public boolean isListening;
    private StringButton.CurrentString currentString = new StringButton.CurrentString("");

    public StringButton(Setting setting) {
        super(setting.getName());
        this.setting = setting;
        this.width = 15;
    }

    public static String removeLastChar(String str) {
        String output = "";
        if (str != null && str.length() > 0) {
            output = str.substring(0, str.length() - 1);
        }

        return output;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        int color2 = ColorUtil.toARGB(((Integer) ClickGui.getInstance().fontcolorr.getValue()).intValue(), ((Integer) ClickGui.getInstance().fontcolorb.getValue()).intValue(), ((Integer) ClickGui.getInstance().fontcolorb.getValue()).intValue(), 255);
        RenderUtil.drawRect(this.x, this.y, this.x + (float) this.width + 7.4F + 20.0F, this.y + (float) this.height - 0.5F, this.getState() ? (!this.isHovering(mouseX, mouseY) ? Legacy.colorManager.getColorWithAlpha(((Integer) ((ClickGui) Legacy.moduleManager.getModuleByClass(ClickGui.class)).hoverAlpha.getValue()).intValue()) : Legacy.colorManager.getColorWithAlpha(((Integer) ((ClickGui) Legacy.moduleManager.getModuleByClass(ClickGui.class)).alpha.getValue()).intValue())) : (!this.isHovering(mouseX, mouseY) ? 290805077 : -2007673515));
        if (this.isListening) {
            Legacy.textManager.drawStringWithShadow(this.currentString.getString() + Legacy.textManager.getIdleSign(), this.x + 2.3F, this.y - 1.7F - (float) LegacyGui.getClickGui().getTextOffset(), this.getState() ? -2 : color2);
        } else {
            Legacy.textManager.drawStringWithShadow((this.setting.getName().equals("Buttons") ? "Buttons " : (this.setting.getName().equals("Prefix") ? "Prefix  " + ChatFormatting.GRAY : "")) + this.setting.getValue(), this.x + 2.3F, this.y - 1.7F - (float) LegacyGui.getClickGui().getTextOffset(), this.getState() ? -2 : color2);
        }

    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.isHovering(mouseX, mouseY)) {
            mc.func_147118_V().func_147682_a(PositionedSoundRecord.func_184371_a(SoundEvents.field_187909_gi, 1.0F));
        }

    }

    public void onKeyTyped(char typedChar, int keyCode) {
        if (this.isListening) {
            switch (keyCode) {
                case 1:
                    return;
                case 28:
                    this.enterString();
                case 14:
                    this.setString(removeLastChar(this.currentString.getString()));
                default:
                    if (ChatAllowedCharacters.func_71566_a(typedChar)) {
                        this.setString(this.currentString.getString() + typedChar);
                    }
            }
        }

    }

    public void update() {
        this.setHidden(!this.setting.isVisible());
    }

    private void enterString() {
        if (this.currentString.getString().isEmpty()) {
            this.setting.setValue(this.setting.getDefaultValue());
        } else {
            this.setting.setValue(this.currentString.getString());
        }

        this.setString("");
        this.onMouseClick();
    }

    public int getHeight() {
        return 14;
    }

    public void toggle() {
        this.isListening = !this.isListening;
    }

    public boolean getState() {
        return !this.isListening;
    }

    public void setString(String newString) {
        this.currentString = new StringButton.CurrentString(newString);
    }

    public static class CurrentString {
        private final String string;

        public CurrentString(String string) {
            this.string = string;
        }

        public String getString() {
            return this.string;
        }
    }
}
