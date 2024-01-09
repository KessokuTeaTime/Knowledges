package net.krlite.knowledges.data.info;

import net.krlite.knowledges.api.Data;
import net.krlite.knowledges.api.Knowledge;
import net.krlite.knowledges.components.info.BlockInfoComponent;
import net.krlite.knowledges.core.DataEvent;
import net.krlite.knowledges.core.path.WithPartialPath;
import org.jetbrains.annotations.NotNull;

public abstract class BlockInfoData<E extends DataEvent<BlockInfoComponent.BlockInfoTarget>> implements Data<BlockInfoComponent.BlockInfoTarget, E>, WithPartialPath {
    @Override
    public Class<? extends Knowledge<?>> knowledgeClass() {
        return BlockInfoComponent.class;
    }

    @Override
    public @NotNull String currentPath() {
        return "info.block";
    }
}
