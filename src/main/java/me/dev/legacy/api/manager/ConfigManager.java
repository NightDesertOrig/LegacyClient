package me.dev.legacy.api.manager;

import com.google.gson.*;
import me.dev.legacy.Legacy;
import me.dev.legacy.api.AbstractModule;
import me.dev.legacy.api.util.Util;
import me.dev.legacy.impl.setting.Bind;
import me.dev.legacy.impl.setting.EnumConverter;
import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.modules.Module;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class ConfigManager implements Util {
    public ArrayList features = new ArrayList();
    public String config = "legacy/config/";

    public static void setValueFromJson(AbstractModule feature, Setting setting, JsonElement element) {
        String var4 = setting.getType();
        byte var5 = -1;
        switch (var4.hashCode()) {
            case -1808118735:
                if (var4.equals("String")) {
                    var5 = 4;
                }
                break;
            case -672261858:
                if (var4.equals("Integer")) {
                    var5 = 3;
                }
                break;
            case 2070621:
                if (var4.equals("Bind")) {
                    var5 = 5;
                }
                break;
            case 2165025:
                if (var4.equals("Enum")) {
                    var5 = 6;
                }
                break;
            case 67973692:
                if (var4.equals("Float")) {
                    var5 = 2;
                }
                break;
            case 1729365000:
                if (var4.equals("Boolean")) {
                    var5 = 0;
                }
                break;
            case 2052876273:
                if (var4.equals("Double")) {
                    var5 = 1;
                }
        }

        switch (var5) {
            case 0:
                setting.setValue(element.getAsBoolean());
                return;
            case 1:
                setting.setValue(element.getAsDouble());
                return;
            case 2:
                setting.setValue(element.getAsFloat());
                return;
            case 3:
                setting.setValue(element.getAsInt());
                return;
            case 4:
                String str = element.getAsString();
                setting.setValue(str.replace("_", " "));
                return;
            case 5:
                setting.setValue((new Bind.BindConverter()).doBackward(element));
                return;
            case 6:
                try {
                    EnumConverter converter = new EnumConverter(((Enum) setting.getValue()).getClass());
                    Enum value = converter.doBackward(element);
                    setting.setValue(value == null ? setting.getDefaultValue() : value);
                } catch (Exception var8) {
                    ;
                }

                return;
            default:
                Legacy.LOGGER.error("Unknown Setting type for: " + feature.getName() + " : " + setting.getName());
        }
    }

    private static void loadFile(JsonObject input, AbstractModule feature) {
        Iterator var2 = input.entrySet().iterator();

        while (true) {
            while (var2.hasNext()) {
                Entry entry = (Entry) var2.next();
                String settingName = (String) entry.getKey();
                JsonElement element = (JsonElement) entry.getValue();
                if (feature instanceof FriendManager) {
                    try {
                        Legacy.friendManager.addFriend(new FriendManager.Friend(element.getAsString(), UUID.fromString(settingName)));
                    } catch (Exception var10) {
                        var10.printStackTrace();
                    }
                } else {
                    boolean settingFound = false;
                    Iterator var7 = feature.getSettings().iterator();

                    while (var7.hasNext()) {
                        Setting setting = (Setting) var7.next();
                        if (settingName.equals(setting.getName())) {
                            try {
                                setValueFromJson(feature, setting, element);
                            } catch (Exception var11) {
                                var11.printStackTrace();
                            }

                            settingFound = true;
                        }
                    }

                    if (settingFound) {
                        ;
                    }
                }
            }

            return;
        }
    }

    public void loadConfig(String name) {
        List files = (List) Arrays.stream((Object[]) Objects.requireNonNull((new File("legacy")).listFiles())).filter(File::isDirectory).collect(Collectors.toList());
        if (files.contains(new File("legacy/" + name + "/"))) {
            this.config = "legacy/" + name + "/";
        } else {
            this.config = "legacy/config/";
        }

        Legacy.friendManager.onLoad();
        Iterator var3 = this.features.iterator();

        while (var3.hasNext()) {
            AbstractModule feature = (AbstractModule) var3.next();

            try {
                this.loadSettings(feature);
            } catch (IOException var6) {
                var6.printStackTrace();
            }
        }

        this.saveCurrentConfig();
    }

    public boolean configExists(String name) {
        List files = (List) Arrays.stream((Object[]) Objects.requireNonNull((new File("legacy")).listFiles())).filter(File::isDirectory).collect(Collectors.toList());
        return files.contains(new File("legacy/" + name + "/"));
    }

    public void saveConfig(String name) {
        this.config = "legacy/" + name + "/";
        File path = new File(this.config);
        if (!path.exists()) {
            path.mkdir();
        }

        Legacy.friendManager.saveFriends();
        Iterator var3 = this.features.iterator();

        while (var3.hasNext()) {
            AbstractModule feature = (AbstractModule) var3.next();

            try {
                this.saveSettings(feature);
            } catch (IOException var6) {
                var6.printStackTrace();
            }
        }

        this.saveCurrentConfig();
    }

    public void saveCurrentConfig() {
        File currentConfig = new File("legacy/currentconfig.txt");

        try {
            FileWriter writer;
            String tempConfig;
            if (currentConfig.exists()) {
                writer = new FileWriter(currentConfig);
                tempConfig = this.config.replaceAll("/", "");
                writer.write(tempConfig.replaceAll("legacy", ""));
                writer.close();
            } else {
                currentConfig.createNewFile();
                writer = new FileWriter(currentConfig);
                tempConfig = this.config.replaceAll("/", "");
                writer.write(tempConfig.replaceAll("legacy", ""));
                writer.close();
            }
        } catch (Exception var4) {
            var4.printStackTrace();
        }

    }

    public String loadCurrentConfig() {
        File currentConfig = new File("legacy/currentconfig.txt");
        String name = "config";

        try {
            if (currentConfig.exists()) {
                Scanner reader;
                for (reader = new Scanner(currentConfig); reader.hasNextLine(); name = reader.nextLine()) {
                    ;
                }

                reader.close();
            }
        } catch (Exception var4) {
            var4.printStackTrace();
        }

        return name;
    }

    public void resetConfig(boolean saveConfig, String name) {
        Iterator var3 = this.features.iterator();

        while (var3.hasNext()) {
            AbstractModule feature = (AbstractModule) var3.next();
            feature.reset();
        }

        if (saveConfig) {
            this.saveConfig(name);
        }

    }

    public void saveSettings(AbstractModule feature) throws IOException {
        new JsonObject();
        File directory = new File(this.config + this.getDirectory(feature));
        if (!directory.exists()) {
            directory.mkdir();
        }

        String featureName = this.config + this.getDirectory(feature) + feature.getName() + ".json";
        Path outputFile = Paths.get(featureName);
        if (!Files.exists(outputFile, new LinkOption[0])) {
            Files.createFile(outputFile);
        }

        Gson gson = (new GsonBuilder()).setPrettyPrinting().create();
        String json = gson.toJson(this.writeSettings(feature));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(outputFile)));
        writer.write(json);
        writer.close();
    }

    public void init() {
        this.features.addAll(Legacy.moduleManager.modules);
        this.features.add(Legacy.friendManager);
        String name = this.loadCurrentConfig();
        this.loadConfig(name);
        Legacy.LOGGER.info("Config loaded.");
    }

    private void loadSettings(AbstractModule feature) throws IOException {
        String featureName = this.config + this.getDirectory(feature) + feature.getName() + ".json";
        Path featurePath = Paths.get(featureName);
        if (Files.exists(featurePath, new LinkOption[0])) {
            this.loadPath(featurePath, feature);
        }
    }

    private void loadPath(Path path, AbstractModule feature) throws IOException {
        InputStream stream = Files.newInputStream(path);

        try {
            loadFile((new JsonParser()).parse(new InputStreamReader(stream)).getAsJsonObject(), feature);
        } catch (IllegalStateException var5) {
            Legacy.LOGGER.error("Bad Config File for: " + feature.getName() + ". Resetting...");
            loadFile(new JsonObject(), feature);
        }

        stream.close();
    }

    public JsonObject writeSettings(AbstractModule feature) {
        JsonObject object = new JsonObject();
        JsonParser jp = new JsonParser();
        Iterator var4 = feature.getSettings().iterator();

        while (var4.hasNext()) {
            Setting setting = (Setting) var4.next();
            if (setting.isEnumSetting()) {
                EnumConverter converter = new EnumConverter(((Enum) setting.getValue()).getClass());
                object.add(setting.getName(), converter.doForward((Enum) setting.getValue()));
            } else {
                if (setting.isStringSetting()) {
                    String str = (String) setting.getValue();
                    setting.setValue(str.replace(" ", "_"));
                }

                try {
                    object.add(setting.getName(), jp.parse(setting.getValueAsString()));
                } catch (Exception var7) {
                    var7.printStackTrace();
                }
            }
        }

        return object;
    }

    public String getDirectory(AbstractModule feature) {
        String directory = "";
        if (feature instanceof Module) {
            directory = directory + ((Module) feature).getCategory().getName() + "/";
        }

        return directory;
    }
}
