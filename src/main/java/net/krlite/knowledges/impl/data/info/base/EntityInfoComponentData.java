package net.krlite.knowledges.impl.data.info.base;

import net.krlite.knowledges.api.data.Data;
import net.krlite.knowledges.impl.component.info.EntityInfoComponent;
import net.krlite.knowledges.api.core.path.WithPartialPath;
import org.jetbrains.annotations.NotNull;

public abstract class EntityInfoComponentData implements Data<EntityInfoComponent>, WithPartialPath {
    @Override
    public @NotNull String currentPath() {
        return "info/entity";
    }
}
