package net.krlite.knowledges.impl.entrypoint.data.info.entity;

import net.krlite.knowledges.api.entrypoint.DataProvider;
import net.krlite.knowledges.impl.data.info.base.entity.EntityInformationData;
import net.krlite.knowledges.impl.data.info.entity.entityinformation.ItemFrameData;
import net.krlite.knowledges.impl.data.info.entity.entityinformation.PaintingData;
import net.krlite.knowledges.impl.data.info.entity.entityinformation.VillagerData;
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
