package net.krlite.knowledges.data.info.block.blockinformation;

import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.AbstractFieldBuilder;
import net.krlite.knowledges.Knowledges;
import net.krlite.knowledges.api.Knowledge;
import net.krlite.knowledges.config.KnowledgesConfig;
import net.krlite.knowledges.config.modmenu.KnowledgesConfigScreen;
import net.krlite.knowledges.data.info.block.AbstractBlockInformationData;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class NoteBlockInformationData extends AbstractBlockInformationData {
    @Override
    public Optional<MutableText> blockInformation(BlockState blockState, PlayerEntity player) {
        if (player.getMainHandStack().isOf(Items.NOTE_BLOCK)) {
            return Optional.of(Knowledge.Util.instrumentName(blockState.getInstrument()));
        }

        if (blockState.isOf(Blocks.NOTE_BLOCK)) {

        }

        return Optional.empty();
    }

    @Override
    public @NotNull String partialPath() {
        return Registries.BLOCK.getId(Blocks.NOTE_BLOCK).getPath();
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
                entryBuilder.startBooleanToggle(
                                localize("config", "test"),
                                true
                        )
                        .setDefaultValue(true)
                        .setSaveConsumer(b -> {})
                        .setYesNoTextSupplier(KnowledgesConfigScreen.BooleanSupplier.ENABLED_DISABLED)
        );
    }
}
