package com.cerbon.model.type;

/**
 * Enumeration representing different game genres/genders available in the library.
 * Includes common game categories like ACTION, RPG, STRATEGY, and others,
 * with a fallback OTHER option for unrecognized genres.
 */
public enum Gender {
    ACTION,
    ADVENTURE,
    RPG,
    STRATEGY,
    SPORTS,
    SIMULATION,
    PUZZLE,
    HORROR,
    PLATFORMER,
    RACING,
    OTHER;

    public static Gender fromString(String value) {
        if (value == null) return OTHER;

        try {
            return Gender.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return OTHER;
        }
    }
}
