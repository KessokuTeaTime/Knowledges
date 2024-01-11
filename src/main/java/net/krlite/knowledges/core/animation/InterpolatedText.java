package net.krlite.knowledges.core.animation;

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

        public static ArrayList<StyledChar> from(String stringUnCut) {
            ArrayList<StyledChar> result = new ArrayList<>();
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

    private final ArrayList<ArrayList<StyledChar>> current = new ArrayList<>(List.of(new ArrayList<>()));
    private List<List<StyledChar>> cache = List.copyOf(current), last = List.copyOf(cache);

    private Style style = Style.EMPTY;

    public MutableText text() {
        ArrayList<ArrayList<StyledChar>> styledLines = new ArrayList<>();

        for (int line = 0; line < Math.max(last.size(), current.size()); line++) {
            List<StyledChar> lastLine = line < last.size() ? last.get(line) : new ArrayList<>();
            List<StyledChar> currentLine = line < current.size() ? current.get(line) : new ArrayList<>();
            ArrayList<StyledChar> styledLine = new ArrayList<>();

            final int
                    lastSize = lastLine.size(),
                    currentSize = currentLine.size(),
                    maxSize = Math.max(lastSize, currentSize);

            for (int index = 0; index < maxSize; index++) {
                switch (alignment) {
                    case LEFT -> {
                        if (index < currentSize) styledLine.add(currentLine.get(index));
                        else styledLine.add(lastLine.get(index));
                    }
                    case RIGHT -> {
                        if (index < maxSize - currentSize) styledLine.add(lastLine.get(index));
                        else styledLine.add(currentLine.get(index + currentSize - maxSize));
                    }
                }
            }

            styledLines.add(styledLine);
        }

        return styledLines.stream()
                .map(line -> alignment.truncate(line, width.value()))
                .map(line -> line.setStyle(style))
                .reduce((p, n) -> p.append("\n").append(n))
                .orElse(Text.empty());
    }

    public void text(Text text) {
        List<String> stringsUnCut = Arrays.stream(text.getString().split("\n")).toList();
        List<String> strings = stringsUnCut.stream().map(StyledChar::cut).toList();

        if (!strings.isEmpty()) {
            if (!cache.equals(current)) {
                last = List.copyOf(cache);
                cache = List.copyOf(current);
                System.out.println(last + ", " + cache + ", " + current);
            }

            for (int line = 0; line < Math.max(strings.size(), current.size()); line++) {
                if (line < current.size() && line < strings.size())
                    current.set(line, StyledChar.from(stringsUnCut.get(line)));
                else if (line < strings.size())
                    current.add(StyledChar.from(stringsUnCut.get(line)));
                else
                    current.set(line, new ArrayList<>());
            }
            style = text.getStyle();
        }

        width.target(strings.stream().map(InterpolatedText::widthOfString).max(Comparator.naturalOrder()).orElse(0));
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
        }

        else {
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
