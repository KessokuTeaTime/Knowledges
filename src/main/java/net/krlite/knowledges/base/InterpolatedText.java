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
    public static final char FORMATTING_SIGN = '§';

    public static class StyledChar {
        private final Character character;
        private final List<Character> formattingMarks;

        public StyledChar(List<Character> letters, int at) {
            this.character = letters.get(at);
            this.formattingMarks = peekFormattingMarks(letters, at, true);
        }

        public int width() {
            return widthOfString(String.valueOf(character));
        }

        @Override
        public String toString() {
            return formattingMarks.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining()) + character + FORMATTING_SIGN + 'r';
        }

        public MutableText mutableText() {
            return Text.literal(toString());
        }

        public static List<StyledChar> from(String stringUnCut) {
            List<StyledChar> result = new ArrayList<>();
            boolean inFormattingMark = false;

            for (int i = 0; i < stringUnCut.length(); i++) {
                char c = stringUnCut.toCharArray()[i];

                if (inFormattingMark) {
                    inFormattingMark = false;
                    continue;
                } else if (c == FORMATTING_SIGN) {
                    inFormattingMark = true;
                    continue;
                }

                result.add(new StyledChar(toCharacterList(stringUnCut), i));
            }

            return result;
        }

        public static String cut(String string) {
            StringBuilder result = new StringBuilder();
            boolean inFormattingMark = false;

            for (int i = 0; i < string.length(); i++) {
                char c = string.toCharArray()[i];

                if (inFormattingMark) {
                    inFormattingMark = false;
                    continue;
                } else if (c == FORMATTING_SIGN) {
                    inFormattingMark = true;
                    continue;
                }

                result.append(c);
            }

            return result.toString();
        }

        public static int widthOf(List<StyledChar> styledChars) {
            return styledChars.stream()
                    .mapToInt(StyledChar::width)
                    .sum();
        }
    }

    public enum Alignment {
        LEFT((styledChars, width) -> {
            List<StyledChar> result = new ArrayList<>();
            ListIterator<StyledChar> iterator = styledChars.listIterator();
            int currentWidth = 0;

            while (iterator.hasNext()) {
                StyledChar letter = iterator.next();
                int letterWidth = letter.width();

                if (currentWidth + letterWidth > width + 1) break;

                currentWidth += letterWidth;
                result.add(letter);
            }

            return result;
        }),
        RIGHT((styledChars, width) -> {
            List<StyledChar> result = new ArrayList<>();
            ListIterator<StyledChar> iterator = styledChars.listIterator(styledChars.size());
            int currentWidth = 0;

            while (iterator.hasPrevious()) {
                StyledChar letter = iterator.previous();
                int letterWidth = letter.width();

                if (currentWidth + letterWidth > width + 1) break;

                currentWidth += letterWidth;
                result.add(0, letter);
            }

            return result;
        });

        final BiFunction<List<StyledChar>, Double, List<StyledChar>> truncateFunction;

        Alignment(BiFunction<List<StyledChar>, Double, List<StyledChar>> truncateFunction) {
            this.truncateFunction = truncateFunction;
        }

        public MutableText truncate(List<StyledChar> styledChars, double width) {
            return truncateFunction.apply(styledChars, width).stream()
                    .map(StyledChar::mutableText)
                    .reduce(MutableText::append)
                    .orElse(Text.empty());
        }
    }

    public InterpolatedText(Alignment alignment) {
        this.alignment = alignment;
    }

    private final InterpolatedDouble width = new InterpolatedDouble(0, 0.02);
    private final Alignment alignment;
    private List<StyledChar> last = new ArrayList<>(), current = new ArrayList<>(), cache = current;
    private Style style = Style.EMPTY;

    public MutableText text() {
        ArrayList<StyledChar> styledChars = new ArrayList<>();
        final int
                lastSize = last.size(),
                currentSize = current.size(),
                maxSize = Math.max(lastSize, currentSize);
        
        for (int i = 0; i < maxSize; i++) {
            switch (alignment) {
                case LEFT -> {
                    if (i < currentSize) styledChars.add(current.get(i));
                    else styledChars.add(last.get(i));
                }
                case RIGHT -> {
                    if (i < maxSize - currentSize) styledChars.add(last.get(i));
                    else styledChars.add(current.get(i + currentSize - maxSize));
                }
            }
        }

        return alignment.truncate(styledChars, width.value()).setStyle(style);
    }

    public void text(Text text) {
        String stringUnCut = text.getString();
        String string = StyledChar.cut(stringUnCut);

        if (!string.isEmpty()) {
            if (!cache.equals(current)) {
                last = cache;
                cache = current;
            }

            current = StyledChar.from(stringUnCut);
            style = text.getStyle();
        }

        width.target(widthOfString(string));
    }

    public static int widthOfString(String string) {
        return MinecraftClient.getInstance().textRenderer.getWidth(string);
    }

    public static List<Character> toCharacterList(String string) {
        List<Character> result = new ArrayList<>();
        for (char c : string.toCharArray()) result.add(c);
        return result;
    }
    
    public static List<Character> peekFormattingMarks(List<Character> letters, int at, boolean findPrevious) {
        if (at < 0 || at >= letters.size()) return new ArrayList<>();
        if ((findPrevious && at < 1) || (!findPrevious && at >= letters.size() - 1)) return new ArrayList<>();

        List<Character> result = new ArrayList<>();
        boolean continuous = false;

        if (!findPrevious) {
            // From head to tail
            ListIterator<Character> iterator = letters.listIterator(at);
            
            while (iterator.hasNext()) {
                Character letter = iterator.next();
                boolean meeting = letter.equals(FORMATTING_SIGN);

                if (continuous) {
                    // Inside a formatting mark
                    result.add(letter);
                    continuous = false;
                }

                else if (meeting) {
                    // Meeting '§'
                    result.add(0, letter);
                    continuous = true;
                }

                else break;
            }
        } else {
            // From tail to head
            ListIterator<Character> iterator = letters.listIterator(at + 1);
            
            while (iterator.hasPrevious()) {
                Character letter = iterator.previous();
                boolean willMeet = iterator.hasPrevious() && letters.get(iterator.previousIndex()).equals(FORMATTING_SIGN);

                if (continuous) {
                    // Inside a formatting mark
                    result.add(0, letter); // Should always be '§'
                    continuous = false;
                }

                else if (willMeet) {
                    // Will meet '§'
                    result.add(0, letter);
                    continuous = true;
                }
            }
        }

        return result;
    }
}
