package com.hcmute.fit.toeicrise.models.enums;

import lombok.Getter;

@Getter
public enum ESort {
    CREATED_AT("createdAt"),
    UPDATED_AT("updatedAt");

    private String value;

    ESort(String value) {
        this.value = value;
    }
}
