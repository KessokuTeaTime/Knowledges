package net.krlite.knowledges.impl.data.info.block.blockinformation;

import net.krlite.knowledges.api.proxy.KnowledgeProxy;
import net.krlite.knowledges.api.representable.BlockRepresentable;
import net.krlite.knowledges.impl.data.info.block.AbstractBlockInformationData;
import net.minecraft.block.Blocks;
import net.minecraft.text.MutableText;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class BrewingStandInformationData extends AbstractBlockInformationData {
    @Override
    public Optional<MutableText> blockInformation(BlockRepresentable representable) {
        // TODO
        return Optional.empty();
    }

    @Override
    public @NotNull String partialPath() {
        return KnowledgeProxy.getId(Blocks.BREWING_STAND).getPath();
    }
}
