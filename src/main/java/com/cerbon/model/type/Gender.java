package com.cerbon.model.type;

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
