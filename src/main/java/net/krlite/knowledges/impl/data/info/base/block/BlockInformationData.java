package net.krlite.knowledges.impl.data.info.base.block;

import net.krlite.knowledges.impl.component.info.BlockInfoComponent;
import net.krlite.knowledges.impl.data.info.base.BlockInfoComponentData;
import org.jetbrains.annotations.NotNull;

public abstract class BlockInformationData extends BlockInfoComponentData implements BlockInfoComponent.BlockInformation.Protocol {
    @Override
    public @NotNull String currentPath() {
        return super.currentPath() + Separator.RANK.toString().repeat(2) + "block_information";
    }
}
