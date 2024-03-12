package net.krlite.knowledges.impl.entrypoint.data.info.entity;

import net.krlite.knowledges.api.entrypoint.DataProvider;
import net.krlite.knowledges.impl.data.info.base.entity.EntityDescriptionData;
import net.krlite.knowledges.impl.data.info.entity.entitydescription.AnimalOwnerEntityDescriptionData;
import net.krlite.knowledges.impl.data.info.entity.entitydescription.ItemFrameEntityDescriptionData;
import net.krlite.knowledges.impl.data.info.entity.entitydescription.VillagerEntityDescriptionData;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EntityDescriptionDataProvider implements DataProvider<EntityDescriptionData> {
    @Override
    public @NotNull List<Class<? extends EntityDescriptionData>> provide() {
        return List.of(
                AnimalOwnerEntityDescriptionData.class,
                ItemFrameEntityDescriptionData.class,
                VillagerEntityDescriptionData.class
        );
    }
}
