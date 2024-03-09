package net.krlite.knowledges.impl.data.info.block;

import net.krlite.knowledges.impl.component.info.BlockInfoComponent;
import net.krlite.knowledges.impl.data.info.AbstractBlockInfoComponentData;
import net.minecraft.block.BlockState;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.text.MutableText;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class MineableToolData extends AbstractBlockInfoComponentData implements BlockInfoComponent.MineableToolInvoker.Protocol {
    @Override
    public Optional<MutableText> mineableTool(BlockState blockState) {
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
    }

    @Override
    public @NotNull String partialPath() {
        return "mineable_tool";
    }

    @Override
    public boolean providesTooltip() {
        return true;
    }
}
