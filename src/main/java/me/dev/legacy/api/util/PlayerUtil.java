package me.dev.legacy.api.util;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.util.UUIDTypeAdapter;
import me.dev.legacy.api.event.events.other.PacketEvent;
import me.dev.legacy.impl.command.Command;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.potion.Potion;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.io.IOUtils;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class PlayerUtil implements Util {
    private static final JsonParser PARSER = new JsonParser();
    private static boolean shifting;
    private static boolean switching;
    private int slot;

    public static String getNameFromUUID(UUID uuid) {
        try {
            PlayerUtil.lookUpName process = new PlayerUtil.lookUpName(uuid);
            Thread thread = new Thread(process);
            thread.start();
            thread.join();
            return process.getName();
        } catch (Exception var3) {
            return null;
        }
    }

    public static BlockPos getPlayerPos() {
        return new BlockPos(Math.floor(mc.field_71439_g.field_70165_t), Math.floor(mc.field_71439_g.field_70163_u), Math.floor(mc.field_71439_g.field_70161_v));
    }

    public static boolean isInHole(Entity e) {
        BlockPos pos = new BlockPos(Math.floor(e.func_174791_d().field_72450_a), Math.floor(e.func_174791_d().field_72448_b + 0.2D), Math.floor(e.func_174791_d().field_72449_c));
        return mc.field_71441_e.func_180495_p(pos.func_177977_b()).func_177230_c().field_149781_w >= 1200.0F && mc.field_71441_e.func_180495_p(pos.func_177974_f()).func_177230_c().field_149781_w >= 1200.0F && mc.field_71441_e.func_180495_p(pos.func_177976_e()).func_177230_c().field_149781_w >= 1200.0F && mc.field_71441_e.func_180495_p(pos.func_177978_c()).func_177230_c().field_149781_w >= 1200.0F && mc.field_71441_e.func_180495_p(pos.func_177968_d()).func_177230_c().field_149781_w >= 1200.0F;
    }

    public static double getBaseMoveSpeed() {
        double baseSpeed = 0.2873D;
        if (mc.field_71439_g != null && mc.field_71439_g.func_70644_a(Potion.func_188412_a(1))) {
            int amplifier = mc.field_71439_g.func_70660_b(Potion.func_188412_a(1)).func_76458_c();
            baseSpeed *= 1.0D + 0.2D * (double) (amplifier + 1);
        }

        return baseSpeed;
    }

    public static boolean isMoving(EntityLivingBase entity) {
        return entity.field_191988_bg != 0.0F || entity.field_70702_br != 0.0F;
    }

    public static void setSpeed(EntityLivingBase entity, double speed) {
        double[] dir = forward(speed);
        entity.field_70159_w = dir[0];
        entity.field_70179_y = dir[1];
    }

    public static double[] forward(double speed) {
        float forward = mc.field_71439_g.field_71158_b.field_192832_b;
        float side = mc.field_71439_g.field_71158_b.field_78902_a;
        float yaw = mc.field_71439_g.field_70126_B + (mc.field_71439_g.field_70177_z - mc.field_71439_g.field_70126_B) * mc.func_184121_ak();
        if (forward != 0.0F) {
            if (side > 0.0F) {
                yaw += (float) (forward > 0.0F ? -45 : 45);
            } else if (side < 0.0F) {
                yaw += (float) (forward > 0.0F ? 45 : -45);
            }

            side = 0.0F;
            if (forward > 0.0F) {
                forward = 1.0F;
            } else if (forward < 0.0F) {
                forward = -1.0F;
            }
        }

        double sin = Math.sin(Math.toRadians((double) (yaw + 90.0F)));
        double cos = Math.cos(Math.toRadians((double) (yaw + 90.0F)));
        double posX = (double) forward * speed * cos + (double) side * speed * sin;
        double posZ = (double) forward * speed * sin - (double) side * speed * cos;
        return new double[]{posX, posZ};
    }

    public static String getNameFromUUID(String uuid) {
        try {
            PlayerUtil.lookUpName process = new PlayerUtil.lookUpName(uuid);
            Thread thread = new Thread(process);
            thread.start();
            thread.join();
            return process.getName();
        } catch (Exception var3) {
            return null;
        }
    }

    public static UUID getUUIDFromName(String name) {
        try {
            PlayerUtil.lookUpUUID process = new PlayerUtil.lookUpUUID(name);
            Thread thread = new Thread(process);
            thread.start();
            thread.join();
            return process.getUUID();
        } catch (Exception var3) {
            return null;
        }
    }

    public static String requestIDs(String data) {
        try {
            String query = "https://api.mojang.com/profiles/minecraft";
            URL url = new URL(query);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            OutputStream os = conn.getOutputStream();
            os.write(data.getBytes(StandardCharsets.UTF_8));
            os.close();
            InputStream in = new BufferedInputStream(conn.getInputStream());
            String res = convertStreamToString(in);
            in.close();
            conn.disconnect();
            return res;
        } catch (Exception var7) {
            return null;
        }
    }

    public static String convertStreamToString(InputStream is) {
        Scanner s = (new Scanner(is)).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "/";
    }

    public static List getHistoryOfNames(UUID id) {
        try {
            JsonArray array = getResources(new URL("https://api.mojang.com/user/profiles/" + getIdNoHyphens(id) + "/names"), "GET").getAsJsonArray();
            List temp = Lists.newArrayList();
            Iterator var3 = array.iterator();

            while (var3.hasNext()) {
                JsonElement e = (JsonElement) var3.next();
                JsonObject node = e.getAsJsonObject();
                String name = node.get("name").getAsString();
                long changedAt = node.has("changedToAt") ? node.get("changedToAt").getAsLong() : 0L;
                temp.add(name + "Г‚В§8" + (new Date(changedAt)).toString());
            }

            Collections.sort(temp);
            return temp;
        } catch (Exception var9) {
            return null;
        }
    }

    public static int findObiInHotbar() {
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = mc.field_71439_g.field_71071_by.func_70301_a(i);
            if (stack != ItemStack.field_190927_a && stack.func_77973_b() instanceof ItemBlock) {
                Block block = ((ItemBlock) stack.func_77973_b()).func_179223_d();
                if (block instanceof BlockEnderChest) {
                    return i;
                }

                if (block instanceof BlockObsidian) {
                    return i;
                }
            }
        }

        return -1;
    }

    public static String getIdNoHyphens(UUID uuid) {
        return uuid.toString().replaceAll("-", "");
    }

    private static JsonElement getResources(URL url, String request) throws Exception {
        return getResources(url, request, (JsonElement) null);
    }

    private static JsonElement getResources(URL url, String request, JsonElement element) throws Exception {
        HttpsURLConnection connection = null;

        try {
            connection = (HttpsURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod(request);
            connection.setRequestProperty("Content-Type", "application/json");
            if (element != null) {
                DataOutputStream output = new DataOutputStream(connection.getOutputStream());
                output.writeBytes(AdvancementManager.field_192783_b.toJson(element));
                output.close();
            }

            Scanner scanner = new Scanner(connection.getInputStream());
            StringBuilder builder = new StringBuilder();

            while (scanner.hasNextLine()) {
                builder.append(scanner.nextLine());
                builder.append('\n');
            }

            scanner.close();
            String json = builder.toString();
            JsonElement data = PARSER.parse(json);
            JsonElement var8 = data;
            return var8;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }

        }
    }

    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketEntityAction) {
            CPacketEntityAction packet = (CPacketEntityAction) event.getPacket();
            if (packet.func_180764_b() == Action.START_SNEAKING) {
                shifting = true;
            } else if (packet.func_180764_b() == Action.STOP_SNEAKING) {
                shifting = false;
            }
        }

        if (event.getPacket() instanceof CPacketHeldItemChange) {
            this.slot = ((CPacketHeldItemChange) event.getPacket()).func_149614_c();
        }

    }

    public void setSwitching(boolean switching) {
        switching = switching;
    }

    public static boolean isShifting() {
        return shifting;
    }

    public int getSlot() {
        return this.slot;
    }

    public static class lookUpName implements Runnable {
        private final String uuid;
        private final UUID uuidID;
        private volatile String name;

        public lookUpName(String input) {
            this.uuid = input;
            this.uuidID = UUID.fromString(input);
        }

        public lookUpName(UUID input) {
            this.uuidID = input;
            this.uuid = input.toString();
        }

        public void run() {
            this.name = this.lookUpName();
        }

        public String lookUpName() {
            EntityPlayer player = null;
            if (Util.mc.field_71441_e != null) {
                player = Util.mc.field_71441_e.func_152378_a(this.uuidID);
            }

            if (player == null) {
                String url = "https://api.mojang.com/user/profiles/" + this.uuid.replace("-", "") + "/names";

                try {
                    String nameJson = IOUtils.toString(new URL(url));
                    if (nameJson.contains(",")) {
                        List names = Arrays.asList(nameJson.split(","));
                        Collections.reverse(names);
                        return ((String) names.get(1)).replace("{\"name\":\"", "").replace("\"", "");
                    } else {
                        return nameJson.replace("[{\"name\":\"", "").replace("\"}]", "");
                    }
                } catch (IOException var5) {
                    var5.printStackTrace();
                    return null;
                }
            } else {
                return player.func_70005_c_();
            }
        }

        public String getName() {
            return this.name;
        }
    }

    public static class lookUpUUID implements Runnable {
        private final String name;
        private volatile UUID uuid;

        public lookUpUUID(String name) {
            this.name = name;
        }

        public void run() {
            NetworkPlayerInfo profile;
            try {
                ArrayList infoMap = new ArrayList(((NetHandlerPlayClient) Objects.requireNonNull(Util.mc.func_147114_u())).func_175106_d());
                profile = (NetworkPlayerInfo) infoMap.stream().filter((networkPlayerInfo) -> {
                    return networkPlayerInfo.func_178845_a().getName().equalsIgnoreCase(this.name);
                }).findFirst().orElse((Object) null);

                assert profile != null;

                this.uuid = profile.func_178845_a().getId();
            } catch (Exception var6) {
                profile = null;
            }

            if (profile == null) {
                Command.sendMessage("Player isn't online. Looking up UUID..");
                String s = PlayerUtil.requestIDs("[\"" + this.name + "\"]");
                if (s != null && !s.isEmpty()) {
                    JsonElement element = (new JsonParser()).parse(s);
                    if (element.getAsJsonArray().size() == 0) {
                        Command.sendMessage("Couldn't find player ID. (1)");
                    } else {
                        try {
                            String id = element.getAsJsonArray().get(0).getAsJsonObject().get("id").getAsString();
                            this.uuid = UUIDTypeAdapter.fromString(id);
                        } catch (Exception var5) {
                            var5.printStackTrace();
                            Command.sendMessage("Couldn't find player ID. (2)");
                        }
                    }
                } else {
                    Command.sendMessage("Couldn't find player ID. Are you connected to the internet? (0)");
                }
            }

        }

        public UUID getUUID() {
            return this.uuid;
        }

        public String getName() {
            return this.name;
        }
    }
}
