package band.kessokuteatime.knowledges.impl.entrypoint.data.info.entity;

import band.kessokuteatime.knowledges.api.entrypoint.DataProvider;
import band.kessokuteatime.knowledges.impl.data.info.base.entity.EntityDescriptionData;
import band.kessokuteatime.knowledges.impl.data.info.entity.entitydescription.AnimalOwnerData;
import band.kessokuteatime.knowledges.impl.data.info.entity.entitydescription.ItemFrameData;
import band.kessokuteatime.knowledges.impl.data.info.entity.entitydescription.VillagerData;
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
