package net.krlite.knowledges.impl.entrypoint.data.info.entity;

import net.krlite.knowledges.api.entrypoint.DataProvider;
import net.krlite.knowledges.impl.data.info.base.entity.EntityDescriptionData;
import net.krlite.knowledges.impl.data.info.entity.entitydescription.AnimalOwnerData;
import net.krlite.knowledges.impl.data.info.entity.entitydescription.ItemFrameData;
import net.krlite.knowledges.impl.data.info.entity.entitydescription.VillagerData;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EntityDescriptionDataProvider implements DataProvider<EntityDescriptionData> {
    @Override
    public @NotNull List<Class<? extends EntityDescriptionData>> provide() {
        return List.of(
                AnimalOwnerData.class,
                ItemFrameData.class,
                VillagerData.class
        );
    }
}
