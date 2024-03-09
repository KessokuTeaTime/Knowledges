package net.krlite.knowledges.impl.data.info.block.blockinformation;

import net.krlite.knowledges.api.proxy.KnowledgeProxy;
import net.krlite.knowledges.api.representable.BlockRepresentable;
import net.krlite.knowledges.impl.data.info.block.AbstractBlockInformationData;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ComposterBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ComposterBlockInformationData extends AbstractBlockInformationData {
    @Override
    public Optional<MutableText> blockInformation(BlockRepresentable representable) {
        if (representable.blockState().isOf(Blocks.COMPOSTER)) {
            return Optional.of(Text.translatable(
                    localizationKey("level"),
                    representable.blockState().get(ComposterBlock.LEVEL), ComposterBlock.MAX_LEVEL
            ));
        }

        return Optional.empty();
    }

    @Override
    public @NotNull String partialPath() {
        return KnowledgeProxy.getId(Blocks.COMPOSTER).getPath();
    }

    @Override
    public boolean providesTooltip() {
        return true;
    }
}
