package me.dev.legacy.modules.combat;

import me.dev.legacy.api.event.events.other.PacketEvent;
import me.dev.legacy.api.event.events.update.UpdateEvent;
import me.dev.legacy.api.util.BlockUtil;
import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.modules.Module;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerDigging.Action;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

public class AntiRegear extends Module {
    private final Setting reach = this.register(new Setting("Reach", 5.0F, 1.0F, 6.0F));
    private final Setting retry = this.register(new Setting("Retry Delay", Integer.valueOf(10), Integer.valueOf(0), Integer.valueOf(20)));
    private final List retries = new ArrayList();
    private final List selfPlaced = new ArrayList();
    private int ticks;

    public AntiRegear() {
        super("AntiRegear", "AntiRegear.", Module.Category.COMBAT, true, false, false);
    }

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (this.ticks++ < ((Integer) this.retry.getValue()).intValue()) {
            this.ticks = 0;
            this.retries.clear();
        }

        List sphere = BlockUtil.getSphere(((Float) this.reach.getValue()).floatValue());
        int size = sphere.size();

        for (int i = 0; i < size; ++i) {
            BlockPos pos = (BlockPos) sphere.get(i);
            if (!this.retries.contains(pos) && !this.selfPlaced.contains(pos) && mc.field_71441_e.func_180495_p(pos).func_177230_c() instanceof BlockShulkerBox) {
                mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
                mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerDigging(Action.START_DESTROY_BLOCK, pos, EnumFacing.UP));
                mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerDigging(Action.STOP_DESTROY_BLOCK, pos, EnumFacing.UP));
                this.retries.add(pos);
            }
        }

    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock) {
            CPacketPlayerTryUseItemOnBlock packet = (CPacketPlayerTryUseItemOnBlock) event.getPacket();
            if (mc.field_71439_g.func_184586_b(packet.func_187022_c()).func_77973_b() instanceof ItemShulkerBox) {
                this.selfPlaced.add(packet.func_187023_a().func_177972_a(packet.func_187024_b()));
            }
        }

    }
}
