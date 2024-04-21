package band.kessokuteatime.knowledges.impl.entrypoint.data.info.block;

import band.kessokuteatime.knowledges.api.entrypoint.DataProvider;
import band.kessokuteatime.knowledges.impl.data.info.block.MineableToolData;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MineableToolDataProvider implements DataProvider<MineableToolData> {
    @Override
    public @NotNull List<Class<? extends MineableToolData>> provide() {
        return List.of(
                MineableToolData.class
        );
    }
}
