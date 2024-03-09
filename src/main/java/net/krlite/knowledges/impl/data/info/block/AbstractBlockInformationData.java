package net.krlite.knowledges.impl.data.info.block;

import net.krlite.knowledges.impl.component.info.BlockInfoComponent;
import net.krlite.knowledges.impl.data.info.AbstractBlockInfoComponentData;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractBlockInformationData extends AbstractBlockInfoComponentData implements BlockInfoComponent.BlockInformationInvoker.Protocol {
    @Override
    public @NotNull String currentPath() {
        return super.currentPath() + ".block_information";
    }
}
