package net.krlite.knowledges.impl.entrypoint.data.info;

import net.krlite.knowledges.api.entrypoint.DataProvider;
import net.krlite.knowledges.api.entrypoint.base.Provider;
import net.krlite.knowledges.impl.data.info.base.BlockInfoComponentData;
import net.krlite.knowledges.impl.entrypoint.data.info.block.BlockInformationDataProvider;
import net.krlite.knowledges.impl.entrypoint.data.info.block.MineableToolDataProvider;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BlockInfoComponentDataProvider implements DataProvider<BlockInfoComponentData> {
    @Override
    public @NotNull List<Class<? extends BlockInfoComponentData>> provide() {
        return new ArrayList<>();
    }

    @Override
    public @NotNull List<Class<? extends Provider<? extends BlockInfoComponentData>>> provideNested() {
        return List.of(
                BlockInformationDataProvider.class,
                MineableToolDataProvider.class
        );
    }
}
