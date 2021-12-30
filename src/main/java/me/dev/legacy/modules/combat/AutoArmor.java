package me.dev.legacy.modules.combat;

import me.dev.legacy.Legacy;
import me.dev.legacy.api.util.InventoryUtil;
import me.dev.legacy.api.util.Timer;
import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.modules.Module;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemExpBottle;
import net.minecraft.item.ItemStack;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AutoArmor extends Module {
    private final Setting delay = this.register(new Setting("Delay", Integer.valueOf(50), Integer.valueOf(0), Integer.valueOf(500)));
    private final Setting curse = this.register(new Setting("Vanishing", false));
    private final Setting mendingTakeOff = this.register(new Setting("AutoMend", false));
    private final Setting closestEnemy = this.register(new Setting("Enemy", Integer.valueOf(8), Integer.valueOf(1), Integer.valueOf(20), (v) -> {
        return ((Boolean) this.mendingTakeOff.getValue()).booleanValue();
    }));
    private final Setting repair = this.register(new Setting("Repair%", Integer.valueOf(80), Integer.valueOf(1), Integer.valueOf(100), (v) -> {
        return ((Boolean) this.mendingTakeOff.getValue()).booleanValue();
    }));
    private final Setting actions = this.register(new Setting("Packets", Integer.valueOf(3), Integer.valueOf(1), Integer.valueOf(12)));
    private final Timer timer = new Timer();
    private final Queue taskList = new ConcurrentLinkedQueue();
    private final List doneSlots = new ArrayList();
    boolean flag;

    public AutoArmor() {
        super("AutoArmor", "Puts Armor on for you.", Module.Category.COMBAT, true, false, false);
    }

    public void onLogin() {
        this.timer.reset();
    }

    public void onDisable() {
        this.taskList.clear();
        this.doneSlots.clear();
        this.flag = false;
    }

    public void onLogout() {
        this.taskList.clear();
        this.doneSlots.clear();
    }

    public void onTick() {
        if (!fullNullCheck() && (!(mc.field_71462_r instanceof GuiContainer) || mc.field_71462_r instanceof GuiInventory)) {
            int slot;
            if (this.taskList.isEmpty()) {
                if (((Boolean) this.mendingTakeOff.getValue()).booleanValue() && InventoryUtil.holdingItem(ItemExpBottle.class) && mc.field_71474_y.field_74313_G.func_151470_d() && mc.field_71441_e.field_73010_i.stream().noneMatch((e) -> {
                    return e != mc.field_71439_g && !Legacy.friendManager.isFriend(e.func_70005_c_()) && mc.field_71439_g.func_70032_d(e) <= (float) ((Integer) this.closestEnemy.getValue()).intValue();
                }) && !this.flag) {
                    int takeOff = 0;
                    Iterator var11 = this.getArmor().entrySet().iterator();

                    int dam;
                    ItemStack itemStack3;
                    float percent;
                    while (var11.hasNext()) {
                        Entry armorSlot = (Entry) var11.next();
                        itemStack3 = (ItemStack) armorSlot.getValue();
                        percent = (float) ((Integer) this.repair.getValue()).intValue() / 100.0F;
                        dam = Math.round((float) itemStack3.func_77958_k() * percent);
                        if (dam < itemStack3.func_77958_k() - itemStack3.func_77952_i()) {
                            ++takeOff;
                        }
                    }

                    if (takeOff == 4) {
                        this.flag = true;
                    }

                    if (!this.flag) {
                        ItemStack itemStack1 = mc.field_71439_g.field_71069_bz.func_75139_a(5).func_75211_c();
                        if (!itemStack1.field_190928_g) {
                            float percent = (float) ((Integer) this.repair.getValue()).intValue() / 100.0F;
                            int dam2 = Math.round((float) itemStack1.func_77958_k() * percent);
                            if (dam2 < itemStack1.func_77958_k() - itemStack1.func_77952_i()) {
                                this.takeOffSlot(5);
                            }
                        }

                        ItemStack itemStack2 = mc.field_71439_g.field_71069_bz.func_75139_a(6).func_75211_c();
                        if (!itemStack2.field_190928_g) {
                            percent = (float) ((Integer) this.repair.getValue()).intValue() / 100.0F;
                            int dam3 = Math.round((float) itemStack2.func_77958_k() * percent);
                            if (dam3 < itemStack2.func_77958_k() - itemStack2.func_77952_i()) {
                                this.takeOffSlot(6);
                            }
                        }

                        itemStack3 = mc.field_71439_g.field_71069_bz.func_75139_a(7).func_75211_c();
                        if (!itemStack3.field_190928_g) {
                            percent = (float) ((Integer) this.repair.getValue()).intValue() / 100.0F;
                            dam = Math.round((float) itemStack3.func_77958_k() * percent);
                            if (dam < itemStack3.func_77958_k() - itemStack3.func_77952_i()) {
                                this.takeOffSlot(7);
                            }
                        }

                        ItemStack itemStack4 = mc.field_71439_g.field_71069_bz.func_75139_a(8).func_75211_c();
                        if (!itemStack4.field_190928_g) {
                            float percent = (float) ((Integer) this.repair.getValue()).intValue() / 100.0F;
                            int dam4 = Math.round((float) itemStack4.func_77958_k() * percent);
                            if (dam4 < itemStack4.func_77958_k() - itemStack4.func_77952_i()) {
                                this.takeOffSlot(8);
                            }
                        }
                    }

                    return;
                }

                this.flag = false;
                ItemStack helm = mc.field_71439_g.field_71069_bz.func_75139_a(5).func_75211_c();
                int slot4;
                if (helm.func_77973_b() == Items.field_190931_a && (slot4 = InventoryUtil.findArmorSlot(EntityEquipmentSlot.HEAD, ((Boolean) this.curse.getValue()).booleanValue(), true)) != -1) {
                    this.getSlotOn(5, slot4);
                }

                int slot3;
                if (mc.field_71439_g.field_71069_bz.func_75139_a(6).func_75211_c().func_77973_b() == Items.field_190931_a && (slot3 = InventoryUtil.findArmorSlot(EntityEquipmentSlot.CHEST, ((Boolean) this.curse.getValue()).booleanValue(), true)) != -1) {
                    this.getSlotOn(6, slot3);
                }

                int slot2;
                if (mc.field_71439_g.field_71069_bz.func_75139_a(7).func_75211_c().func_77973_b() == Items.field_190931_a && (slot2 = InventoryUtil.findArmorSlot(EntityEquipmentSlot.LEGS, ((Boolean) this.curse.getValue()).booleanValue(), true)) != -1) {
                    this.getSlotOn(7, slot2);
                }

                if (mc.field_71439_g.field_71069_bz.func_75139_a(8).func_75211_c().func_77973_b() == Items.field_190931_a && (slot = InventoryUtil.findArmorSlot(EntityEquipmentSlot.FEET, ((Boolean) this.curse.getValue()).booleanValue(), true)) != -1) {
                    this.getSlotOn(8, slot);
                }
            }

            if (this.timer.passedMs((long) ((int) ((float) ((Integer) this.delay.getValue()).intValue() * Legacy.serverManager.getTpsFactor())))) {
                if (!this.taskList.isEmpty()) {
                    for (slot = 0; slot < ((Integer) this.actions.getValue()).intValue(); ++slot) {
                        InventoryUtil.Task task = (InventoryUtil.Task) this.taskList.poll();
                        if (task != null) {
                            task.run();
                        }
                    }
                }

                this.timer.reset();
            }

        }
    }

    private void takeOffSlot(int slot) {
        if (this.taskList.isEmpty()) {
            int target = -1;
            Iterator var3 = InventoryUtil.findEmptySlots(true).iterator();

            while (var3.hasNext()) {
                int i = ((Integer) var3.next()).intValue();
                if (!this.doneSlots.contains(target)) {
                    target = i;
                    this.doneSlots.add(i);
                }
            }

            if (target != -1) {
                this.taskList.add(new InventoryUtil.Task(slot));
                this.taskList.add(new InventoryUtil.Task(target));
                this.taskList.add(new InventoryUtil.Task());
            }
        }

    }

    private void getSlotOn(int slot, int target) {
        if (this.taskList.isEmpty()) {
            this.doneSlots.remove(target);
            this.taskList.add(new InventoryUtil.Task(target));
            this.taskList.add(new InventoryUtil.Task(slot));
            this.taskList.add(new InventoryUtil.Task());
        }

    }

    private Map getArmor() {
        return this.getInventorySlots(5, 8);
    }

    private Map getInventorySlots(int current, int last) {
        HashMap fullInventorySlots;
        for (fullInventorySlots = new HashMap(); current <= last; ++current) {
            fullInventorySlots.put(current, mc.field_71439_g.field_71069_bz.func_75138_a().get(current));
        }

        return fullInventorySlots;
    }
}
