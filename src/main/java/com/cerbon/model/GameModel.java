package com.cerbon.model;

import com.cerbon.model.type.Gender;
import com.cerbon.model.type.Platform;
import com.cerbon.model.type.Status;

/**
 * Record class representing a game entity with all its properties.
 * Contains information about a game including its title, genre, platform,
 * release year, current status, and user rating.
 */
public record GameModel(
        int id,
        String title,
        Gender gender,
        Platform platform,
        int year,
        Status status,
        int rate) {
}
