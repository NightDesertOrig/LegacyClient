package me.dev.legacy.modules;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.dev.legacy.Legacy;
import me.dev.legacy.api.AbstractModule;
import me.dev.legacy.api.event.events.render.Render2DEvent;
import me.dev.legacy.api.event.events.render.Render3DEvent;
import me.dev.legacy.api.util.Util;
import me.dev.legacy.impl.gui.LegacyGui;
import me.dev.legacy.modules.client.*;
import me.dev.legacy.modules.combat.*;
import me.dev.legacy.modules.misc.*;
import me.dev.legacy.modules.movement.*;
import me.dev.legacy.modules.player.*;
import me.dev.legacy.modules.render.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import org.lwjgl.input.Keyboard;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ModuleManager extends AbstractModule {
    public ArrayList modules = new ArrayList();
    public List sortedModules = new ArrayList();
    public List sortedModulesABC = new ArrayList();
    public ModuleManager.Animation animationThread;
    private static final LinkedHashMap modulesNameMap = new LinkedHashMap();
    private static final LinkedHashMap modulesClassMap = new LinkedHashMap();
    public static ArrayList nigger;

    public void init() {
        this.modules.add(new ClickGui());
        this.modules.add(new HUD());
        this.modules.add(new Media());
        this.modules.add(new Watermark());
        this.modules.add(new ModulesList());
        this.modules.add(new Friends());
        this.modules.add(new Colors());
        this.modules.add(new TabFriends());
        this.modules.add(new Components());
        this.modules.add(new RPC());
        this.modules.add(new BlockHighlight());
        this.modules.add(new Trajectories());
        this.modules.add(new NoRender());
        this.modules.add(new SkyColor());
        this.modules.add(new ESP());
        this.modules.add(new Fullbright());
        this.modules.add(new HoleESP());
        this.modules.add(new Tracers());
        this.modules.add(new HitMarkers());
        this.modules.add(new Swing());
        this.modules.add(new ItemViewModel());
        this.modules.add(new EntityHunger());
        this.modules.add(new Aspect());
        this.modules.add(new CrystalChams());
        this.modules.add(new PlayerChams());
        this.modules.add(new SmallShield());
        this.modules.add(new Nametags());
        this.modules.add(new BetterXP());
        this.modules.add(new Crits());
        this.modules.add(new Offhand());
        this.modules.add(new AutoWeb());
        this.modules.add(new HoleFiller());
        this.modules.add(new AutoArmor());
        this.modules.add(new Burrow());
        this.modules.add(new AutoCrystal());
        this.modules.add(new AutoLog());
        this.modules.add(new MinDamage());
        this.modules.add(new Quiver());
        this.modules.add(new Aura());
        this.modules.add(new AutoTrap());
        this.modules.add(new AntiRegear());
        this.modules.add(new Surround());
        this.modules.add(new BowAim());
        this.modules.add(new AutoBed());
        this.modules.add(new AntiCity());
        this.modules.add(new SmartBurrow());
        this.modules.add(new AutoCity());
        this.modules.add(new AntiHunger());
        this.modules.add(new Freecam());
        this.modules.add(new FastPlace());
        this.modules.add(new Replenish());
        this.modules.add(new FakePlayer());
        this.modules.add(new MCP());
        this.modules.add(new LiquidInteract());
        this.modules.add(new TpsSync());
        this.modules.add(new MultiTask());
        this.modules.add(new Reach());
        this.modules.add(new FastBreak());
        this.modules.add(new Blink());
        this.modules.add(new Scaffold());
        this.modules.add(new Timestamps());
        this.modules.add(new BuildHeight());
        this.modules.add(new MCF());
        this.modules.add(new ShulkerViewer());
        this.modules.add(new Tracker());
        this.modules.add(new PopCounter());
        this.modules.add(new XCarry());
        this.modules.add(new Dupe());
        this.modules.add(new PearlNotify());
        this.modules.add(new BurrowAlert());
        this.modules.add(new NoHitBox());
        this.modules.add(new StashLogger());
        this.modules.add(new AutoBebra());
        this.modules.add(new AutoKit());
        this.modules.add(new PortalBuilder());
        this.modules.add(new AntiVoid());
        this.modules.add(new NoSlow());
        this.modules.add(new ReverseStep());
        this.modules.add(new Velocity());
        this.modules.add(new Sprint());
        this.modules.add(new Step());
        this.modules.add(new Jesus());
        this.modules.add(new Speed());
        this.modules.add(new IceSpeed());
        this.modules.add(new Rubberband());
        this.modules.add(new NoWeb());
        this.modules.add(new Anchor());
        this.modules.add(new HoleTP());
        this.modules.add(new AntiLevitate());
        this.modules.add(new PacketFly());
        this.modules.add(new Strafe());
        this.modules.add(new Phase());
        this.modules.add(new BoatFly());
        this.modules.add(new ElytraFlight());
    }

    public Module getModuleByName(String name) {
        Iterator var2 = this.modules.iterator();

        Module module;
        do {
            if (!var2.hasNext()) {
                return null;
            }

            module = (Module) var2.next();
        } while (!module.getName().equalsIgnoreCase(name));

        return module;
    }

    public Module getModuleByClass(Class clazz) {
        Iterator var2 = this.modules.iterator();

        Module module;
        do {
            if (!var2.hasNext()) {
                return null;
            }

            module = (Module) var2.next();
        } while (!clazz.isInstance(module));

        return module;
    }

    public void enableModule(Class clazz) {
        Module module = this.getModuleByClass(clazz);
        if (module != null) {
            module.enable();
        }

    }

    public void disableModule(Class clazz) {
        Module module = this.getModuleByClass(clazz);
        if (module != null) {
            module.disable();
        }

    }

    public void enableModule(String name) {
        Module module = this.getModuleByName(name);
        if (module != null) {
            module.enable();
        }

    }

    public void disableModule(String name) {
        Module module = this.getModuleByName(name);
        if (module != null) {
            module.disable();
        }

    }

    public Module getModuleByDisplayName(String displayName) {
        Iterator var2 = this.modules.iterator();

        Module module;
        do {
            if (!var2.hasNext()) {
                return null;
            }

            module = (Module) var2.next();
        } while (!module.getDisplayName().equalsIgnoreCase(displayName));

        return module;
    }

    public ArrayList getEnabledModules() {
        ArrayList enabledModules = new ArrayList();
        Iterator var2 = this.modules.iterator();

        while (var2.hasNext()) {
            Module module = (Module) var2.next();
            if (module.isEnabled()) {
                enabledModules.add(module);
            }
        }

        return enabledModules;
    }

    public ArrayList getEnabledModulesName() {
        ArrayList enabledModules = new ArrayList();
        Iterator var2 = this.modules.iterator();

        while (var2.hasNext()) {
            Module module = (Module) var2.next();
            if (module.isEnabled() && module.isDrawn()) {
                enabledModules.add(module.getFullArrayString());
            }
        }

        return enabledModules;
    }

    public ArrayList getModulesByCategory(Module.Category category) {
        ArrayList modulesCategory = new ArrayList();
        this.modules.forEach((module) -> {
            if (module.getCategory() == category) {
                modulesCategory.add(module);
            }

        });
        return modulesCategory;
    }

    public List getCategories() {
        return Arrays.asList(Module.Category.values());
    }

    public void onLoad() {
        Stream var10000 = this.modules.stream().filter(Module::listening);
        EventBus var10001 = MinecraftForge.EVENT_BUS;
        MinecraftForge.EVENT_BUS.getClass();
        var10000.forEach(var10001::register);
        this.modules.forEach(Module::onLoad);
    }

    public void onUpdate() {
        this.modules.stream().filter(AbstractModule::isEnabled).forEach(Module::onUpdate);
    }

    public void onTick() {
        this.modules.stream().filter(AbstractModule::isEnabled).forEach(Module::onTick);
    }

    public void onRender2D(Render2DEvent event) {
        this.modules.stream().filter(AbstractModule::isEnabled).forEach((module) -> {
            module.onRender2D(event);
        });
    }

    public void onRender3D(Render3DEvent event) {
        this.modules.stream().filter(AbstractModule::isEnabled).forEach((module) -> {
            module.onRender3D(event);
        });
    }

    public Module getModuleT(Class clazz) {
        return (Module) this.modules.stream().filter((module) -> {
            return module.getClass() == clazz;
        }).map((module) -> {
            return module;
        }).findFirst().orElse((Object) null);
    }

    public void sortModules(boolean reverse) {
        this.sortedModules = (List) this.getEnabledModules().stream().filter(Module::isDrawn).sorted(Comparator.comparing((module) -> {
            return this.renderer.getStringWidth(module.getFullArrayString()) * (reverse ? -1 : 1);
        })).collect(Collectors.toList());
    }

    public void sortModulesABC() {
        this.sortedModulesABC = new ArrayList(this.getEnabledModulesName());
        this.sortedModulesABC.sort(String.CASE_INSENSITIVE_ORDER);
    }

    public void onLogout() {
        this.modules.forEach(Module::onLogout);
    }

    public void onLogin() {
        this.modules.forEach(Module::onLogin);
    }

    public void onUnload() {
        ArrayList var10000 = this.modules;
        EventBus var10001 = MinecraftForge.EVENT_BUS;
        MinecraftForge.EVENT_BUS.getClass();
        var10000.forEach(var10001::unregister);
        this.modules.forEach(Module::onUnload);
    }

    public void onUnloadPost() {
        Iterator var1 = this.modules.iterator();

        while (var1.hasNext()) {
            Module module = (Module) var1.next();
            module.enabled.setValue(false);
        }

    }

    public void onKeyPressed(int eventKey) {
        if (eventKey != 0 && Keyboard.getEventKeyState() && !(mc.field_71462_r instanceof LegacyGui)) {
            this.modules.forEach((module) -> {
                if (module.getBind().getKey() == eventKey) {
                    module.toggle();
                }

            });
        }
    }

    public static ArrayList getModules() {
        return nigger;
    }

    public static boolean isModuleEnablednigger(String name) {
        Module modulenigger = (Module) getModules().stream().filter((mm) -> {
            return mm.getName().equalsIgnoreCase(name);
        }).findFirst().orElse((Object) null);
        return modulenigger.isEnabled();
    }

    public static boolean isModuleEnablednigger(Module modulenigger) {
        return modulenigger.isEnabled();
    }

    public static Module getModule(Class clazz) {
        return (Module) modulesClassMap.get(clazz);
    }

    public static Module getModule(String name) {
        return name == null ? null : (Module) modulesNameMap.get(name.toLowerCase(Locale.ROOT));
    }

    public static boolean isModuleEnabled(Class clazz) {
        Module module = getModule(clazz);
        return module != null && module.isEnabled();
    }

    public static boolean isModuleEnabled(String name) {
        Module module = getModule(name);
        return module != null && module.isEnabled();
    }

    private class Animation extends Thread {
        public Module module;
        public float offset;
        public float vOffset;
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

        public Animation() {
            super("Animation");
        }

        public void run() {
            Iterator var1;
            if (HUD.getInstance().renderingMode.getValue() == HUD.RenderingMode.Length) {
                var1 = ModuleManager.this.sortedModules.iterator();

                while (true) {
                    while (var1.hasNext()) {
                        Module modulex = (Module) var1.next();
                        String text = modulex.getDisplayName() + ChatFormatting.GRAY + (modulex.getDisplayInfo() != null ? " [" + ChatFormatting.WHITE + modulex.getDisplayInfo() + ChatFormatting.GRAY + "]" : "");
                        modulex.offset = (float) ModuleManager.this.renderer.getStringWidth(text) / ((Integer) HUD.getInstance().animationHorizontalTime.getValue()).floatValue();
                        modulex.vOffset = (float) ModuleManager.this.renderer.getFontHeight() / ((Integer) HUD.getInstance().animationVerticalTime.getValue()).floatValue();
                        if (modulex.isEnabled() && ((Integer) HUD.getInstance().animationHorizontalTime.getValue()).intValue() != 1) {
                            if (modulex.arrayListOffset > modulex.offset && Util.mc.field_71441_e != null) {
                                modulex.arrayListOffset -= modulex.offset;
                                modulex.sliding = true;
                            }
                        } else if (modulex.isDisabled() && ((Integer) HUD.getInstance().animationHorizontalTime.getValue()).intValue() != 1) {
                            if (modulex.arrayListOffset < (float) ModuleManager.this.renderer.getStringWidth(text) && Util.mc.field_71441_e != null) {
                                modulex.arrayListOffset += modulex.offset;
                                modulex.sliding = true;
                            } else {
                                modulex.sliding = false;
                            }
                        }
                    }

                    return;
                }
            } else {
                var1 = ModuleManager.this.sortedModulesABC.iterator();

                while (true) {
                    while (var1.hasNext()) {
                        String e = (String) var1.next();
                        Module module = Legacy.moduleManager.getModuleByName(e);
                        String textx = module.getDisplayName() + ChatFormatting.GRAY + (module.getDisplayInfo() != null ? " [" + ChatFormatting.WHITE + module.getDisplayInfo() + ChatFormatting.GRAY + "]" : "");
                        module.offset = (float) ModuleManager.this.renderer.getStringWidth(textx) / ((Integer) HUD.getInstance().animationHorizontalTime.getValue()).floatValue();
                        module.vOffset = (float) ModuleManager.this.renderer.getFontHeight() / ((Integer) HUD.getInstance().animationVerticalTime.getValue()).floatValue();
                        if (module.isEnabled() && ((Integer) HUD.getInstance().animationHorizontalTime.getValue()).intValue() != 1) {
                            if (module.arrayListOffset > module.offset && Util.mc.field_71441_e != null) {
                                module.arrayListOffset -= module.offset;
                                module.sliding = true;
                            }
                        } else if (module.isDisabled() && ((Integer) HUD.getInstance().animationHorizontalTime.getValue()).intValue() != 1) {
                            if (module.arrayListOffset < (float) ModuleManager.this.renderer.getStringWidth(textx) && Util.mc.field_71441_e != null) {
                                module.arrayListOffset += module.offset;
                                module.sliding = true;
                            } else {
                                module.sliding = false;
                            }
                        }
                    }

                    return;
                }
            }
        }

        public void start() {
            System.out.println("Starting animation thread.");
            this.service.scheduleAtFixedRate(this, 0L, 1L, TimeUnit.MILLISECONDS);
        }
    }
}
