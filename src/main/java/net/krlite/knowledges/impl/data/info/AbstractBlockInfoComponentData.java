package net.krlite.knowledges.impl.data.info;

import net.krlite.knowledges.api.data.Data;
import net.krlite.knowledges.impl.component.info.BlockInfoComponent;
import net.krlite.knowledges.api.core.path.WithPartialPath;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractBlockInfoComponentData implements Data<BlockInfoComponent>, WithPartialPath {
    @Override
    public @NotNull String currentPath() {
        return "info/block";
    }
}
