package band.kessokuteatime.knowledges.impl.data.info.block.blockinformation;

import band.kessokuteatime.knowledges.api.proxy.ModProxy;
import band.kessokuteatime.knowledges.api.representable.BlockRepresentable;
import band.kessokuteatime.knowledges.impl.data.info.base.block.BlockInformationData;
import net.minecraft.block.Blocks;
import net.minecraft.block.ComposterBlock;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ComposterData extends BlockInformationData {
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
        return ModProxy.getId(Blocks.COMPOSTER).getPath();
    }

    @Override
    public boolean providesTooltip() {
        return true;
    }
}
