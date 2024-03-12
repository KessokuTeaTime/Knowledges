package net.krlite.knowledges.impl.entrypoint.data.info.entity;

import net.krlite.knowledges.api.entrypoint.DataProvider;
import net.krlite.knowledges.impl.data.info.base.entity.EntityInformationData;
import net.krlite.knowledges.impl.data.info.entity.entityinformation.ItemFrameEntityInformationData;
import net.krlite.knowledges.impl.data.info.entity.entityinformation.PaintingEntityInformationData;
import net.krlite.knowledges.impl.data.info.entity.entityinformation.VillagerEntityInformationData;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EntityInformationDataProvider implements DataProvider<EntityInformationData> {
    @Override
    public @NotNull List<Class<? extends EntityInformationData>> provide() {
        return List.of(
                PaintingEntityInformationData.class,
                ItemFrameEntityInformationData.class,
                VillagerEntityInformationData.class
        );
    }
}
