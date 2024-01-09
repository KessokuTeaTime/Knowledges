package net.krlite.knowledges.data;

import net.krlite.knowledges.api.Data;
import net.krlite.knowledges.api.Knowledge;
import net.krlite.knowledges.components.info.BlockInfoComponent;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class TestData implements Data<BlockInfoComponent.BlockInfoTarget, Integer, String> {
    @Override
    public Optional<String> get(Integer param) {
        return Optional.ofNullable(String.valueOf(param));
    }

    @Override
    public BlockInfoComponent.BlockInfoTarget target() {
        return BlockInfoComponent.BlockInfoTarget.TEST;
    }

    @Override
    public Class<? extends Knowledge<?>> targetClass() {
        return BlockInfoComponent.class;
    }

    @Override
    public @NotNull String path() {
        return "test";
    }
}
