package net.krlite.knowledges.data.info.block.blockinformation;

import net.krlite.knowledges.data.info.block.AbstractBlockInformationData;
import net.minecraft.block.BlockState;
import net.minecraft.block.SaplingBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class SaplingBlockInformationData extends AbstractBlockInformationData {
    @Override
    public Optional<MutableText> blockInformation(BlockState blockState, PlayerEntity player) {
        if (blockState.getBlock() instanceof SaplingBlock) {
            int stage = blockState.get(SaplingBlock.STAGE);

            return Optional.of(Text.translatable(
                    localizationKey("stage"),
                    stage
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
        return "sapling";
    }
}
