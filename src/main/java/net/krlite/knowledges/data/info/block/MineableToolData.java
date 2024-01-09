package net.krlite.knowledges.data.info.block;

import net.krlite.knowledges.components.info.BlockInfoComponent;
import net.krlite.knowledges.data.info.AbstractBlockInfoComponentData;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.text.MutableText;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class MineableToolData extends AbstractBlockInfoComponentData<BlockInfoComponent.BlockInfoCallbacks.MineableToolEvent> {
    @Override
    public BlockInfoComponent.BlockInfoCallbacks.MineableToolEvent listener() {
        return blockState -> {
            MutableText tool = null;

            if (blockState.isIn(BlockTags.PICKAXE_MINEABLE)) {
                tool = localize("pickaxe");
            } else if (blockState.isIn(BlockTags.AXE_MINEABLE)) {
                tool = localize("axe");
            } else if (blockState.isIn(BlockTags.HOE_MINEABLE)) {
                tool = localize("hoe");
            } else if (blockState.isIn(BlockTags.SHOVEL_MINEABLE)) {
                tool = localize("shovel");
            }

            return Optional.ofNullable(tool);
        };
    }

    @Override
    public BlockInfoComponent.BlockInfoCallbacks target() {
        return BlockInfoComponent.BlockInfoCallbacks.MINEABLE_TOOL;
    }

    @Override
    public @NotNull String partialPath() {
        return "tool";
    }
}
