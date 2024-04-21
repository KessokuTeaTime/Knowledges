package band.kessokuteatime.knowledges.impl.data.info.base.block;

import band.kessokuteatime.knowledges.impl.component.info.BlockInfoComponent;
import band.kessokuteatime.knowledges.impl.data.info.base.BlockInfoComponentData;
import org.jetbrains.annotations.NotNull;

public abstract class BlockInformationData extends BlockInfoComponentData implements BlockInfoComponent.BlockInformation.Protocol {
    @Override
    public @NotNull String currentPath() {
        return super.currentPath() + Separator.RANK.toString().repeat(2) + "block_information";
    }
}
