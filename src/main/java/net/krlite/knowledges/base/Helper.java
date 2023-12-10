package net.krlite.knowledges.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Stream;

public class Helper {
    public static class Map {
        public static <K, V> java.util.List<V> fastMerge(HashMap<K, java.util.List<V>> hashMap, K key, V defaultValue) {
            return hashMap.merge(
                    key,
                    new ArrayList<>(java.util.List.of(defaultValue)),
                    (l1, l2) -> Stream.concat(l1.stream(), l2.stream()).toList()
            );
        }
    }

    public static class Math {
        public static double mapToPower(double x, double power, double threshold) {
            return threshold + (1 - threshold) * java.lang.Math.pow(x, power);
        }
    }
}
