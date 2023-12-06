package net.krlite.knowledges.base;

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

    public enum Alignment {
        LEFT((letters, width) -> {
            ArrayList<Character> result = new ArrayList<>();
            boolean wasFormatMark = false;

            ListIterator<Character> iterator = letters.listIterator(0);
            int currentWidth = 0;
            while (iterator.hasNext()) {
                Character letter = iterator.next();

                processLetterWidth: {
                    if (wasFormatMark) {
                        wasFormatMark = false;
                        break processLetterWidth;
                    } else if (letter.equals('§')) {
                        wasFormatMark = true;
                        break processLetterWidth;
                    }

                    int letterWidth = widthOf(String.valueOf(letter));
                    if (currentWidth + letterWidth > width + 1) break;

                    currentWidth += letterWidth;
                }

                result.add(letter);
            }

            return result;
        }),
        RIGHT((letters, width) -> {
            ArrayList<Character> result = new ArrayList<>();
            boolean willHaveFormatMark = false;

            ListIterator<Character> iterator = letters.listIterator(letters.size());
            int currentWidth = 0;
            while (iterator.hasPrevious()) {
                Character letter = iterator.previous();

                processLetterWidth: {
                    if (willHaveFormatMark) {
                        willHaveFormatMark = false;
                        break processLetterWidth;
                    } else if (iterator.hasPrevious() && letters.get(iterator.previousIndex()).equals('§')) {
                        willHaveFormatMark = true;
                        break processLetterWidth;
                    }

                    int letterWidth = widthOf(String.valueOf(letter));
                    if (currentWidth + letterWidth > width + 1) break;

                    currentWidth += letterWidth;
                }

                result.add(0, letter);
            }

            return result;
        });

        final BiFunction<ArrayList<Character>, Double, ArrayList<Character>> truncateFunction;

        Alignment(BiFunction<ArrayList<Character>, Double, ArrayList<Character>> truncateFunction) {
            this.truncateFunction = truncateFunction;
        }

        public MutableText truncate(ArrayList<Character> letters, double width) {
            return truncateFunction.apply(letters, width).stream()
                    .map(String::valueOf)
                    .map(Text::literal)
                    .reduce(MutableText::append)
                    .orElse(Text.empty());
        }

        private ArrayList<Character> peekFormattingMarks(ArrayList<Character> letters, int at, boolean rewind) {
            if (at < 0 || at > letters.size()) return new ArrayList<>();
            if ((rewind && at < 1) || (!rewind && at > letters.size() - 1)) return new ArrayList<>();

            ListIterator<Character> iterator = letters.listIterator(at);
            ArrayList<Character> result = new ArrayList<>();
            final Character starting = '§';
            boolean continuous = false;

            if (!rewind) {
                // From head to tail
                while (iterator.hasNext()) {
                    Character letter = iterator.next();
                    boolean meeting = letter.equals(starting);

                    if (!result.isEmpty()) {
                        if (continuous) {
                            // Inside a formatting mark
                            result.add(letter);
                            continuous = false;
                        }

                        else if (meeting) {
                            // Meeting another '§'
                            result.add(0, letter);
                            continuous = true;
                        }

                        else break;
                    }

                    else if (meeting) {
                        if (!continuous) {
                            // Meeting the first '§'
                            result.add(letter); // '§'
                            continuous = true;
                        }
                    }
                }
            } else {
                // From tail to head
                while (iterator.hasPrevious()) {
                    Character letter = iterator.previous();
                    boolean willMeet = iterator.hasPrevious() && letters.get(iterator.previousIndex()).equals(starting);

                    if (!result.isEmpty()) {
                        if (continuous) {
                            // Inside a formatting mark
                            result.add(0, letter); // Should always be '§'
                            continuous = false;
                        }

                        else if (willMeet) {
                            // Will meet another '§'
                            result.add(0, letter);
                            continuous = true;
                        }

                        else break;
                    }

                    else if (willMeet) {
                        // Will meet the first '§'
                        result.add(0, letter);
                        continuous = true;
                    }
                }
            }

            return result;
        }
    }

    public InterpolatedText(Alignment alignment) {
        this.alignment = alignment;
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

        return alignment.truncate(letters, width.value()).setStyle(style);
    }

    public void text(Text text) {
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
}
