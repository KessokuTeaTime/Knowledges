package net.krlite.knowledges.core.util;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
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

    public static class Text {
        public static @NotNull MutableText withFormatting(MutableText text, Formatting... formattings) {
            return text.styled(style -> style.withFormatting(formattings));
        }

        public static @NotNull MutableText withFormatting(String string, Formatting... formattings) {
            return withFormatting(net.minecraft.text.Text.literal(string), formattings);
        }

        public static @NotNull MutableText withFormatting(net.minecraft.text.Text text, Formatting... formattings) {
            return withFormatting(text.copy(), formattings);
        }

        public static Optional<MutableText> combineToMultiline(@Nullable net.minecraft.text.MutableText... texts) {
            return Stream.of(texts)
                    .filter(Objects::nonNull)
                    .reduce((p, n) -> p.append("\n").append(n));
        }
    }
}
