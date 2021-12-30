package me.dev.legacy.impl.gui.components.items.buttons;

import me.dev.legacy.Legacy;
import me.dev.legacy.api.util.ColorUtil;
import me.dev.legacy.api.util.RenderUtil;
import me.dev.legacy.impl.gui.LegacyGui;
import me.dev.legacy.impl.gui.components.Component;
import me.dev.legacy.impl.gui.components.items.Item;
import me.dev.legacy.modules.client.ClickGui;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

import java.util.Iterator;

public class Button extends Item {
    private boolean state;

    public Button(String name) {
        super(name);
        this.height = 15;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        int color2 = ColorUtil.toARGB(((Integer) ClickGui.getInstance().fontcolorr.getValue()).intValue(), ((Integer) ClickGui.getInstance().fontcolorb.getValue()).intValue(), ((Integer) ClickGui.getInstance().fontcolorb.getValue()).intValue(), 255);
        RenderUtil.drawRect(this.x, this.y, this.x + (float) this.width + 20.0F, this.y + (float) this.height - 0.5F, this.getState() ? (!this.isHovering(mouseX, mouseY) ? Legacy.colorManager.getColorWithAlpha(((Integer) ((ClickGui) Legacy.moduleManager.getModuleByClass(ClickGui.class)).hoverAlpha.getValue()).intValue()) : Legacy.colorManager.getColorWithAlpha(((Integer) ((ClickGui) Legacy.moduleManager.getModuleByClass(ClickGui.class)).alpha.getValue()).intValue())) : (!this.isHovering(mouseX, mouseY) ? 290805077 : -2007673515));
        Legacy.textManager.drawStringWithShadow(this.getName(), this.x + 2.3F, this.y - 2.0F - (float) LegacyGui.getClickGui().getTextOffset(), this.getState() ? -2 : color2);
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0 && this.isHovering(mouseX, mouseY)) {
            this.onMouseClick();
        }

    }

    public void onMouseClick() {
        this.state = !this.state;
        this.toggle();
        mc.func_147118_V().func_147682_a(PositionedSoundRecord.func_184371_a(SoundEvents.field_187909_gi, 1.0F));
    }

    public void toggle() {
    }

    public boolean getState() {
        return this.state;
    }

    public int getHeight() {
        return 14;
    }

    public boolean isHovering(int mouseX, int mouseY) {
        Iterator var3 = LegacyGui.getClickGui().getComponents().iterator();

        while (var3.hasNext()) {
            Component component = (Component) var3.next();
            if (component.drag) {
                return false;
            }
        }

        return (float) mouseX >= this.getX() && (float) mouseX <= this.getX() + (float) this.getWidth() && (float) mouseY >= this.getY() && (float) mouseY <= this.getY() + (float) this.height;
    }
}
