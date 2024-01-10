package net.krlite.knowledges.data.info.block;

import net.krlite.knowledges.component.info.BlockInfoComponent;
import net.krlite.knowledges.core.data.DataInvoker;
import net.krlite.knowledges.data.info.AbstractBlockInfoComponentData;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractBlockInformationData extends AbstractBlockInfoComponentData implements BlockInfoComponent.BlockInformationInvoker.Protocol {
    @Override
    public DataInvoker<BlockInfoComponent, ?> dataInvoker() {
        return BlockInfoComponent.BlockInformationInvoker.Protocol.super.dataInvoker();
    }

    @Override
    public @NotNull String currentPath() {
        return super.currentPath() + ".block_information";
    }
}
