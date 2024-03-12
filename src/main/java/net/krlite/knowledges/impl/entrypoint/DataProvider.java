package net.krlite.knowledges.impl.entrypoint;

import net.krlite.knowledges.api.data.Data;
import net.krlite.knowledges.impl.data.info.block.MineableToolData;
import net.krlite.knowledges.impl.data.info.block.blockinformation.*;
import net.krlite.knowledges.impl.data.info.entity.entitydescription.AnimalOwnerData;
import net.krlite.knowledges.impl.data.info.entity.entityinformation.ItemFrameData;
import net.krlite.knowledges.impl.data.info.entity.entityinformation.PaintingData;
import net.krlite.knowledges.impl.data.info.entity.entityinformation.VillagerData;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DataProvider implements net.krlite.knowledges.api.entrypoint.DataProvider.Global {
    @Override
    public @NotNull List<Class<? extends Data<?>>> provide() {
        return List.of(
                MineableToolData.class,

                NoteBlockData.class,
                BannerData.class,
                ComposterData.class,
                RedstoneWireData.class,
                CropData.class,
                SaplingData.class,
                BeehiveData.class,

                PaintingData.class,
                ItemFrameData.class,
                VillagerData.class,

                AnimalOwnerData.class,
                net.krlite.knowledges.impl.data.info.entity.entitydescription.ItemFrameData.class,
                net.krlite.knowledges.impl.data.info.entity.entitydescription.VillagerData.class
        );
    }
}
