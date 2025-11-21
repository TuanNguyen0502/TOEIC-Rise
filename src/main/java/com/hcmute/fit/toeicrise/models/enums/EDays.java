package com.hcmute.fit.toeicrise.models.enums;

import lombok.Getter;

import java.time.Duration;

@Getter
public enum EDays {
    ONE_MONTH("1 Month", 30),
    THREE_MONTHS("3 Months", 90),
    SIX_MONTHS("6 Months", 180),
    ONE_YEAR("1 Year", 365),
    TWO_YEARS("2 Years", 720),
    THREE_YEARS("3 Years", 1095);

    private final String name;
    private final int days;
    EDays(String name, int days) {
        this.name = name;
        this.days = days;
    }
}
