package net.krlite.knowledges.data.info.block.blockinformation;

import net.krlite.knowledges.api.Knowledge;
import net.krlite.knowledges.data.info.block.AbstractBlockInformationData;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class NoteBlockInformationData extends AbstractBlockInformationData {
    @Override
    public Optional<MutableText> fetchInfo(BlockState blockState, PlayerEntity player) {
        if (player.getMainHandStack().isOf(Items.NOTE_BLOCK)) {
            return Optional.of(Knowledge.Util.getInstrumentName(blockState.getInstrument()));
        }

        return Optional.empty();
    }

    @Override
    public @NotNull String partialPath() {
        return Registries.BLOCK.getId(Blocks.NOTE_BLOCK).getPath();
    }
}
