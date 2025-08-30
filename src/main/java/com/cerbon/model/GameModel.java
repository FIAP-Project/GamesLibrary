package com.cerbon.model;

import com.cerbon.model.type.Gender;
import com.cerbon.model.type.Platform;
import com.cerbon.model.type.Status;

public record GameModel(
        int id,
        String title,
        Gender gender,
        Platform platform,
        int year,
        Status status,
        int rate) {
}
