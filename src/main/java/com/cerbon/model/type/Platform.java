package com.cerbon.model.type;

/**
 * Enumeration representing different gaming platforms supported by the library.
 * Includes major gaming platforms like PC, PlayStation, Xbox, Nintendo Switch,
 * mobile devices, and a fallback OTHER option for unrecognized platforms.
 */
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
