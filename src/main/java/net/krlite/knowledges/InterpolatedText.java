package net.krlite.knowledges;

import net.krlite.equator.visual.animation.interpolated.InterpolatedDouble;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class InterpolatedText {
    private final InterpolatedDouble width = new InterpolatedDouble(0, 0.02);
    private final Alignment alignment;
    private final long delay;

    public enum Alignment {
        LEFT((letters, width) -> {
            ArrayList<Character> result = new ArrayList<>();

            ListIterator<Character> iterator = letters.listIterator(0);
            int currentWidth = 0;
            while (iterator.hasNext()) {
                Character letter = iterator.next();
                int letterWidth = widthOf(String.valueOf(letter));

                if (currentWidth + letterWidth > width + 1) break;

                currentWidth += letterWidth;
                result.add(letter);
            }

            return result;
        }),
        RIGHT((letters, width) -> {
            ArrayList<Character> result = new ArrayList<>();

            ListIterator<Character> iterator = letters.listIterator(letters.size());
            int currentWidth = 0;
            while (iterator.hasPrevious()) {
                Character letter = iterator.previous();
                int letterWidth = widthOf(String.valueOf(letter));

                if (currentWidth + letterWidth > width + 1) break;

                currentWidth += letterWidth;
                result.add(0, letter);
            }

            return result;
        });

        final BiFunction<ArrayList<Character>, Double, ArrayList<Character>> truncateFunction;

        Alignment(BiFunction<ArrayList<Character>, Double, ArrayList<Character>> truncateFunction) {
            this.truncateFunction = truncateFunction;
        }

        public ArrayList<MutableText> truncate(ArrayList<Character> letters, double width) {
            return truncateFunction.apply(letters, width).stream()
                    .map(String::valueOf)
                    .map(Text::literal)
                    .collect(Collectors.toCollection(ArrayList::new));
        }
    }

    public InterpolatedText(Alignment alignment, long delay) {
        this.alignment = alignment;
        this.delay = delay;
    }

    public InterpolatedText(Alignment alignment) {
        this(alignment, 0);
    }

    protected static int widthOf(String string) {
        return MinecraftClient.getInstance().textRenderer.getWidth(string);
    }

    private String last = "", current = "", cache = current;
    private Style style = Style.EMPTY;

    public MutableText text() {
        ArrayList<Character> letters = new ArrayList<>();

        final int maxLength = Math.max(last.length(), current.length());
        for (int i = 0; i < maxLength; i++) {
            switch (alignment) {
                case LEFT -> {
                    if (i < current.length()) letters.add(current.toCharArray()[i]);
                    else letters.add(last.toCharArray()[i]);
                }
                case RIGHT -> {
                    if (i < maxLength - current.length()) letters.add(last.toCharArray()[i]);
                    else letters.add(current.toCharArray()[i + current.length() - maxLength]);
                }
            }
        }

        return alignment.truncate(letters, width.value()).stream()
                .reduce(MutableText::append)
                .orElse(Text.empty())
                .setStyle(style);
    }

    public void text(Text text) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (!text.getString().isEmpty()) {
                    if (!cache.equals(current)) {
                        last = cache;
                        cache = current;
                    }

                    current = text.getString();
                    style = text.getStyle();
                }

                width.target(widthOf(text.getString()));
            }
        }, delay);
    }
}
