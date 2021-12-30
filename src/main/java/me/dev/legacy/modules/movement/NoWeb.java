package me.dev.legacy.modules.movement;

import me.dev.legacy.Legacy;
import me.dev.legacy.api.event.events.block.BlockCollisionBoundingBoxEvent;
import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.modules.Module;
import me.dev.legacy.modules.ModuleManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockWeb;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class NoWeb extends Module {
    public Setting disableBB = this.register(new Setting("AddBB", true));
    public Setting bbOffset = this.register(new Setting("BBOffset", 0.4F, -2.0F, 2.0F));
    public Setting onGround = this.register(new Setting("On Ground", true));
    public Setting motionY = this.register(new Setting("Set MotionY", 0.0F, 0.0F, 20.0F));
    public Setting motionX = this.register(new Setting("Set MotionX", 0.8F, -1.0F, 5.0F));

    public NoWeb() {
        super("NoWeb", "aw", Module.Category.MOVEMENT, true, false, false);
    }

    @SubscribeEvent
    public void bbEvent(BlockCollisionBoundingBoxEvent event) {
        if (!nullCheck()) {
            if (mc.field_71441_e.func_180495_p(event.getPos()).func_177230_c() instanceof BlockWeb && ((Boolean) this.disableBB.getValue()).booleanValue()) {
                event.setCanceled(true);
                event.setBoundingBox(Block.field_185505_j.func_191195_a(0.0D, (double) ((Float) this.bbOffset.getValue()).floatValue(), 0.0D));
            }

        }
    }

    public void onUpdate() {
        ModuleManager var10000 = Legacy.moduleManager;
        if (!ModuleManager.isModuleEnabled("WebTP")) {
            label43:
            {
                if (mc.field_71439_g.field_70134_J) {
                    var10000 = Legacy.moduleManager;
                    if (!ModuleManager.isModuleEnabled("Step")) {
                        break label43;
                    }
                }

                if (!mc.field_71439_g.field_70134_J) {
                    return;
                }

                var10000 = Legacy.moduleManager;
                if (ModuleManager.isModuleEnabled("StepTwo")) {
                    return;
                }
            }

            EntityPlayerSP player2;
            if (Keyboard.isKeyDown(mc.field_71474_y.field_74311_E.field_74512_d)) {
                mc.field_71439_g.field_70134_J = true;
                player2 = mc.field_71439_g;
                player2.field_70181_x *= (double) ((Float) this.motionY.getValue()).floatValue();
            } else if (((Boolean) this.onGround.getValue()).booleanValue()) {
                mc.field_71439_g.field_70122_E = false;
            }

            if (Keyboard.isKeyDown(mc.field_71474_y.field_74351_w.field_74512_d) || Keyboard.isKeyDown(mc.field_71474_y.field_74368_y.field_74512_d) || Keyboard.isKeyDown(mc.field_71474_y.field_74370_x.field_74512_d) || Keyboard.isKeyDown(mc.field_71474_y.field_74366_z.field_74512_d)) {
                mc.field_71439_g.field_70134_J = false;
                player2 = mc.field_71439_g;
                player2.field_70159_w *= (double) ((Float) this.motionX.getValue()).floatValue();
                EntityPlayerSP player3 = mc.field_71439_g;
                player3.field_70179_y *= (double) ((Float) this.motionX.getValue()).floatValue();
            }

        }
    }
}
