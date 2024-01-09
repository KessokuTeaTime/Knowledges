package net.krlite.knowledges.impl;

import net.krlite.knowledges.api.Data;
import net.krlite.knowledges.api.entrypoints.DataProvider;
import net.krlite.knowledges.data.info.block.MineableToolData;
import net.krlite.knowledges.data.info.block.blockinfo.BannerData;
import net.krlite.knowledges.data.info.block.blockinfo.NoteBlockData;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DefaultDataProvider implements DataProvider {
    @Override
    public @NotNull List<Class<? extends Data<?>>> provide() {
        return List.of(
                MineableToolData.class,
                NoteBlockData.class,
                BannerData.class
        );
    }
}
