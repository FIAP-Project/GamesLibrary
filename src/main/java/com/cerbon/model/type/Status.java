package com.cerbon.model.type;

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
