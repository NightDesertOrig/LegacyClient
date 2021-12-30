package me.dev.legacy.modules.player;

import me.dev.legacy.api.event.ClientEvent;
import me.dev.legacy.api.event.events.other.PacketEvent;
import me.dev.legacy.api.util.InventoryUtil;
import me.dev.legacy.api.util.ReflectionUtil;
import me.dev.legacy.api.util.Util;
import me.dev.legacy.impl.command.Command;
import me.dev.legacy.impl.gui.LegacyGui;
import me.dev.legacy.impl.setting.Bind;
import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.modules.Module;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class XCarry extends Module {
    private final Setting simpleMode = this.register(new Setting("Simple", false));
    private final Setting autoStore = this.register(new Setting("AutoDuel", new Bind(-1)));
    private final Setting obbySlot = this.register(new Setting("ObbySlot", Integer.valueOf(2), Integer.valueOf(1), Integer.valueOf(9), (v) -> {
        return ((Bind) this.autoStore.getValue()).getKey() != -1;
    }));
    private final Setting slot1 = this.register(new Setting("Slot1", Integer.valueOf(22), Integer.valueOf(9), Integer.valueOf(44), (v) -> {
        return ((Bind) this.autoStore.getValue()).getKey() != -1;
    }));
    private final Setting slot2 = this.register(new Setting("Slot2", Integer.valueOf(23), Integer.valueOf(9), Integer.valueOf(44), (v) -> {
        return ((Bind) this.autoStore.getValue()).getKey() != -1;
    }));
    private final Setting slot3 = this.register(new Setting("Slot3", Integer.valueOf(24), Integer.valueOf(9), Integer.valueOf(44), (v) -> {
        return ((Bind) this.autoStore.getValue()).getKey() != -1;
    }));
    private final Setting tasks = this.register(new Setting("Actions", Integer.valueOf(3), Integer.valueOf(1), Integer.valueOf(12), (v) -> {
        return ((Bind) this.autoStore.getValue()).getKey() != -1;
    }));
    private final Setting store = this.register(new Setting("Store", false));
    private final Setting shiftClicker = this.register(new Setting("ShiftClick", false));
    private final Setting withShift = this.register(new Setting("WithShift", true, (v) -> {
        return ((Boolean) this.shiftClicker.getValue()).booleanValue();
    }));
    private final Setting keyBind = this.register(new Setting("ShiftBind", new Bind(-1), (v) -> {
        return ((Boolean) this.shiftClicker.getValue()).booleanValue();
    }));
    private static XCarry INSTANCE = new XCarry();
    private GuiInventory openedGui = null;
    private final AtomicBoolean guiNeedsClose = new AtomicBoolean(false);
    private boolean guiCloseGuard = false;
    private boolean autoDuelOn = false;
    private final Queue taskList = new ConcurrentLinkedQueue();
    private boolean obbySlotDone = false;
    private boolean slot1done = false;
    private boolean slot2done = false;
    private boolean slot3done = false;
    private List doneSlots = new ArrayList();

    public XCarry() {
        super("XCarry", "Uses the crafting inventory for storage", Module.Category.PLAYER, true, false, false);
        this.setInstance();
    }

    private void setInstance() {
        INSTANCE = this;
    }

    public static XCarry getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new XCarry();
        }

        return INSTANCE;
    }

    public void onUpdate() {
        if (((Boolean) this.shiftClicker.getValue()).booleanValue() && mc.field_71462_r instanceof GuiInventory) {
            boolean ourBind = ((Bind) this.keyBind.getValue()).getKey() != -1 && Keyboard.isKeyDown(((Bind) this.keyBind.getValue()).getKey()) && !Keyboard.isKeyDown(42);
            Slot slot;
            if ((Keyboard.isKeyDown(42) && ((Boolean) this.withShift.getValue()).booleanValue() || ourBind) && Mouse.isButtonDown(0) && (slot = ((GuiInventory) mc.field_71462_r).getSlotUnderMouse()) != null && InventoryUtil.getEmptyXCarry() != -1) {
                int slotNumber = slot.field_75222_d;
                if (slotNumber > 4 && ourBind) {
                    this.taskList.add(new InventoryUtil.Task(slotNumber));
                    this.taskList.add(new InventoryUtil.Task(InventoryUtil.getEmptyXCarry()));
                } else if (slotNumber > 4 && ((Boolean) this.withShift.getValue()).booleanValue()) {
                    boolean isHotBarFull = true;
                    boolean isInvFull = true;
                    Iterator var7 = InventoryUtil.findEmptySlots(false).iterator();

                    while (true) {
                        while (var7.hasNext()) {
                            int i = ((Integer) var7.next()).intValue();
                            if (i > 4 && i < 36) {
                                isInvFull = false;
                            } else if (i > 35 && i < 45) {
                                isHotBarFull = false;
                            }
                        }

                        if (slotNumber > 35 && slotNumber < 45) {
                            if (isInvFull) {
                                this.taskList.add(new InventoryUtil.Task(slotNumber));
                                this.taskList.add(new InventoryUtil.Task(InventoryUtil.getEmptyXCarry()));
                            }
                        } else if (isHotBarFull) {
                            this.taskList.add(new InventoryUtil.Task(slotNumber));
                            this.taskList.add(new InventoryUtil.Task(InventoryUtil.getEmptyXCarry()));
                        }
                        break;
                    }
                }
            }
        }

        if (this.autoDuelOn) {
            this.doneSlots = new ArrayList();
            if (InventoryUtil.getEmptyXCarry() == -1 || this.obbySlotDone && this.slot1done && this.slot2done && this.slot3done) {
                this.autoDuelOn = false;
            }

            if (this.autoDuelOn) {
                if (!this.obbySlotDone && !mc.field_71439_g.field_71071_by.func_70301_a(((Integer) this.obbySlot.getValue()).intValue() - 1).field_190928_g) {
                    this.addTasks(36 + ((Integer) this.obbySlot.getValue()).intValue() - 1);
                }

                this.obbySlotDone = true;
                if (!this.slot1done && !((Slot) mc.field_71439_g.field_71069_bz.field_75151_b.get(((Integer) this.slot1.getValue()).intValue())).func_75211_c().field_190928_g) {
                    this.addTasks(((Integer) this.slot1.getValue()).intValue());
                }

                this.slot1done = true;
                if (!this.slot2done && !((Slot) mc.field_71439_g.field_71069_bz.field_75151_b.get(((Integer) this.slot2.getValue()).intValue())).func_75211_c().field_190928_g) {
                    this.addTasks(((Integer) this.slot2.getValue()).intValue());
                }

                this.slot2done = true;
                if (!this.slot3done && !((Slot) mc.field_71439_g.field_71069_bz.field_75151_b.get(((Integer) this.slot3.getValue()).intValue())).func_75211_c().field_190928_g) {
                    this.addTasks(((Integer) this.slot3.getValue()).intValue());
                }

                this.slot3done = true;
            }
        } else {
            this.obbySlotDone = false;
            this.slot1done = false;
            this.slot2done = false;
            this.slot3done = false;
        }

        if (!this.taskList.isEmpty()) {
            for (int i = 0; i < ((Integer) this.tasks.getValue()).intValue(); ++i) {
                InventoryUtil.Task task = (InventoryUtil.Task) this.taskList.poll();
                if (task != null) {
                    task.run();
                }
            }
        }

    }

    private void addTasks(int slot) {
        if (InventoryUtil.getEmptyXCarry() != -1) {
            int xcarrySlot = InventoryUtil.getEmptyXCarry();
            if (this.doneSlots.contains(xcarrySlot) || !InventoryUtil.isSlotEmpty(xcarrySlot)) {
                ++xcarrySlot;
                if (this.doneSlots.contains(xcarrySlot) || !InventoryUtil.isSlotEmpty(xcarrySlot)) {
                    ++xcarrySlot;
                    if (this.doneSlots.contains(xcarrySlot) || !InventoryUtil.isSlotEmpty(xcarrySlot)) {
                        ++xcarrySlot;
                        if (this.doneSlots.contains(xcarrySlot) || !InventoryUtil.isSlotEmpty(xcarrySlot)) {
                            return;
                        }
                    }
                }
            }

            if (xcarrySlot > 4) {
                return;
            }

            this.doneSlots.add(xcarrySlot);
            this.taskList.add(new InventoryUtil.Task(slot));
            this.taskList.add(new InventoryUtil.Task(xcarrySlot));
            this.taskList.add(new InventoryUtil.Task());
        }

    }

    public void onDisable() {
        if (!fullNullCheck()) {
            if (!((Boolean) this.simpleMode.getValue()).booleanValue()) {
                this.closeGui();
                this.close();
            } else {
                mc.field_71439_g.field_71174_a.func_147297_a(new CPacketCloseWindow(mc.field_71439_g.field_71069_bz.field_75152_c));
            }
        }

    }

    public void onLogout() {
        this.onDisable();
    }

    @SubscribeEvent
    public void onCloseGuiScreen(PacketEvent.Send event) {
        if (((Boolean) this.simpleMode.getValue()).booleanValue() && event.getPacket() instanceof CPacketCloseWindow) {
            CPacketCloseWindow packet = (CPacketCloseWindow) event.getPacket();
            if (packet.field_149556_a == mc.field_71439_g.field_71069_bz.field_75152_c) {
                event.setCanceled(true);
            }
        }

    }

    @SubscribeEvent(
            priority = EventPriority.LOWEST
    )
    public void onGuiOpen(GuiOpenEvent event) {
        if (!((Boolean) this.simpleMode.getValue()).booleanValue()) {
            if (this.guiCloseGuard) {
                event.setCanceled(true);
            } else if (event.getGui() instanceof GuiInventory) {
                this.openedGui = this.createGuiWrapper((GuiInventory) event.getGui());
                event.setGui(this.openedGui);
                this.guiNeedsClose.set(false);
            }
        }

    }

    @SubscribeEvent
    public void onSettingChange(ClientEvent event) {
        if (event.getStage() == 2 && event.getSetting() != null && event.getSetting().getFeature() != null && event.getSetting().getFeature().equals(this)) {
            Setting setting = event.getSetting();
            String settingname = event.getSetting().getName();
            if (setting.equals(this.simpleMode) && setting.getPlannedValue() != setting.getValue()) {
                this.disable();
            } else if (settingname.equalsIgnoreCase("Store")) {
                event.setCanceled(true);
                this.autoDuelOn = !this.autoDuelOn;
                Command.sendMessage("<XCarry> §aAutostoring...");
            }
        }

    }

    @SubscribeEvent
    public void onKeyInput(KeyInputEvent event) {
        if (Keyboard.getEventKeyState() && !(mc.field_71462_r instanceof LegacyGui) && ((Bind) this.autoStore.getValue()).getKey() == Keyboard.getEventKey()) {
            this.autoDuelOn = !this.autoDuelOn;
            Command.sendMessage("<XCarry> §aAutostoring...");
        }

    }

    private void close() {
        this.openedGui = null;
        this.guiNeedsClose.set(false);
        this.guiCloseGuard = false;
    }

    private void closeGui() {
        if (this.guiNeedsClose.compareAndSet(true, false) && !fullNullCheck()) {
            this.guiCloseGuard = true;
            mc.field_71439_g.func_71053_j();
            if (this.openedGui != null) {
                this.openedGui.func_146281_b();
                this.openedGui = null;
            }

            this.guiCloseGuard = false;
        }

    }

    private GuiInventory createGuiWrapper(GuiInventory gui) {
        try {
            XCarry.GuiInventoryWrapper wrapper = new XCarry.GuiInventoryWrapper();
            ReflectionUtil.copyOf(gui, wrapper);
            return wrapper;
        } catch (NoSuchFieldException | IllegalAccessException var3) {
            var3.printStackTrace();
            return null;
        }
    }

    private class GuiInventoryWrapper extends GuiInventory {
        GuiInventoryWrapper() {
            super(Util.mc.field_71439_g);
        }

        protected void func_73869_a(char typedChar, int keyCode) throws IOException {
            if (!XCarry.this.isEnabled() || keyCode != 1 && !this.field_146297_k.field_71474_y.field_151445_Q.isActiveAndMatches(keyCode)) {
                super.func_73869_a(typedChar, keyCode);
            } else {
                XCarry.this.guiNeedsClose.set(true);
                this.field_146297_k.func_147108_a((GuiScreen) null);
            }

        }

        public void func_146281_b() {
            if (XCarry.this.guiCloseGuard || !XCarry.this.isEnabled()) {
                super.func_146281_b();
            }

        }
    }
}
