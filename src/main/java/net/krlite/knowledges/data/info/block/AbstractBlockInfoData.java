package net.krlite.knowledges.data.info.block;

import net.krlite.knowledges.components.info.BlockInfoComponent;
import net.krlite.knowledges.data.info.AbstractBlockInfoComponentData;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public abstract class AbstractBlockInfoData extends AbstractBlockInfoComponentData<BlockInfoComponent.BlockInfoEvent.BlockInfoCallback> {
    public abstract Optional<MutableText> fetchInfo(BlockState blockState, ItemStack mainHandStack);

    @Override
    public BlockInfoComponent.BlockInfoEvent.BlockInfoCallback listener() {
        return this::fetchInfo;
    }

    @Override
    public BlockInfoComponent.BlockInfoEvent source() {
        return BlockInfoComponent.BlockInfoEvent.BLOCK_INFO;
    }

    @Override
    public @NotNull String currentPath() {
        return super.currentPath() + ".block_info";
    }
}
