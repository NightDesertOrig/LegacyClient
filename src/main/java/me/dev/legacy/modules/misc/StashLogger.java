package me.dev.legacy.modules.misc;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.dev.legacy.api.event.events.other.PacketEvent;
import me.dev.legacy.impl.command.Command;
import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.modules.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

public class StashLogger extends Module {
    private final Setting chests = this.register(new Setting("Chests", true));
    private final Setting chestsValue = this.register(new Setting("ChestsValue", Integer.valueOf(4), Integer.valueOf(1), Integer.valueOf(30), (v) -> {
        return ((Boolean) this.chests.getValue()).booleanValue();
    }));
    private final Setting Shulkers = this.register(new Setting("Shulkers", true));
    private final Setting shulkersValue = this.register(new Setting("ShulkersValue", Integer.valueOf(4), Integer.valueOf(1), Integer.valueOf(30), (v) -> {
        return ((Boolean) this.Shulkers.getValue()).booleanValue();
    }));
    private final Setting writeToFile = this.register(new Setting("CoordsSaver", true));
    File mainFolder;
    final Iterator iterator;

    public StashLogger() {
        super("StashLogger", "Logs stashes", Module.Category.MISC, true, false, false);
        this.mainFolder = new File(Minecraft.func_71410_x().field_71412_D + File.separator + "legacy");
        this.iterator = null;
    }

    @SubscribeEvent
    public void onPacket(PacketEvent event) {
        if (!nullCheck()) {
            if (event.getPacket() instanceof SPacketChunkData) {
                SPacketChunkData l_Packet = (SPacketChunkData) event.getPacket();
                int l_ChestsCount = 0;
                int shulkers = 0;
                Iterator var5 = l_Packet.func_189554_f().iterator();

                while (true) {
                    while (var5.hasNext()) {
                        NBTTagCompound l_Tag = (NBTTagCompound) var5.next();
                        String l_Id = l_Tag.func_74779_i("id");
                        if (l_Id.equals("minecraft:chest") && ((Boolean) this.chests.getValue()).booleanValue()) {
                            ++l_ChestsCount;
                        } else if (l_Id.equals("minecraft:shulker_box") && ((Boolean) this.Shulkers.getValue()).booleanValue()) {
                            ++shulkers;
                        }
                    }

                    if (l_ChestsCount >= ((Integer) this.chestsValue.getValue()).intValue()) {
                        this.SendMessage(String.format("%s chests located at X: %s, Z: %s", l_ChestsCount, l_Packet.func_149273_e() * 16, l_Packet.func_149271_f() * 16), true);
                    }

                    if (shulkers >= ((Integer) this.shulkersValue.getValue()).intValue()) {
                        this.SendMessage(String.format("%s shulker boxes at X: %s, Z: %s", shulkers, l_Packet.func_149273_e() * 16, l_Packet.func_149271_f() * 16), true);
                    }
                    break;
                }
            }

        }
    }

    private void SendMessage(String message, boolean save) {
        String server = Minecraft.func_71410_x().func_71356_B() ? "singleplayer".toUpperCase() : mc.func_147104_D().field_78845_b;
        if (((Boolean) this.writeToFile.getValue()).booleanValue() && save) {
            try {
                FileWriter writer = new FileWriter(this.mainFolder + "/stashes.txt", true);
                writer.write("[" + server + "]: " + message + "\n");
                writer.close();
            } catch (IOException var5) {
                var5.printStackTrace();
            }
        }

        mc.func_147118_V().func_147682_a(PositionedSoundRecord.func_194007_a(SoundEvents.field_187604_bf, 1.0F, 1.0F));
        Command.sendMessage(ChatFormatting.GREEN + message);
    }
}
