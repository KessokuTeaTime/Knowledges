package net.krlite.knowledges.data.info.block.blockinformation;

import net.krlite.knowledges.data.info.block.AbstractBlockInformationData;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ComposterBlock;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class RedstoneWireBlockInformationData extends AbstractBlockInformationData {
    @Override
    public Optional<MutableText> blockInformation(BlockState blockState, PlayerEntity player) {
        if (blockState.isOf(Blocks.REDSTONE_WIRE)) {
            return Optional.of(Text.translatable(
                    localizationKey("power"),
                    blockState.get(RedstoneWireBlock.POWER)
            ));
        }

        return Optional.empty();
    }

    @Override
    public boolean providesTooltip() {
        return true;
    }

    @Override
    public @NotNull String partialPath() {
        return Registries.BLOCK.getId(Blocks.REDSTONE_WIRE).getPath();
    }
}
