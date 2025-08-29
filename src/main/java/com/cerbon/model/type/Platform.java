package com.cerbon.model.type;

public enum Platform {
    PC,
    PLAYSTATION,
    XBOX,
    SWITCH,
    MOBILE,
    OTHER;

    public static Platform fromString(String value) {
        if (value == null) return OTHER;

        try {
            return Platform.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return OTHER;
        }
    }

}
