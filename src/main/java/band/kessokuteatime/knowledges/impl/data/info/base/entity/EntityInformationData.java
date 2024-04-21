package band.kessokuteatime.knowledges.impl.data.info.base.entity;

import band.kessokuteatime.knowledges.impl.component.info.EntityInfoComponent;
import band.kessokuteatime.knowledges.impl.data.info.base.EntityInfoComponentData;
import org.jetbrains.annotations.NotNull;

public abstract class EntityInformationData extends EntityInfoComponentData implements EntityInfoComponent.EntityInformation.Protocol {
    @Override
    public @NotNull String currentPath() {
        return super.currentPath() + Separator.RANK.toString().repeat(2) + "entity_information";
    }
}
