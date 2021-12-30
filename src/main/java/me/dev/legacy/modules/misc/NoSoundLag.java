package me.dev.legacy.modules.misc;

import com.google.common.collect.Sets;
import me.dev.legacy.api.event.events.other.PacketEvent;
import me.dev.legacy.api.util.MathUtil;
import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.modules.Module;
import me.dev.legacy.modules.combat.AutoCrystal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

public class NoSoundLag extends Module {
    private static final Set BLACKLIST;
    private static NoSoundLag instance;
    public Setting crystals = this.register(new Setting("Crystals", true));
    public Setting armor = this.register(new Setting("Armor", true));
    public Setting soundRange = this.register(new Setting("SoundRange", 12.0F, 0.0F, 12.0F));

    public NoSoundLag() {
        super("NoSoundLag", "Prevents Lag through sound spam.", Module.Category.MISC, true, false, false);
        instance = this;
    }

    public static NoSoundLag getInstance() {
        if (instance == null) {
            instance = new NoSoundLag();
        }

        return instance;
    }

    public static void removeEntities(SPacketSoundEffect packet, float range) {
        BlockPos pos = new BlockPos(packet.func_149207_d(), packet.func_149211_e(), packet.func_149210_f());
        ArrayList toRemove = new ArrayList();
        Iterator var4 = mc.field_71441_e.field_72996_f.iterator();

        Entity entity;
        while (var4.hasNext()) {
            entity = (Entity) var4.next();
            if (entity instanceof EntityEnderCrystal && entity.func_174818_b(pos) <= MathUtil.square((double) range)) {
                toRemove.add(entity);
            }
        }

        var4 = toRemove.iterator();

        while (var4.hasNext()) {
            entity = (Entity) var4.next();
            entity.func_70106_y();
        }

    }

    @SubscribeEvent
    public void onPacketReceived(PacketEvent.Receive event) {
        if (event != null && event.getPacket() != null && mc.field_71439_g != null && mc.field_71441_e != null && event.getPacket() instanceof SPacketSoundEffect) {
            SPacketSoundEffect packet = (SPacketSoundEffect) event.getPacket();
            if (((Boolean) this.crystals.getValue()).booleanValue() && packet.func_186977_b() == SoundCategory.BLOCKS && packet.func_186978_a() == SoundEvents.field_187539_bB && AutoCrystal.getInstance().isOff()) {
                removeEntities(packet, ((Float) this.soundRange.getValue()).floatValue());
            }

            if (BLACKLIST.contains(packet.func_186978_a()) && ((Boolean) this.armor.getValue()).booleanValue()) {
                event.setCanceled(true);
            }
        }

    }

    static {
        BLACKLIST = Sets.newHashSet(new SoundEvent[]{SoundEvents.field_187719_p, SoundEvents.field_191258_p, SoundEvents.field_187716_o, SoundEvents.field_187725_r, SoundEvents.field_187722_q, SoundEvents.field_187713_n, SoundEvents.field_187728_s});
    }
}
