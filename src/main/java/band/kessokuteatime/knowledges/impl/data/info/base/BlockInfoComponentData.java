package band.kessokuteatime.knowledges.impl.data.info.base;

import band.kessokuteatime.knowledges.api.data.Data;
import band.kessokuteatime.knowledges.impl.component.info.BlockInfoComponent;
import band.kessokuteatime.knowledges.api.core.path.WithPartialPath;
import org.jetbrains.annotations.NotNull;

public abstract class BlockInfoComponentData implements Data<BlockInfoComponent>, WithPartialPath {
    @Override
    public @NotNull String currentPath() {
        return "info/block";
    }
}
