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
            boolean metFormattingMark = false;

            ListIterator<Character> iterator = letters.listIterator();
            int currentWidth = 0;
            while (iterator.hasNext()) {
                Character letter = iterator.next();

                processLetterWidth: {
                    if (metFormattingMark) {
                        metFormattingMark = false;
                        break processLetterWidth;
                    } else if (letter.equals('§')) {
                        metFormattingMark = true;
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
            boolean willMeetFormattingMark = false;

            ListIterator<Character> iterator = letters.listIterator(letters.size());
            int currentWidth = 0;
            while (iterator.hasPrevious()) {
                Character letter = iterator.previous();

                processLetterWidth: {
                    if (willMeetFormattingMark) {
                        willMeetFormattingMark = false;
                        break processLetterWidth;
                    } else if (iterator.hasPrevious() && letters.get(iterator.previousIndex()).equals('§')) {
                        willMeetFormattingMark = true;
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
        
        switch (alignment) {
            case LEFT -> {
                if (last.length() > current.length()) {
                    letters.addAll(current.length(), peekFormattingMarks(letters, current.length(), false));
                }
            }
            case RIGHT -> {
                // TODO: 2023/12/6  
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
    
    private ArrayList<Character> peekFormattingMarks(ArrayList<Character> letters, int at, boolean rewind) {
        if (at < 0 || at >= letters.size()) return new ArrayList<>();
        if ((rewind && at < 1) || (!rewind && at >= letters.size() - 1)) return new ArrayList<>();

        ArrayList<Character> result = new ArrayList<>();
        final Character starting = '§';
        boolean continuous = false;

        if (!rewind) {
            // From head to tail
            ListIterator<Character> iterator = letters.listIterator(at);
            
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
            ListIterator<Character> iterator = letters.listIterator(at + 1);
            
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
