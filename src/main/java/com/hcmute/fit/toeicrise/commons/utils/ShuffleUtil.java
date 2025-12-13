package com.hcmute.fit.toeicrise.commons.utils;

import java.util.List;
import java.util.Random;

public class ShuffleUtil {
    private static final ThreadLocal<Random> RANDOM = ThreadLocal.withInitial(Random::new);

    public static <T> void shuffle(List<T> list, int k) {
        if (list == null || list.size() < k)
            return;

        Random random = RANDOM.get();
        int n = list.size();
        k = Math.min(k, n);

        for (int i = 0; i < k; i++) {
            int index = i + random.nextInt(n - i);

            T temp = list.get(i);
            list.set(i, list.get(index));
            list.set(index, temp);
        }
    }

    public static <T> void shuffle(List<T> list) {
        if (list == null || list.isEmpty()) {
            return;
        }

        Random random = RANDOM.get();
        int n = list.size();

        for (int i = n - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            // Swap elements at i and j
            T temp = list.get(i);
            list.set(i, list.get(j));
            list.set(j, temp);
        }
    }
}
