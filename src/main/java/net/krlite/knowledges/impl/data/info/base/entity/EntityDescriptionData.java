package net.krlite.knowledges.impl.data.info.base.entity;

import net.krlite.knowledges.impl.component.info.EntityInfoComponent;
import net.krlite.knowledges.impl.data.info.base.EntityInfoComponentData;
import org.jetbrains.annotations.NotNull;

public abstract class EntityDescriptionData extends EntityInfoComponentData implements EntityInfoComponent.EntityDescriptionInvoker.Protocol {
    @Override
    public @NotNull String currentPath() {
        return super.currentPath() + Separator.RANK.toString().repeat(2) + "entity_description";
    }
}
