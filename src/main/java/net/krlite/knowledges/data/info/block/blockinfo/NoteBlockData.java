package net.krlite.knowledges.data.info.block.blockinfo;

import net.krlite.knowledges.api.Knowledge;
import net.krlite.knowledges.data.info.block.AbstractBlockInfoData;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class NoteBlockData extends AbstractBlockInfoData {
    @Override
    public Optional<MutableText> fetchInfo(BlockState blockState, ItemStack mainHandStack) {
        if (mainHandStack.isOf(Items.NOTE_BLOCK)) {
            return Optional.of(Knowledge.Util.getInstrumentName(blockState.getInstrument()));
        }

        return Optional.empty();
    }

    @Override
    public @NotNull String partialPath() {
        return Registries.BLOCK.getId(Blocks.NOTE_BLOCK).getPath();
    }
}
