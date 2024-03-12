package net.krlite.knowledges.impl.data.info.block.blockinformation;

import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.AbstractFieldBuilder;
import net.krlite.knowledges.KnowledgesClient;
import net.krlite.knowledges.api.core.localization.EnumLocalizable;
import net.krlite.knowledges.Util;
import net.krlite.knowledges.api.proxy.KnowledgeProxy;
import net.krlite.knowledges.api.representable.BlockRepresentable;
import net.krlite.knowledges.impl.data.info.block.AbstractBlockInformationData;
import net.minecraft.block.Blocks;
import net.minecraft.block.NoteBlock;
import net.minecraft.item.Items;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public class NoteBlockInformationData extends AbstractBlockInformationData {
    @Override
    public Optional<MutableText> blockInformation(BlockRepresentable representable) {
        if (representable.blockState().isOf(Blocks.NOTE_BLOCK)) {
            MutableText instrumentText = KnowledgeProxy.getInstrumentName(representable.blockState().get(NoteBlock.INSTRUMENT));
            MutableText noteText = KnowledgesClient.CONFIG.get().data.noteBlockInformation.musicalAlphabet.alphabet(
                    representable.blockState().get(NoteBlock.NOTE),
                    KnowledgesClient.CONFIG.get().data.noteBlockInformation.noteModifier
            );

            return Util.Text.combineToMultiline(instrumentText, noteText);
        }

        if (representable.player().getMainHandStack().isOf(Items.NOTE_BLOCK)) {
            return Optional.of(KnowledgeProxy.getInstrumentName(representable.blockState().getInstrument()));
        }

        return Optional.empty();
    }

    @Override
    public @NotNull String partialPath() {
        return KnowledgeProxy.getId(Blocks.NOTE_BLOCK).getPath();
    }

    @Override
    public boolean providesTooltip() {
        return true;
    }

    @Override
    public boolean requestsConfigPage() {
        return true;
    }

    @Override
    public Function<ConfigEntryBuilder, List<AbstractFieldBuilder<?, ?, ?>>> buildConfigEntries() {
        return entryBuilder -> List.of(
                entryBuilder.startEnumSelector(
                                localizeForConfig("note_modifiers"),
                                NoteModifier.class,
                                KnowledgesClient.CONFIG.get().data.noteBlockInformation.noteModifier
                        )
                        .setDefaultValue(NoteModifier.SHARPS)
                        .setTooltip(localizeTooltipForConfig("note_modifiers"))
                        .setEnumNameProvider(e -> ((EnumLocalizable.WithName) e).localization())
                        .setSaveConsumer(value -> KnowledgesClient.CONFIG.get().data.noteBlockInformation.noteModifier = value),

                entryBuilder.startEnumSelector(
                                localizeForConfig("musical_alphabet"),
                                MusicalAlphabet.class,
                                KnowledgesClient.CONFIG.get().data.noteBlockInformation.musicalAlphabet
                        )
                        .setDefaultValue(MusicalAlphabet.ENGLISH)
                        .setTooltip(localizeTooltipForConfig("musical_alphabet"))
                        .setEnumNameProvider(e -> ((EnumLocalizable.WithName) e).localization())
                        .setSaveConsumer(value -> KnowledgesClient.CONFIG.get().data.noteBlockInformation.musicalAlphabet = value)
        );
    }

    public enum NoteModifier implements EnumLocalizable.WithName {
        SHARPS("sharps"), FLATS("flats");

        private final String path;

        NoteModifier(String path) {
            this.path = path;
        }

        @Override
        public String path() {
            return path;
        }

        public MutableText map(MutableText withSharp, MutableText withFlat) {
            return this == SHARPS ? withSharp : withFlat;
        }

        public MutableText map(String withSharp, String withFlat) {
            return map(Text.literal(withSharp), Text.literal(withFlat));
        }

        @Override
        public MutableText localization() {
            return KnowledgesClient.localize("note_modifier", path());
        }
    }

    public enum MusicalAlphabet implements EnumLocalizable.WithName {
        NUMERIC(
                "numeric",
                (n, m) -> Text.literal(String.valueOf(n)),
                KnowledgesClient.localize("musical_alphabet", "numeric", "tooltip")
        ),
        SOLFEGE("solfege", mapNotes(
                m -> m.map("Fa♯3", "So♭l3"),
                m -> Text.literal("Sol3"),
                m -> m.map("Sol♯3", "La♭3"),
                m -> Text.literal("La3"),
                m -> m.map("La♯3", "Si♭3"),
                m -> Text.literal("Si3"),
                m -> Text.literal("Do4"),
                m -> m.map("Do♯4", "Re♭4"),
                m -> Text.literal("Re4"),
                m -> m.map("Re♯4", "Mi♭4"),
                m -> Text.literal("Mi4"),
                m -> Text.literal("Fa4"),
                m -> m.map("Fa♯4", "Sol♭4"),
                m -> Text.literal("Sol4"),
                m -> m.map("Sol♯4", "La♭4"),
                m -> Text.literal("La4"),
                m -> m.map("La♯4", "Si♭4"),
                m -> Text.literal("Si4"),
                m -> Text.literal("Do5"),
                m -> m.map("Do♯5", "Re♭5"),
                m -> Text.literal("Re5"),
                m -> m.map("Re♯5", "Mi♭5"),
                m -> Text.literal("Mi5"),
                m -> Text.literal("Fa5"),
                m -> m.map("Fa♯5", "Sol♭5")
        ), Text.literal("Do-Re-Mi-Fa-Sol-La-Si ♯ ♭")),
        ENGLISH("english", mapNotes(
                m -> m.map("F♯3", "G♭3"),
                m -> Text.literal("G3"),
                m -> m.map("G♯3", "A♭3"),
                m -> Text.literal("A3"),
                m -> m.map("A♯3", "B♭3"),
                m -> Text.literal("B3"),
                m -> Text.literal("C4"),
                m -> m.map("C♯4", "D♭4"),
                m -> Text.literal("D4"),
                m -> m.map("D♯4", "E♭4"),
                m -> Text.literal("E4"),
                m -> Text.literal("F4"),
                m -> m.map("F♯4", "G♭4"),
                m -> Text.literal("G4"),
                m -> m.map("G♯4", "A♭4"),
                m -> Text.literal("A4"),
                m -> m.map("A♯4", "B♭4"),
                m -> Text.literal("B4"),
                m -> Text.literal("C5"),
                m -> m.map("C♯5", "D♭5"),
                m -> Text.literal("D5"),
                m -> m.map("D♯5", "E♭5"),
                m -> Text.literal("E5"),
                m -> Text.literal("F5"),
                m -> m.map("F♯5", "G♭5")
        ), Text.literal("C-D-E-F-G-A-B ♯ ♭")),
        ENGLISH_NOTE_GROUPS("english_note_groups", mapNotes(
                m -> m.map("F♯", "G♭"),
                m -> Text.literal("G"),
                m -> m.map("G♯", "A♭"),
                m -> Text.literal("A"),
                m -> m.map("A♯", "B♭"),
                m -> Text.literal("B"),
                m -> Text.literal("c"),
                m -> m.map("c♯", "d♭"),
                m -> Text.literal("d"),
                m -> m.map("d♯", "e♭"),
                m -> Text.literal("e"),
                m -> Text.literal("f"),
                m -> m.map("f♯", "g♭"),
                m -> Text.literal("g"),
                m -> m.map("g♯", "a♭"),
                m -> Text.literal("a"),
                m -> m.map("a♯", "b♭"),
                m -> Text.literal("b"),
                m -> Text.literal("c1"),
                m -> m.map("c♯1", "d♭1"),
                m -> Text.literal("d1"),
                m -> m.map("d♯1", "e♭1"),
                m -> Text.literal("e1"),
                m -> Text.literal("F"),
                m -> m.map("f♯1", "g♭1")
        ), Text.literal("c-d-e-f-g-a-b ♯ ♭")),
        GERMANIC("germanic", mapNotes(
                m -> m.map("Fis3", "Ges3"),
                m -> Text.literal("G3"),
                m -> m.map("Gis3", "As3"),
                m -> Text.literal("A3"),
                m -> m.map("Ais3", "H3"),
                m -> Text.literal("B3"),
                m -> Text.literal("C4"),
                m -> m.map("Cis4", "Des4"),
                m -> Text.literal("D4"),
                m -> m.map("Dis4", "Es4"),
                m -> Text.literal("E4"),
                m -> Text.literal("F4"),
                m -> m.map("Fis4", "Ges4"),
                m -> Text.literal("G4"),
                m -> m.map("Gis4", "As4"),
                m -> Text.literal("A4"),
                m -> m.map("Ais4", "H4"),
                m -> Text.literal("B4"),
                m -> Text.literal("C5"),
                m -> m.map("Cis5", "Des5"),
                m -> Text.literal("D5"),
                m -> m.map("Dis5", "Es5"),
                m -> Text.literal("E5"),
                m -> Text.literal("F5"),
                m -> m.map("Fis5", "Ges5")
        ), Text.literal("C-D-E-F-G-A-H -is -es")),
        RUSSIAN("russian", mapNotes(
                m -> m.map("Фа♯3", "So♭l3"),
                m -> Text.literal("Соль3"),
                m -> m.map("Соль♯3", "Ля♭3"),
                m -> Text.literal("Ля3"),
                m -> m.map("Ля♯3", "Си♭3"),
                m -> Text.literal("Си3"),
                m -> Text.literal("До4"),
                m -> m.map("До♯4", "Ре♭4"),
                m -> Text.literal("Ре4"),
                m -> m.map("Ре♯4", "Ми♭4"),
                m -> Text.literal("Ми4"),
                m -> Text.literal("Фа4"),
                m -> m.map("Фа♯4", "Соль♭4"),
                m -> Text.literal("Соль4"),
                m -> m.map("Соль♯4", "Ля♭4"),
                m -> Text.literal("Ля4"),
                m -> m.map("Ля♯4", "Си♭4"),
                m -> Text.literal("Си4"),
                m -> Text.literal("До5"),
                m -> m.map("До♯5", "Ре♭5"),
                m -> Text.literal("Ре5"),
                m -> m.map("Ре♯5", "Ми♭5"),
                m -> Text.literal("Ми5"),
                m -> Text.literal("Фа5"),
                m -> m.map("Фа♯5", "Соль♭5")
        ), Text.literal("До-Ре-Ми-Фа-Соль-Ля-Си")),
        JAPANESE("japanese", mapNotes(
                m -> m.map("嬰ヘ-1", "変ト-1"),
                m -> Text.literal("ト-1"),
                m -> m.map("嬰ト-1", "変イ-1"),
                m -> Text.literal("イ-1"),
                m -> m.map("嬰イ-1", "変ロ-1"),
                m -> Text.literal("ロ-1"),
                m -> Text.literal("ハ"),
                m -> m.map("嬰ハ", "変二"),
                m -> Text.literal("二"),
                m -> m.map("嬰二", "変ホ"),
                m -> Text.literal("ホ"),
                m -> Text.literal("ヘ"),
                m -> m.map("嬰ヘ", "変ト"),
                m -> Text.literal("ト"),
                m -> m.map("嬰ト", "変イ"),
                m -> Text.literal("イ"),
                m -> m.map("嬰イ", "変ロ"),
                m -> Text.literal("ロ"),
                m -> Text.literal("ハ+1"),
                m -> m.map("嬰ハ+1", "変二+1"),
                m -> Text.literal("二+1"),
                m -> m.map("嬰二+1", "変ホ+1"),
                m -> Text.literal("ホ+1"),
                m -> Text.literal("ヘ+1"),
                m -> m.map("嬰ヘ+1", "変ト+1")
        ), Text.literal("ハ 二 ホ ヘ ト イ ロ")),
        CHINESE("chinese", mapNotes(
                m -> m.map("升变徴-1", "降徴-1"),
                m -> Text.literal("徴-1"),
                m -> m.map("升徴-1", "降羽-1"),
                m -> Text.literal("羽-1"),
                m -> m.map("升羽-1", "降清宫-1"),
                m -> Text.literal("清宫-1"),
                m -> Text.literal("宫"),
                m -> m.map("升宫", "降商"),
                m -> Text.literal("商"),
                m -> m.map("升商", "降角"),
                m -> Text.literal("角"),
                m -> Text.literal("变徴"),
                m -> m.map("升变徴", "降徴"),
                m -> Text.literal("徴"),
                m -> m.map("升徴", "降羽"),
                m -> Text.literal("羽"),
                m -> m.map("升羽", "降清宫"),
                m -> Text.literal("清宫"),
                m -> Text.literal("宫+1"),
                m -> m.map("升宫+1", "降商+1"),
                m -> Text.literal("商+1"),
                m -> m.map("升商+1", "降角+1"),
                m -> Text.literal("角+1"),
                m -> Text.literal("变徴+1"),
                m -> m.map("升变徴+1", "降羽+1")
        ), Text.literal("宫 商 角 徴 羽"));

        private final String path;
        /**
         * 0 -> F#; 24 -> f#2
         */
        private final BiFunction<Integer, NoteModifier, MutableText> function;
        private final MutableText demo;

        MusicalAlphabet(String path, BiFunction<Integer, NoteModifier, MutableText> function, MutableText demo) {
            this.path = path;
            this.function = function;
            this.demo = demo;
        }

        @SafeVarargs
        public static BiFunction<Integer, NoteModifier, MutableText> mapNotes(Function<NoteModifier, MutableText>... functions) {
            return (note, modifier) -> {
                if (note >= 0 && note < functions.length) return functions[note].apply(modifier);
                return Text.empty();
            };
        }

        @Override
        public String path() {
            return path;
        }

        public MutableText alphabet(int note, NoteModifier modifier) {
            return function.apply(note, modifier);
        }

        @Override
        public MutableText localization() {
            return Text.translatable(
                    KnowledgesClient.localizationKey("musical_alphabet", path()),
                    demo
            );
        }
    }
}
