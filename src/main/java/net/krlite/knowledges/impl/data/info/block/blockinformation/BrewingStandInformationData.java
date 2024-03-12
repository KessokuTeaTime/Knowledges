package net.krlite.knowledges.impl.data.info.block.blockinformation;

import net.krlite.knowledges.api.proxy.ModProxy;
import net.krlite.knowledges.api.representable.BlockRepresentable;
import net.krlite.knowledges.impl.data.info.base.block.BlockInformationData;
import net.minecraft.block.Blocks;
import net.minecraft.text.MutableText;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class BrewingStandInformationData extends BlockInformationData {
    @Override
    public Optional<MutableText> blockInformation(BlockRepresentable representable) {
        // TODO
        return Optional.empty();
    }

    @Override
    public @NotNull String partialPath() {
        return ModProxy.getId(Blocks.BREWING_STAND).getPath();
    }
}
