package me.doggy.justguard.flag;

import org.checkerframework.checker.nullness.qual.Nullable;

public class FlagValue {

    private Object value;

    public FlagValue(@Nullable Object value) {
        setValue(value);
    }

    public static FlagValue parse(@Nullable String str) {
        if(str == null)
            return new FlagValue(null);

        String lower = str.toLowerCase();

        Object value;
        if(lower.equals("true"))
            value = Boolean.TRUE;
        else if(lower.equals("false"))
            value = Boolean.FALSE;
        else
            value = str;

        return new FlagValue(value);
    }

    public Boolean setValue(@Nullable Object value)
    {
        if(value == null || !(value instanceof Boolean || value instanceof String)) {
            this.value = null;
            return false;
        } else {
            this.value = value;
            return true;
        }
    }

    public Boolean isEmpty() {
        return value == null;
    }

    @Nullable
    public Object getValue() {
        return value;
    }

    @Nullable
    public Boolean getBoolean(@Nullable Boolean def) {
        if(value == null)
            return def;
        return value instanceof Boolean ? (Boolean) value : def;
    }
    @Nullable
    public Boolean getBoolean() {
        return this.getBoolean(null);
    }

    @Nullable
    public String getString(@Nullable String def) {
        if(value == null)
            return def;
        return value instanceof String ? (String) value : def;
    }
    @Nullable
    public String getString() {
        return this.getString(null);
    }

    @Override
    public String toString() {
        return this.getClass().getName() + "(" + (value == null ? "null" : value.toString()) + ")";
    }

}
