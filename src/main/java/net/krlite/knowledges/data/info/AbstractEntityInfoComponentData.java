package net.krlite.knowledges.data.info;

import net.krlite.knowledges.api.Data;
import net.krlite.knowledges.component.info.EntityInfoComponent;
import net.krlite.knowledges.core.path.WithPartialPath;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractEntityInfoComponentData implements Data<EntityInfoComponent>, WithPartialPath {
    @Override
    public @NotNull String currentPath() {
        return "info.entity";
    }
}
