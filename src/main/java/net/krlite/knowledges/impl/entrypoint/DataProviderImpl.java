package net.krlite.knowledges.impl.entrypoint;

import net.krlite.knowledges.api.data.Data;
import net.krlite.knowledges.api.entrypoint.DataProvider;
import net.krlite.knowledges.impl.data.info.block.MineableToolData;
import net.krlite.knowledges.impl.data.info.block.blockinformation.*;
import net.krlite.knowledges.impl.data.info.entity.entitydescription.AnimalOwnerEntityDescriptionData;
import net.krlite.knowledges.impl.data.info.entity.entitydescription.ItemFrameEntityDescriptionData;
import net.krlite.knowledges.impl.data.info.entity.entitydescription.VillagerEntityDescriptionData;
import net.krlite.knowledges.impl.data.info.entity.entityinformation.ItemFrameEntityInformationData;
import net.krlite.knowledges.impl.data.info.entity.entityinformation.PaintingEntityInformationData;
import net.krlite.knowledges.impl.data.info.entity.entityinformation.VillagerEntityInformationData;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DataProviderImpl implements DataProvider {
    @Override
    public @NotNull List<Class<? extends Data<?>>> provide() {
        return List.of(
                MineableToolData.class,

                NoteBlockInformationData.class,
                BannerBlockInformationData.class,
                ComposterBlockInformationData.class,
                RedstoneWireBlockInformationData.class,
                CropBlockInformationData.class,
                SaplingBlockInformationData.class,
                BeehiveBlockInformationData.class,

                PaintingEntityInformationData.class,
                ItemFrameEntityInformationData.class,
                VillagerEntityInformationData.class,

                AnimalOwnerEntityDescriptionData.class,
                ItemFrameEntityDescriptionData.class,
                VillagerEntityDescriptionData.class
        );
    }
}
