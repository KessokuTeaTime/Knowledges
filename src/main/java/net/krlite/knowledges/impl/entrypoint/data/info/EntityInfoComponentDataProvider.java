package net.krlite.knowledges.impl.entrypoint.data.info;

import net.krlite.knowledges.api.entrypoint.DataProvider;
import net.krlite.knowledges.api.entrypoint.base.Provider;
import net.krlite.knowledges.impl.data.info.base.EntityInfoComponentData;
import net.krlite.knowledges.impl.entrypoint.data.info.entity.EntityDescriptionDataProvider;
import net.krlite.knowledges.impl.entrypoint.data.info.entity.EntityInformationDataProvider;
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
