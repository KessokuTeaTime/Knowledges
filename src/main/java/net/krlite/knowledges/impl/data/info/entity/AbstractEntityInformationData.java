package net.krlite.knowledges.impl.data.info.entity;

import net.krlite.knowledges.impl.component.info.EntityInfoComponent;
import net.krlite.knowledges.impl.data.info.AbstractEntityInfoComponentData;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractEntityInformationData extends AbstractEntityInfoComponentData implements EntityInfoComponent.EntityInformationInvoker.Protocol {
    @Override
    public @NotNull String currentPath() {
        return super.currentPath() + "/entity_information";
    }
}
