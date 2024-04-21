package band.kessokuteatime.knowledges.impl.entrypoint.data.info;

import band.kessokuteatime.knowledges.api.entrypoint.DataProvider;
import band.kessokuteatime.knowledges.api.entrypoint.base.Provider;
import band.kessokuteatime.knowledges.impl.data.info.base.EntityInfoComponentData;
import band.kessokuteatime.knowledges.impl.entrypoint.data.info.entity.EntityDescriptionDataProvider;
import band.kessokuteatime.knowledges.impl.entrypoint.data.info.entity.EntityInformationDataProvider;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class EntityInfoComponentDataProvider implements DataProvider<EntityInfoComponentData> {
    @Override
    public @NotNull List<Class<? extends EntityInfoComponentData>> provide() {
        return new ArrayList<>();
    }

    @Override
    public @NotNull List<Class<? extends Provider<? extends EntityInfoComponentData>>> provideNested() {
        return List.of(
                EntityDescriptionDataProvider.class,
                EntityInformationDataProvider.class
        );
    }
}
