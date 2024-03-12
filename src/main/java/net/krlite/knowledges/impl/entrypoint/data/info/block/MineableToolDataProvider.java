package net.krlite.knowledges.impl.entrypoint.data.info.block;

import net.krlite.knowledges.api.entrypoint.DataProvider;
import net.krlite.knowledges.impl.data.info.block.MineableToolData;
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
