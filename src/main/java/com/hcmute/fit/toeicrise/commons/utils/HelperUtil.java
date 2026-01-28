package com.hcmute.fit.toeicrise.commons.utils;

import com.hcmute.fit.toeicrise.models.entities.Part;
import com.hcmute.fit.toeicrise.models.entities.QuestionGroup;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class HelperUtil {
    public static <T> List<T> groupByPartAndMap(List<QuestionGroup> groups, BiFunction<Part,
            List<QuestionGroup>, T> mapper, Comparator<T> comparator) {
        return groups.stream().collect(Collectors.groupingBy(QuestionGroup::getPart))
                .entrySet().stream().map(entry -> mapper.apply(entry.getKey(), entry.getValue()))
                .sorted(comparator).toList();
    }

    public static int initialAverageValue(List<Integer> value){
        return value.isEmpty() ? 0 :
                (int) value.stream().mapToInt(Integer::intValue).average().orElse(0.0);
    }

    public static int initialMaxValue(List<Integer> value){
        return value.isEmpty() ? 0 :
                value.stream().mapToInt(Integer::intValue).max().orElse(0);
    }
}
