package net.krlite.knowledges.impl.data.info.block.blockinformation;

import net.krlite.knowledges.api.proxy.KnowledgeProxy;
import net.krlite.knowledges.api.representable.BlockRepresentable;
import net.krlite.knowledges.impl.data.info.base.block.BlockInformationData;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class RedstoneWireBlockInformationData extends BlockInformationData {
    @Override
    public Optional<MutableText> blockInformation(BlockRepresentable representable) {
        if (representable.blockState().isOf(Blocks.REDSTONE_WIRE)) {
            return Optional.of(Text.translatable(
                    localizationKey("power"),
                    representable.blockState().get(RedstoneWireBlock.POWER)
            ));
        }

        return Optional.empty();
    }

    @Override
    public @NotNull String partialPath() {
        return KnowledgeProxy.getId(Blocks.REDSTONE_WIRE).getPath();
    }

    @Override
    public boolean providesTooltip() {
        return true;
    }
}
