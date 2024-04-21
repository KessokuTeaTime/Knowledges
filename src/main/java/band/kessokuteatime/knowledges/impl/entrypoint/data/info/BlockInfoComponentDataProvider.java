package band.kessokuteatime.knowledges.impl.entrypoint.data.info;

import band.kessokuteatime.knowledges.api.entrypoint.DataProvider;
import band.kessokuteatime.knowledges.api.entrypoint.base.Provider;
import band.kessokuteatime.knowledges.impl.data.info.base.BlockInfoComponentData;
import band.kessokuteatime.knowledges.impl.entrypoint.data.info.block.BlockInformationDataProvider;
import band.kessokuteatime.knowledges.impl.entrypoint.data.info.block.MineableToolDataProvider;
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
