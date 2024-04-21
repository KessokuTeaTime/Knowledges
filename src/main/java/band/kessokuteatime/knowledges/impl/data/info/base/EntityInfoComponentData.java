package band.kessokuteatime.knowledges.impl.data.info.base;

import band.kessokuteatime.knowledges.api.data.Data;
import band.kessokuteatime.knowledges.impl.component.info.EntityInfoComponent;
import band.kessokuteatime.knowledges.api.core.path.WithPartialPath;
import org.jetbrains.annotations.NotNull;

public abstract class EntityInfoComponentData implements Data<EntityInfoComponent>, WithPartialPath {
    @Override
    public @NotNull String currentPath() {
        return "info/entity";
    }
}
