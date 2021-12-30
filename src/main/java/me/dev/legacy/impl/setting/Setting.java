package me.dev.legacy.impl.setting;

import me.dev.legacy.api.AbstractModule;
import me.dev.legacy.api.event.ClientEvent;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;

import java.util.function.Predicate;

public class Setting {
    private final String name;
    private final Object defaultValue;
    private Object value;
    private Object plannedValue;
    private Object min;
    private Object max;
    private boolean hasRestriction;
    private Predicate visibility;
    private String description;
    private AbstractModule feature;
    private int key;

    public Setting(String name, Object defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
        this.plannedValue = defaultValue;
        this.description = "";
    }

    public Setting(String name, Object defaultValue, String description) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
        this.plannedValue = defaultValue;
        this.description = description;
    }

    public Setting(String name, Object defaultValue, Object min, Object max, String description) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
        this.min = min;
        this.max = max;
        this.plannedValue = defaultValue;
        this.description = description;
        this.hasRestriction = true;
    }

    public Setting(String name, Object defaultValue, Object min, Object max) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
        this.min = min;
        this.max = max;
        this.plannedValue = defaultValue;
        this.description = "";
        this.hasRestriction = true;
    }

    public Setting(String name, Object defaultValue, Object min, Object max, Predicate visibility, String description) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
        this.min = min;
        this.max = max;
        this.plannedValue = defaultValue;
        this.visibility = visibility;
        this.description = description;
        this.hasRestriction = true;
    }

    public Setting(String name, Object defaultValue, Object min, Object max, Predicate visibility) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
        this.min = min;
        this.max = max;
        this.plannedValue = defaultValue;
        this.visibility = visibility;
        this.description = "";
        this.hasRestriction = true;
    }

    public Setting(String name, Object defaultValue, Predicate visibility) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
        this.visibility = visibility;
        this.plannedValue = defaultValue;
    }

    public String getName() {
        return this.name;
    }

    public Object getValue() {
        return this.value;
    }

    public void setValue(Object value) {
        this.setPlannedValue(value);
        if (this.hasRestriction) {
            if (((Number) this.min).floatValue() > ((Number) value).floatValue()) {
                this.setPlannedValue(this.min);
            }

            if (((Number) this.max).floatValue() < ((Number) value).floatValue()) {
                this.setPlannedValue(this.max);
            }
        }

        ClientEvent event = new ClientEvent(this);
        MinecraftForge.EVENT_BUS.post(event);
        if (!event.isCanceled()) {
            this.value = this.plannedValue;
        } else {
            this.plannedValue = this.value;
        }

    }

    public Object getPlannedValue() {
        return this.plannedValue;
    }

    public void setPlannedValue(Object value) {
        this.plannedValue = value;
    }

    public Object getMin() {
        return this.min;
    }

    public void setMin(Object min) {
        this.min = min;
    }

    public Object getMax() {
        return this.max;
    }

    public void setMax(Object max) {
        this.max = max;
    }

    public void setValueNoEvent(Object value) {
        this.setPlannedValue(value);
        if (this.hasRestriction) {
            if (((Number) this.min).floatValue() > ((Number) value).floatValue()) {
                this.setPlannedValue(this.min);
            }

            if (((Number) this.max).floatValue() < ((Number) value).floatValue()) {
                this.setPlannedValue(this.max);
            }
        }

        this.value = this.plannedValue;
    }

    public AbstractModule getFeature() {
        return this.feature;
    }

    public void setFeature(AbstractModule feature) {
        this.feature = feature;
    }

    public int getEnum(String input) {
        for (int i = 0; i < this.value.getClass().getEnumConstants().length; ++i) {
            Enum e = (Enum) this.value.getClass().getEnumConstants()[i];
            if (e.name().equalsIgnoreCase(input)) {
                return i;
            }
        }

        return -1;
    }

    public void setEnumValue(String value) {
        Enum[] var2 = (Enum[]) ((Enum[]) ((Enum) this.value).getClass().getEnumConstants());
        int var3 = var2.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            Enum e = var2[var4];
            if (e.name().equalsIgnoreCase(value)) {
                this.value = e;
            }
        }

    }

    public String currentEnumName() {
        return EnumConverter.getProperName((Enum) this.value);
    }

    public int currentEnum() {
        return EnumConverter.currentEnum((Enum) this.value);
    }

    public void increaseEnum() {
        this.plannedValue = EnumConverter.increaseEnum((Enum) this.value);
        ClientEvent event = new ClientEvent(this);
        MinecraftForge.EVENT_BUS.post(event);
        if (!event.isCanceled()) {
            this.value = this.plannedValue;
        } else {
            this.plannedValue = this.value;
        }

    }

    public void increaseEnumNoEvent() {
        this.value = EnumConverter.increaseEnum((Enum) this.value);
    }

    public String getType() {
        return this.isEnumSetting() ? "Enum" : this.getClassName(this.defaultValue);
    }

    public String getClassName(Object value) {
        return value.getClass().getSimpleName();
    }

    public String getDescription() {
        return this.description == null ? "" : this.description;
    }

    public boolean isNumberSetting() {
        return this.value instanceof Double || this.value instanceof Integer || this.value instanceof Short || this.value instanceof Long || this.value instanceof Float;
    }

    public boolean isEnumSetting() {
        return !this.isNumberSetting() && !(this.value instanceof String) && !(this.value instanceof Bind) && !(this.value instanceof Character) && !(this.value instanceof Boolean);
    }

    public boolean isStringSetting() {
        return this.value instanceof String;
    }

    public Object getDefaultValue() {
        return this.defaultValue;
    }

    public String getValueAsString() {
        return this.value.toString();
    }

    public boolean hasRestriction() {
        return this.hasRestriction;
    }

    public void setVisibility(Predicate visibility) {
        this.visibility = visibility;
    }

    public boolean isVisible() {
        return this.visibility == null ? true : this.visibility.test(this.getValue());
    }

    public boolean is(String off) {
        return false;
    }

    public int getKey() {
        return this.key;
    }

    public boolean isEmpty() {
        return this.key < 0;
    }

    public boolean isDown() {
        return !this.isEmpty() && Keyboard.isKeyDown(this.getKey());
    }
}
