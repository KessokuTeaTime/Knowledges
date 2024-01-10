package net.krlite.knowledges.impl;

import net.krlite.knowledges.api.Data;
import net.krlite.knowledges.api.entrypoint.KnowledgesDataProvider;
import net.krlite.knowledges.data.info.block.MineableToolData;
import net.krlite.knowledges.data.info.block.blockinformation.BannerBlockInformationData;
import net.krlite.knowledges.data.info.block.blockinformation.ComposterBlockInformationData;
import net.krlite.knowledges.data.info.block.blockinformation.NoteBlockInformationData;
import net.krlite.knowledges.data.info.entity.entitydescription.ItemFrameEntityDescriptionData;
import net.krlite.knowledges.data.info.entity.entitydescription.VillagerEntityDescriptionData;
import net.krlite.knowledges.data.info.entity.entityinformation.ItemFrameEntityInformationData;
import net.krlite.knowledges.data.info.entity.entityinformation.PaintingEntityInformationData;
import net.krlite.knowledges.data.info.entity.entityinformation.VillagerEntityInformationData;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DefaultKnowledgesDataProvider implements KnowledgesDataProvider {
    @Override
    public @NotNull List<Class<? extends Data<?>>> provide() {
        return List.of(
                MineableToolData.class,
                NoteBlockInformationData.class,
                BannerBlockInformationData.class,
                ComposterBlockInformationData.class,

                PaintingEntityInformationData.class,
                ItemFrameEntityInformationData.class,
                VillagerEntityInformationData.class,
                ItemFrameEntityDescriptionData.class,
                VillagerEntityDescriptionData.class
        );
    }
}
