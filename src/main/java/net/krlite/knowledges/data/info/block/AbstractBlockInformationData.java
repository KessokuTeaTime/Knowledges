package net.krlite.knowledges.data.info.block;

import net.krlite.knowledges.component.info.BlockInfoComponent;
import net.krlite.knowledges.data.info.AbstractBlockInfoComponentData;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.MutableText;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public abstract class AbstractBlockInformationData extends AbstractBlockInfoComponentData<BlockInfoComponent.BlockInformationCallback> {
    public abstract Optional<MutableText> fetchInfo(BlockState blockState, PlayerEntity player);

    @Override
    public BlockInfoComponent.BlockInformationCallback callback() {
        return (blockState, player) -> shouldProvideNothing() ? Optional.empty() : fetchInfo(blockState, player);
    }

    @Override
    public @NotNull String currentPath() {
        return super.currentPath() + ".block_information";
    }
}
