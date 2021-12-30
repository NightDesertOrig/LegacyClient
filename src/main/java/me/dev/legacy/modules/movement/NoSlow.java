package me.dev.legacy.modules.movement;

import me.dev.legacy.Legacy;
import me.dev.legacy.api.event.events.other.KeyPressedEvent;
import me.dev.legacy.api.event.events.other.PacketEvent;
import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.modules.Module;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class NoSlow extends Module {
    public Setting guiMove = this.register(new Setting("GuiMove", true));
    public Setting noSlow = this.register(new Setting("NoSlow", true));
    public Setting strict = this.register(new Setting("Strict", false));
    public Setting sneakPacket = this.register(new Setting("SneakPacket", false));
    public Setting webs = this.register(new Setting("Webs", false));
    public final Setting webHorizontalFactor = this.register(new Setting("WebHSpeed", 2.0D, 0.0D, 100.0D));
    public final Setting webVerticalFactor = this.register(new Setting("WebVSpeed", 2.0D, 0.0D, 100.0D));
    private static NoSlow INSTANCE = new NoSlow();
    private boolean sneaking = false;
    private static KeyBinding[] keys;

    public NoSlow() {
        super("NoSlow", "Prevents you from getting slowed down.", Module.Category.MOVEMENT, true, false, false);
        this.setInstance();
    }

    private void setInstance() {
        INSTANCE = this;
    }

    public static NoSlow getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NoSlow();
        }

        return INSTANCE;
    }

    public void onUpdate() {
        if (((Boolean) this.guiMove.getValue()).booleanValue()) {
            KeyBinding[] var1;
            int var2;
            int var3;
            KeyBinding bind;
            if (!(mc.field_71462_r instanceof GuiOptions) && !(mc.field_71462_r instanceof GuiVideoSettings) && !(mc.field_71462_r instanceof GuiScreenOptionsSounds) && !(mc.field_71462_r instanceof GuiContainer) && !(mc.field_71462_r instanceof GuiIngameMenu)) {
                if (mc.field_71462_r == null) {
                    var1 = keys;
                    var2 = var1.length;

                    for (var3 = 0; var3 < var2; ++var3) {
                        bind = var1[var3];
                        if (!Keyboard.isKeyDown(bind.func_151463_i())) {
                            KeyBinding.func_74510_a(bind.func_151463_i(), false);
                        }
                    }
                }
            } else {
                var1 = keys;
                var2 = var1.length;

                for (var3 = 0; var3 < var2; ++var3) {
                    bind = var1[var3];
                    KeyBinding.func_74510_a(bind.func_151463_i(), Keyboard.isKeyDown(bind.func_151463_i()));
                }
            }
        }

        if (((Boolean) this.webs.getValue()).booleanValue() && ((PacketFly) Legacy.moduleManager.getModuleByClass(PacketFly.class)).isDisabled() && ((PacketFly) Legacy.moduleManager.getModuleByClass(PacketFly.class)).isDisabled() && mc.field_71439_g.field_70134_J) {
            mc.field_71439_g.field_70159_w *= ((Double) this.webHorizontalFactor.getValue()).doubleValue();
            mc.field_71439_g.field_70179_y *= ((Double) this.webHorizontalFactor.getValue()).doubleValue();
            mc.field_71439_g.field_70181_x *= ((Double) this.webVerticalFactor.getValue()).doubleValue();
        }

        Item item = mc.field_71439_g.func_184607_cu().func_77973_b();
        if (this.sneaking && !mc.field_71439_g.func_184587_cr() && ((Boolean) this.sneakPacket.getValue()).booleanValue()) {
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.STOP_SNEAKING));
            this.sneaking = false;
        }

    }

    @SubscribeEvent
    public void onUseItem(RightClickItem event) {
        Item item = mc.field_71439_g.func_184586_b(event.getHand()).func_77973_b();
        if ((item instanceof ItemFood || item instanceof ItemBow || item instanceof ItemPotion && ((Boolean) this.sneakPacket.getValue()).booleanValue()) && !this.sneaking) {
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.START_SNEAKING));
            this.sneaking = true;
        }

    }

    @SubscribeEvent
    public void onInput(InputUpdateEvent event) {
        if (((Boolean) this.noSlow.getValue()).booleanValue() && mc.field_71439_g.func_184587_cr() && !mc.field_71439_g.func_184218_aH()) {
            MovementInput var10000 = event.getMovementInput();
            var10000.field_78902_a *= 5.0F;
            var10000 = event.getMovementInput();
            var10000.field_192832_b *= 5.0F;
        }

    }

    @SubscribeEvent
    public void onKeyEvent(KeyPressedEvent event) {
        if (((Boolean) this.guiMove.getValue()).booleanValue() && event.getStage() == 0 && !(mc.field_71462_r instanceof GuiChat)) {
            event.info = event.pressed;
        }

    }

    @SubscribeEvent
    public void onPacket(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketPlayer && ((Boolean) this.strict.getValue()).booleanValue() && ((Boolean) this.noSlow.getValue()).booleanValue() && mc.field_71439_g.func_184587_cr() && !mc.field_71439_g.func_184218_aH()) {
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerDigging(net.minecraft.network.play.client.CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, new BlockPos(Math.floor(mc.field_71439_g.field_70165_t), Math.floor(mc.field_71439_g.field_70163_u), Math.floor(mc.field_71439_g.field_70161_v)), EnumFacing.DOWN));
        }

    }

    static {
        keys = new KeyBinding[]{mc.field_71474_y.field_74351_w, mc.field_71474_y.field_74368_y, mc.field_71474_y.field_74370_x, mc.field_71474_y.field_74366_z, mc.field_71474_y.field_74314_A, mc.field_71474_y.field_151444_V};
    }
}
