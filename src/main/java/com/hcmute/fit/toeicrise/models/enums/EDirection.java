package com.hcmute.fit.toeicrise.models.enums;

import lombok.Getter;

@Getter
public enum EDirection {
    ASC("asc"),
    DES("desc");

    private String value;

    EDirection(String value) {
        this.value = value;
    }
}