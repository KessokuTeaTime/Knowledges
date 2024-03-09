package net.krlite.knowledges.impl.data.info.entity;

import net.krlite.knowledges.impl.component.info.EntityInfoComponent;
import net.krlite.knowledges.impl.data.info.AbstractEntityInfoComponentData;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractEntityDescriptionData extends AbstractEntityInfoComponentData implements EntityInfoComponent.EntityDescriptionInvoker.Protocol {
    @Override
    public @NotNull String currentPath() {
        return super.currentPath() + ".entity_description";
    }
}
