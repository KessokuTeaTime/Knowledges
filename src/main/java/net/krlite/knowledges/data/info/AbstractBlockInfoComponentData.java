package net.krlite.knowledges.data.info;

import net.krlite.knowledges.api.Data;
import net.krlite.knowledges.component.info.BlockInfoComponent;
import net.krlite.knowledges.core.path.WithPartialPath;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractBlockInfoComponentData implements Data<BlockInfoComponent>, WithPartialPath {
    @Override
    public @NotNull String currentPath() {
        return "info.block";
    }
}
