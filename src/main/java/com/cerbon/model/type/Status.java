package com.cerbon.model.type;

/**
 * Enumeration representing different game progress statuses in the library.
 * Tracks the current state of a game from planning to play to completion,
 * including TO_PLAY, PLAYING, STOPPED, CONCLUDED, and WISHLIST states.
 */
public enum Status {
    TO_PLAY,
    PLAYING,
    STOPPED,
    CONCLUDED,
    WISHLIST;

    public static Status fromString(String value) {
        if (value == null) return WISHLIST;

        try {
            return Status.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return WISHLIST;
        }
    }
}
