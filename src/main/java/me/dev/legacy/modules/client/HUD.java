package me.dev.legacy.modules.client;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.dev.legacy.Legacy;
import me.dev.legacy.api.event.ClientEvent;
import me.dev.legacy.api.event.events.render.Render2DEvent;
import me.dev.legacy.api.util.Timer;
import me.dev.legacy.api.util.*;
import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.modules.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.text.SimpleDateFormat;
import java.util.*;

public class HUD extends Module {
    private static final ResourceLocation box = new ResourceLocation("textures/gui/container/shulker_box.png");
    private static final ItemStack totem;
    private static RenderItem itemRender;
    private static HUD INSTANCE;
    private final Setting grayNess = this.register(new Setting("Gray", false));
    private final Setting renderingUp = this.register(new Setting("RenderingUp", false, "Orientation of the HUD-Elements."));
    private final Setting waterMark = this.register(new Setting("Watermark", false, "displays watermark"));
    public Setting waterMarkY = this.register(new Setting("WatermarkPosY", Integer.valueOf(2), Integer.valueOf(0), Integer.valueOf(100), (v) -> {
        return ((Boolean) this.waterMark.getValue()).booleanValue();
    }));
    private final Setting arrayList = this.register(new Setting("ActiveModules", false, "Lists the active modules."));
    private final Setting coords = this.register(new Setting("Coords", false, "Your current coordinates"));
    private final Setting direction = this.register(new Setting("Direction", false, "The Direction you are facing."));
    private final Setting armor = this.register(new Setting("Armor", false, "ArmorHUD"));
    private final Setting totems = this.register(new Setting("Totems", false, "TotemHUD"));
    private final Setting greeter = this.register(new Setting("Welcomer", false, "The time"));
    private final Setting speed = this.register(new Setting("Speed", false, "Your Speed"));
    private final Setting potions = this.register(new Setting("Potions", false, "Your Speed"));
    private final Setting ping = this.register(new Setting("Ping", false, "Your response time to the server."));
    private final Setting line = this.register(new Setting("RainbowBar", false, "Cs moode."));
    public Setting lineoffset = this.register(new Setting("Offset", Integer.valueOf(1000), Integer.valueOf(0), Integer.valueOf(1000), (v) -> {
        return ((Boolean) this.line.getValue()).booleanValue();
    }));
    private final Setting tps = this.register(new Setting("TPS", false, "Ticks per second of the server."));
    private final Setting fps = this.register(new Setting("FPS", false, "Your frames per second."));
    private final Timer timer = new Timer();
    private final Map players = new HashMap();
    public Setting command = this.register(new Setting("Prefix", "legacy"));
    public Setting commandBracket = this.register(new Setting("Bracket", "["));
    public Setting commandBracket2 = this.register(new Setting("Bracket2", "]"));
    public Setting bracketColor;
    public Setting commandColor;
    public Setting notifyToggles;
    public Setting animationHorizontalTime;
    public Setting animationVerticalTime;
    public Setting renderingMode;
    public Setting time;
    private int color;
    public float hue;
    private boolean shouldIncrement;
    private int hitMarkerTimer;

    public HUD() {
        super("HUD", "HUD Elements rendered on your screen", Module.Category.CLIENT, true, false, false);
        this.bracketColor = this.register(new Setting("BracketColor", TextUtil.Color.WHITE));
        this.commandColor = this.register(new Setting("NameColor", TextUtil.Color.LIGHT_PURPLE));
        this.notifyToggles = this.register(new Setting("ChatNotify", false, "notifys in chat"));
        this.animationHorizontalTime = this.register(new Setting("AnimationHTime", Integer.valueOf(500), Integer.valueOf(1), Integer.valueOf(1000), (v) -> {
            return ((Boolean) this.arrayList.getValue()).booleanValue();
        }));
        this.animationVerticalTime = this.register(new Setting("AnimationVTime", Integer.valueOf(50), Integer.valueOf(1), Integer.valueOf(500), (v) -> {
            return ((Boolean) this.arrayList.getValue()).booleanValue();
        }));
        this.renderingMode = this.register(new Setting("Ordering", HUD.RenderingMode.Length));
        this.time = this.register(new Setting("Time", false, "The time"));
        this.setInstance();
    }

    public static HUD getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HUD();
        }

        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    public void onUpdate() {
        if (this.shouldIncrement) {
            ++this.hitMarkerTimer;
        }

        if (this.hitMarkerTimer == 10) {
            this.hitMarkerTimer = 0;
            this.shouldIncrement = false;
        }

    }

    public void onRender2D(Render2DEvent event) {
        if (!fullNullCheck()) {
            int width = this.renderer.scaledWidth;
            int height = this.renderer.scaledHeight;
            this.color = ColorUtil.toRGBA(((Integer) ClickGui.getInstance().red.getValue()).intValue(), ((Integer) ClickGui.getInstance().green.getValue()).intValue(), ((Integer) ClickGui.getInstance().blue.getValue()).intValue());
            int posX;
            int posY;
            if (((Boolean) this.waterMark.getValue()).booleanValue()) {
                String string = "legacy v1.2.5";
                if (((Boolean) ClickGui.getInstance().rainbow.getValue()).booleanValue()) {
                    if (ClickGui.getInstance().rainbowModeHud.getValue() == ClickGui.rainbowMode.Static) {
                        this.renderer.drawString(string, 2.0F, (float) ((Integer) this.waterMarkY.getValue()).intValue(), ColorUtil.rainbow(((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB(), true);
                    } else {
                        int[] arrayOfInt = new int[]{1};
                        char[] stringToCharArray = string.toCharArray();
                        float f = 0.0F;
                        char[] var8 = stringToCharArray;
                        posX = stringToCharArray.length;

                        for (posY = 0; posY < posX; ++posY) {
                            char c = var8[posY];
                            this.renderer.drawString(String.valueOf(c), 2.0F + f, (float) ((Integer) this.waterMarkY.getValue()).intValue(), ColorUtil.rainbow(arrayOfInt[0] * ((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB(), true);
                            f += (float) this.renderer.getStringWidth(String.valueOf(c));
                            ++arrayOfInt[0];
                        }
                    }
                } else {
                    this.renderer.drawString(string, 2.0F, (float) ((Integer) this.waterMarkY.getValue()).intValue(), this.color, true);
                }
            }

            int[] counter1 = new int[]{1};
            int j = mc.field_71462_r instanceof GuiChat && !((Boolean) this.renderingUp.getValue()).booleanValue() ? 14 : 0;
            String fpsText;
            if (((Boolean) this.arrayList.getValue()).booleanValue()) {
                int k;
                String str;
                Module module;
                if (((Boolean) this.renderingUp.getValue()).booleanValue()) {
                    if (this.renderingMode.getValue() == HUD.RenderingMode.ABC) {
                        for (k = 0; k < Legacy.moduleManager.sortedModulesABC.size(); ++k) {
                            str = (String) Legacy.moduleManager.sortedModulesABC.get(k);
                            this.renderer.drawString(str, (float) (width - 2 - this.renderer.getStringWidth(str)), (float) (2 + j * 10), ((Boolean) ClickGui.getInstance().rainbow.getValue()).booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB() : ColorUtil.rainbow(((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB()) : this.color, true);
                            ++j;
                            ++counter1[0];
                        }
                    } else {
                        for (k = 0; k < Legacy.moduleManager.sortedModules.size(); ++k) {
                            module = (Module) Legacy.moduleManager.sortedModules.get(k);
                            fpsText = module.getDisplayName() + ChatFormatting.GRAY + (module.getDisplayInfo() != null ? " [" + ChatFormatting.WHITE + module.getDisplayInfo() + ChatFormatting.GRAY + "]" : "");
                            this.renderer.drawString(fpsText, (float) (width - 2 - this.renderer.getStringWidth(fpsText)), (float) (2 + j * 10), ((Boolean) ClickGui.getInstance().rainbow.getValue()).booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB() : ColorUtil.rainbow(((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB()) : this.color, true);
                            ++j;
                            ++counter1[0];
                        }
                    }
                } else if (this.renderingMode.getValue() == HUD.RenderingMode.ABC) {
                    for (k = 0; k < Legacy.moduleManager.sortedModulesABC.size(); ++k) {
                        str = (String) Legacy.moduleManager.sortedModulesABC.get(k);
                        j += 10;
                        this.renderer.drawString(str, (float) (width - 2 - this.renderer.getStringWidth(str)), (float) (height - j), ((Boolean) ClickGui.getInstance().rainbow.getValue()).booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB() : ColorUtil.rainbow(((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB()) : this.color, true);
                        ++counter1[0];
                    }
                } else {
                    for (k = 0; k < Legacy.moduleManager.sortedModules.size(); ++k) {
                        module = (Module) Legacy.moduleManager.sortedModules.get(k);
                        fpsText = module.getDisplayName() + ChatFormatting.GRAY + (module.getDisplayInfo() != null ? " [" + ChatFormatting.WHITE + module.getDisplayInfo() + ChatFormatting.GRAY + "]" : "");
                        j += 10;
                        this.renderer.drawString(fpsText, (float) (width - 2 - this.renderer.getStringWidth(fpsText)), (float) (height - j), ((Boolean) ClickGui.getInstance().rainbow.getValue()).booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB() : ColorUtil.rainbow(((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB()) : this.color, true);
                        ++counter1[0];
                    }
                }
            }

            String grayString = ((Boolean) this.grayNess.getValue()).booleanValue() ? String.valueOf(ChatFormatting.GRAY) : "";
            int i = mc.field_71462_r instanceof GuiChat && ((Boolean) this.renderingUp.getValue()).booleanValue() ? 13 : (((Boolean) this.renderingUp.getValue()).booleanValue() ? -2 : 0);
            ArrayList effects;
            Iterator var38;
            byte padding;
            String str1;
            PotionEffect potionEffect;
            String str;
            if (((Boolean) this.renderingUp.getValue()).booleanValue()) {
                if (((Boolean) this.potions.getValue()).booleanValue()) {
                    effects = new ArrayList(Minecraft.func_71410_x().field_71439_g.func_70651_bq());
                    var38 = effects.iterator();

                    while (var38.hasNext()) {
                        potionEffect = (PotionEffect) var38.next();
                        str = Legacy.potionManager.getColoredPotionString(potionEffect);
                        i += 10;
                        this.renderer.drawString(str, (float) (width - this.renderer.getStringWidth(str) - 2), (float) (height - 2 - i), potionEffect.func_188419_a().func_76401_j(), true);
                    }
                }

                if (((Boolean) this.speed.getValue()).booleanValue()) {
                    fpsText = grayString + "Speed " + ChatFormatting.WHITE + Legacy.speedManager.getSpeedKpH() + " km/h";
                    i += 10;
                    this.renderer.drawString(fpsText, (float) (width - this.renderer.getStringWidth(fpsText) - 2), (float) (height - 2 - i), ((Boolean) ClickGui.getInstance().rainbow.getValue()).booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB() : ColorUtil.rainbow(((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB()) : this.color, true);
                    ++counter1[0];
                }

                if (((Boolean) this.line.getValue()).booleanValue()) {
                    padding = 5;
                    RenderUtil.drawHLineG(0 - padding, 5 - padding, 1000 - padding, RainbowUtil.getColour().hashCode(), RainbowUtil.getFurtherColour(((Integer) this.lineoffset.getValue()).intValue()).hashCode());
                }

                if (((Boolean) this.time.getValue()).booleanValue()) {
                    fpsText = grayString + "Time " + ChatFormatting.WHITE + (new SimpleDateFormat("h:mm a")).format(new Date());
                    i += 10;
                    this.renderer.drawString(fpsText, (float) (width - this.renderer.getStringWidth(fpsText) - 2), (float) (height - 2 - i), ((Boolean) ClickGui.getInstance().rainbow.getValue()).booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB() : ColorUtil.rainbow(((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB()) : this.color, true);
                    ++counter1[0];
                }

                if (((Boolean) this.tps.getValue()).booleanValue()) {
                    fpsText = grayString + "TPS " + ChatFormatting.WHITE + Legacy.serverManager.getTPS();
                    i += 10;
                    this.renderer.drawString(fpsText, (float) (width - this.renderer.getStringWidth(fpsText) - 2), (float) (height - 2 - i), ((Boolean) ClickGui.getInstance().rainbow.getValue()).booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB() : ColorUtil.rainbow(((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB()) : this.color, true);
                    ++counter1[0];
                }

                fpsText = grayString + "FPS " + ChatFormatting.WHITE + Minecraft.field_71470_ab;
                str1 = grayString + "Ping " + ChatFormatting.WHITE + Legacy.serverManager.getPing();
                if (this.renderer.getStringWidth(str1) > this.renderer.getStringWidth(fpsText)) {
                    if (((Boolean) this.ping.getValue()).booleanValue()) {
                        i += 10;
                        this.renderer.drawString(str1, (float) (width - this.renderer.getStringWidth(str1) - 2), (float) (height - 2 - i), ((Boolean) ClickGui.getInstance().rainbow.getValue()).booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB() : ColorUtil.rainbow(((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB()) : this.color, true);
                        ++counter1[0];
                    }

                    if (((Boolean) this.fps.getValue()).booleanValue()) {
                        i += 10;
                        this.renderer.drawString(fpsText, (float) (width - this.renderer.getStringWidth(fpsText) - 2), (float) (height - 2 - i), ((Boolean) ClickGui.getInstance().rainbow.getValue()).booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB() : ColorUtil.rainbow(((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB()) : this.color, true);
                        ++counter1[0];
                    }
                } else {
                    if (((Boolean) this.fps.getValue()).booleanValue()) {
                        i += 10;
                        this.renderer.drawString(fpsText, (float) (width - this.renderer.getStringWidth(fpsText) - 2), (float) (height - 2 - i), ((Boolean) ClickGui.getInstance().rainbow.getValue()).booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB() : ColorUtil.rainbow(((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB()) : this.color, true);
                        ++counter1[0];
                    }

                    if (((Boolean) this.ping.getValue()).booleanValue()) {
                        i += 10;
                        this.renderer.drawString(str1, (float) (width - this.renderer.getStringWidth(str1) - 2), (float) (height - 2 - i), ((Boolean) ClickGui.getInstance().rainbow.getValue()).booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB() : ColorUtil.rainbow(((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB()) : this.color, true);
                        ++counter1[0];
                    }
                }
            } else {
                if (((Boolean) this.line.getValue()).booleanValue()) {
                    padding = 5;
                    RenderUtil.drawHLineG(0 - padding, 5 - padding, 1000 - padding, RainbowUtil.getColour().hashCode(), RainbowUtil.getFurtherColour(((Integer) this.lineoffset.getValue()).intValue()).hashCode());
                }

                if (((Boolean) this.potions.getValue()).booleanValue()) {
                    effects = new ArrayList(Minecraft.func_71410_x().field_71439_g.func_70651_bq());
                    var38 = effects.iterator();

                    while (var38.hasNext()) {
                        potionEffect = (PotionEffect) var38.next();
                        str = Legacy.potionManager.getColoredPotionString(potionEffect);
                        this.renderer.drawString(str, (float) (width - this.renderer.getStringWidth(str) - 2), (float) (2 + i++ * 10), potionEffect.func_188419_a().func_76401_j(), true);
                    }
                }

                if (((Boolean) this.speed.getValue()).booleanValue()) {
                    fpsText = grayString + "Speed " + ChatFormatting.WHITE + Legacy.speedManager.getSpeedKpH() + " km/h";
                    this.renderer.drawString(fpsText, (float) (width - this.renderer.getStringWidth(fpsText) - 2), (float) (2 + i++ * 10), ((Boolean) ClickGui.getInstance().rainbow.getValue()).booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB() : ColorUtil.rainbow(((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB()) : this.color, true);
                    ++counter1[0];
                }

                if (((Boolean) this.time.getValue()).booleanValue()) {
                    fpsText = grayString + "Time " + ChatFormatting.WHITE + (new SimpleDateFormat("h:mm a")).format(new Date());
                    this.renderer.drawString(fpsText, (float) (width - this.renderer.getStringWidth(fpsText) - 2), (float) (2 + i++ * 10), ((Boolean) ClickGui.getInstance().rainbow.getValue()).booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB() : ColorUtil.rainbow(((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB()) : this.color, true);
                    ++counter1[0];
                }

                if (((Boolean) this.tps.getValue()).booleanValue()) {
                    fpsText = grayString + "TPS " + ChatFormatting.WHITE + Legacy.serverManager.getTPS();
                    this.renderer.drawString(fpsText, (float) (width - this.renderer.getStringWidth(fpsText) - 2), (float) (2 + i++ * 10), ((Boolean) ClickGui.getInstance().rainbow.getValue()).booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB() : ColorUtil.rainbow(((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB()) : this.color, true);
                    ++counter1[0];
                }

                fpsText = grayString + "FPS " + ChatFormatting.WHITE + Minecraft.field_71470_ab;
                str1 = grayString + "Ping " + ChatFormatting.WHITE + Legacy.serverManager.getPing();
                if (this.renderer.getStringWidth(str1) > this.renderer.getStringWidth(fpsText)) {
                    if (((Boolean) this.ping.getValue()).booleanValue()) {
                        this.renderer.drawString(str1, (float) (width - this.renderer.getStringWidth(str1) - 2), (float) (2 + i++ * 10), ((Boolean) ClickGui.getInstance().rainbow.getValue()).booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB() : ColorUtil.rainbow(((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB()) : this.color, true);
                        ++counter1[0];
                    }

                    if (((Boolean) this.fps.getValue()).booleanValue()) {
                        this.renderer.drawString(fpsText, (float) (width - this.renderer.getStringWidth(fpsText) - 2), (float) (2 + i++ * 10), ((Boolean) ClickGui.getInstance().rainbow.getValue()).booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB() : ColorUtil.rainbow(((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB()) : this.color, true);
                        ++counter1[0];
                    }
                } else {
                    if (((Boolean) this.fps.getValue()).booleanValue()) {
                        this.renderer.drawString(fpsText, (float) (width - this.renderer.getStringWidth(fpsText) - 2), (float) (2 + i++ * 10), ((Boolean) ClickGui.getInstance().rainbow.getValue()).booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB() : ColorUtil.rainbow(((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB()) : this.color, true);
                        ++counter1[0];
                    }

                    if (((Boolean) this.ping.getValue()).booleanValue()) {
                        this.renderer.drawString(str1, (float) (width - this.renderer.getStringWidth(str1) - 2), (float) (2 + i++ * 10), ((Boolean) ClickGui.getInstance().rainbow.getValue()).booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB() : ColorUtil.rainbow(((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB()) : this.color, true);
                        ++counter1[0];
                    }
                }
            }

            boolean inHell = mc.field_71441_e.func_180494_b(mc.field_71439_g.func_180425_c()).func_185359_l().equals("Hell");
            posX = (int) mc.field_71439_g.field_70165_t;
            posY = (int) mc.field_71439_g.field_70163_u;
            int posZ = (int) mc.field_71439_g.field_70161_v;
            float nether = !inHell ? 0.125F : 8.0F;
            int hposX = (int) (mc.field_71439_g.field_70165_t * (double) nether);
            int hposZ = (int) (mc.field_71439_g.field_70161_v * (double) nether);
            i = mc.field_71462_r instanceof GuiChat ? 14 : 0;
            String coordinates = ChatFormatting.WHITE + "XYZ " + ChatFormatting.RESET + (inHell ? posX + ", " + posY + ", " + posZ + ChatFormatting.WHITE + " [" + ChatFormatting.RESET + hposX + ", " + hposZ + ChatFormatting.WHITE + "]" + ChatFormatting.RESET : posX + ", " + posY + ", " + posZ + ChatFormatting.WHITE + " [" + ChatFormatting.RESET + hposX + ", " + hposZ + ChatFormatting.WHITE + "]");
            String direction = ((Boolean) this.direction.getValue()).booleanValue() ? Legacy.rotationManager.getDirection4D(false) : "";
            String coords = ((Boolean) this.coords.getValue()).booleanValue() ? coordinates : "";
            i += 10;
            if (((Boolean) ClickGui.getInstance().rainbow.getValue()).booleanValue()) {
                String rainbowCoords = ((Boolean) this.coords.getValue()).booleanValue() ? "XYZ " + (inHell ? posX + ", " + posY + ", " + posZ + " [" + hposX + ", " + hposZ + "]" : posX + ", " + posY + ", " + posZ + " [" + hposX + ", " + hposZ + "]") : "";
                if (ClickGui.getInstance().rainbowModeHud.getValue() == ClickGui.rainbowMode.Static) {
                    this.renderer.drawString(direction, 2.0F, (float) (height - i - 11), ColorUtil.rainbow(((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB(), true);
                    this.renderer.drawString(rainbowCoords, 2.0F, (float) (height - i), ColorUtil.rainbow(((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB(), true);
                } else {
                    int[] counter2 = new int[]{1};
                    char[] stringToCharArray = direction.toCharArray();
                    float s = 0.0F;
                    char[] var22 = stringToCharArray;
                    int var23 = stringToCharArray.length;

                    for (int var24 = 0; var24 < var23; ++var24) {
                        char c = var22[var24];
                        this.renderer.drawString(String.valueOf(c), 2.0F + s, (float) (height - i - 11), ColorUtil.rainbow(counter2[0] * ((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB(), true);
                        s += (float) this.renderer.getStringWidth(String.valueOf(c));
                        ++counter2[0];
                    }

                    int[] counter3 = new int[]{1};
                    char[] stringToCharArray2 = rainbowCoords.toCharArray();
                    float u = 0.0F;
                    char[] var48 = stringToCharArray2;
                    int var26 = stringToCharArray2.length;

                    for (int var27 = 0; var27 < var26; ++var27) {
                        char c = var48[var27];
                        this.renderer.drawString(String.valueOf(c), 2.0F + u, (float) (height - i), ColorUtil.rainbow(counter3[0] * ((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB(), true);
                        u += (float) this.renderer.getStringWidth(String.valueOf(c));
                        ++counter3[0];
                    }
                }
            } else {
                this.renderer.drawString(direction, 2.0F, (float) (height - i - 11), this.color, true);
                this.renderer.drawString(coords, 2.0F, (float) (height - i), this.color, true);
            }

            if (((Boolean) this.armor.getValue()).booleanValue()) {
                this.renderArmorHUD(true);
            }

            if (((Boolean) this.totems.getValue()).booleanValue()) {
                this.renderTotemHUD();
            }

            if (((Boolean) this.greeter.getValue()).booleanValue()) {
                this.renderGreeter();
            }

        }
    }

    public Map getTextRadarPlayers() {
        return EntityUtil.getTextRadarPlayers();
    }

    public void renderGreeter() {
        int width = this.renderer.scaledWidth;
        String text = "looking good today, ";
        if (((Boolean) this.greeter.getValue()).booleanValue()) {
            text = text + mc.field_71439_g.getDisplayNameString() + " <3";
        }

        if (((Boolean) ClickGui.getInstance().rainbow.getValue()).booleanValue()) {
            if (ClickGui.getInstance().rainbowModeHud.getValue() == ClickGui.rainbowMode.Static) {
                this.renderer.drawString(text, (float) width / 2.0F - (float) this.renderer.getStringWidth(text) / 2.0F + 2.0F, 2.0F, ColorUtil.rainbow(((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB(), true);
            } else {
                int[] counter1 = new int[]{1};
                char[] stringToCharArray = text.toCharArray();
                float i = 0.0F;
                char[] var6 = stringToCharArray;
                int var7 = stringToCharArray.length;

                for (int var8 = 0; var8 < var7; ++var8) {
                    char c = var6[var8];
                    this.renderer.drawString(String.valueOf(c), (float) width / 2.0F - (float) this.renderer.getStringWidth(text) / 2.0F + 2.0F + i, 2.0F, ColorUtil.rainbow(counter1[0] * ((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB(), true);
                    i += (float) this.renderer.getStringWidth(String.valueOf(c));
                    ++counter1[0];
                }
            }
        } else {
            this.renderer.drawString(text, (float) width / 2.0F - (float) this.renderer.getStringWidth(text) / 2.0F + 2.0F, 2.0F, this.color, true);
        }

    }

    public void renderTotemHUD() {
        int width = this.renderer.scaledWidth;
        int height = this.renderer.scaledHeight;
        int totems = mc.field_71439_g.field_71071_by.field_70462_a.stream().filter((itemStack) -> {
            return itemStack.func_77973_b() == Items.field_190929_cY;
        }).mapToInt(ItemStack::func_190916_E).sum();
        if (mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_190929_cY) {
            totems += mc.field_71439_g.func_184592_cb().func_190916_E();
        }

        if (totems > 0) {
            GlStateManager.func_179098_w();
            int i = width / 2;
            int iteration = false;
            int y = height - 55 - (mc.field_71439_g.func_70090_H() && mc.field_71442_b.func_78763_f() ? 10 : 0);
            int x = i - 189 + 180 + 2;
            GlStateManager.func_179126_j();
            RenderUtil.itemRender.field_77023_b = 200.0F;
            RenderUtil.itemRender.func_180450_b(totem, x, y);
            RenderUtil.itemRender.func_180453_a(mc.field_71466_p, totem, x, y, "");
            RenderUtil.itemRender.field_77023_b = 0.0F;
            GlStateManager.func_179098_w();
            GlStateManager.func_179140_f();
            GlStateManager.func_179097_i();
            this.renderer.drawStringWithShadow(totems + "", (float) (x + 19 - 2 - this.renderer.getStringWidth(totems + "")), (float) (y + 9), 16777215);
            GlStateManager.func_179126_j();
            GlStateManager.func_179140_f();
        }

    }

    public void renderCrystal() {
        int width = this.renderer.scaledWidth;
        int height = this.renderer.scaledHeight;
        int crystals = mc.field_71439_g.field_71071_by.field_70462_a.stream().filter((itemStack) -> {
            return itemStack.func_77973_b() == Items.field_185158_cP;
        }).mapToInt(ItemStack::func_190916_E).sum();
        if (mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_185158_cP) {
            crystals += mc.field_71439_g.func_184592_cb().func_190916_E();
        }

        if (crystals > 0) {
            GlStateManager.func_179098_w();
            int i = width / 2;
            int iteration = false;
            int y = height - 60 - (mc.field_71439_g.func_70090_H() && mc.field_71442_b.func_78763_f() ? 10 : 0);
            int x = i - 189 + 180 + 2;
            GlStateManager.func_179126_j();
            RenderUtil.itemRender.field_77023_b = 200.0F;
            RenderUtil.itemRender.func_180450_b(totem, x, y);
            RenderUtil.itemRender.func_180453_a(mc.field_71466_p, totem, x, y, "");
            RenderUtil.itemRender.field_77023_b = 0.0F;
            GlStateManager.func_179098_w();
            GlStateManager.func_179140_f();
            GlStateManager.func_179097_i();
            this.renderer.drawStringWithShadow(crystals + "", (float) (x + 19 - 2 - this.renderer.getStringWidth(crystals + "")), (float) (y + 9), 16777215);
            GlStateManager.func_179126_j();
            GlStateManager.func_179140_f();
        }

    }

    public void renderArmorHUD(boolean percent) {
        int width = this.renderer.scaledWidth;
        int height = this.renderer.scaledHeight;
        GlStateManager.func_179098_w();
        int i = width / 2;
        int iteration = 0;
        int y = height - 55 - (mc.field_71439_g.func_70090_H() && mc.field_71442_b.func_78763_f() ? 10 : 0);
        Iterator var7 = mc.field_71439_g.field_71071_by.field_70460_b.iterator();

        while (var7.hasNext()) {
            ItemStack is = (ItemStack) var7.next();
            ++iteration;
            if (!is.func_190926_b()) {
                int x = i - 90 + (9 - iteration) * 20 + 2;
                GlStateManager.func_179126_j();
                RenderUtil.itemRender.field_77023_b = 200.0F;
                RenderUtil.itemRender.func_180450_b(is, x, y);
                RenderUtil.itemRender.func_180453_a(mc.field_71466_p, is, x, y, "");
                RenderUtil.itemRender.field_77023_b = 0.0F;
                GlStateManager.func_179098_w();
                GlStateManager.func_179140_f();
                GlStateManager.func_179097_i();
                String s = is.func_190916_E() > 1 ? is.func_190916_E() + "" : "";
                this.renderer.drawStringWithShadow(s, (float) (x + 19 - 2 - this.renderer.getStringWidth(s)), (float) (y + 9), 16777215);
                if (percent) {
                    int dmg = false;
                    int itemDurability = is.func_77958_k() - is.func_77952_i();
                    float green = ((float) is.func_77958_k() - (float) is.func_77952_i()) / (float) is.func_77958_k();
                    float red = 1.0F - green;
                    int dmg;
                    if (percent) {
                        dmg = 100 - (int) (red * 100.0F);
                    } else {
                        dmg = itemDurability;
                    }

                    this.renderer.drawStringWithShadow(dmg + "", (float) (x + 8 - this.renderer.getStringWidth(dmg + "") / 2), (float) (y - 11), ColorUtil.toRGBA((int) (red * 255.0F), (int) (green * 255.0F), 0));
                }
            }
        }

        GlStateManager.func_179126_j();
        GlStateManager.func_179140_f();
    }

    public void onLoad() {
        Legacy.commandManager.setClientMessage(this.getCommandMessage());
    }

    @SubscribeEvent
    public void onSettingChange(ClientEvent event) {
        if (event.getStage() == 2 && this.equals(event.getSetting().getFeature())) {
            Legacy.commandManager.setClientMessage(this.getCommandMessage());
        }

    }

    public String getCommandMessage() {
        return TextUtil.coloredString((String) this.commandBracket.getPlannedValue(), (TextUtil.Color) this.bracketColor.getPlannedValue()) + TextUtil.coloredString((String) this.command.getPlannedValue(), (TextUtil.Color) this.commandColor.getPlannedValue()) + TextUtil.coloredString((String) this.commandBracket2.getPlannedValue(), (TextUtil.Color) this.bracketColor.getPlannedValue());
    }

    public String getRainbowCommandMessage() {
        StringBuilder stringBuilder = new StringBuilder(this.getRawCommandMessage());
        stringBuilder.insert(0, "§+");
        stringBuilder.append("§r");
        return stringBuilder.toString();
    }

    public String getRawCommandMessage() {
        return (String) this.commandBracket.getValue() + (String) this.command.getValue() + (String) this.commandBracket2.getValue();
    }

    static {
        totem = new ItemStack(Items.field_190929_cY);
        INSTANCE = new HUD();
    }

    public static enum RenderingMode {
        Length,
        ABC;
    }
}
