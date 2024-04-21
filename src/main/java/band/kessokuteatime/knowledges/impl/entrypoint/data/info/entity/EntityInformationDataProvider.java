package band.kessokuteatime.knowledges.impl.entrypoint.data.info.entity;

import band.kessokuteatime.knowledges.api.entrypoint.DataProvider;
import band.kessokuteatime.knowledges.impl.data.info.base.entity.EntityInformationData;
import band.kessokuteatime.knowledges.impl.data.info.entity.entityinformation.ItemFrameData;
import band.kessokuteatime.knowledges.impl.data.info.entity.entityinformation.PaintingData;
import band.kessokuteatime.knowledges.impl.data.info.entity.entityinformation.VillagerData;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EntityInformationDataProvider implements DataProvider<EntityInformationData> {
    @Override
    public @NotNull List<Class<? extends EntityInformationData>> provide() {
        return List.of(
                ItemFrameData.class,
                PaintingData.class,
                VillagerData.class
        );
    }
}
