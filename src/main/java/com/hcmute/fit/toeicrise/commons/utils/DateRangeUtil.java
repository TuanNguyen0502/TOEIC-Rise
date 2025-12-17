package com.hcmute.fit.toeicrise.commons.utils;

import com.hcmute.fit.toeicrise.dtos.responses.DateRange;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class DateRangeUtil {
    public static DateRange previousPeriod(LocalDate startDate, LocalDate endDate) {
        long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;

        LocalDate previousTo = startDate.minusDays(1);
        LocalDate previousFrom = startDate.minusDays(days-1);

        return new DateRange(previousFrom, previousTo);
    }
}
