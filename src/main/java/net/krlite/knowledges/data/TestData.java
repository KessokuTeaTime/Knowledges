package net.krlite.knowledges.data;

import net.krlite.knowledges.api.Data;
import net.krlite.knowledges.api.Knowledge;
import net.krlite.knowledges.components.info.BlockInfoComponent;
import org.jetbrains.annotations.NotNull;

public class TestData implements Data<Integer, String> {
    @Override
    public String get(Integer param) {
        return String.valueOf(param);
    }

    @Override
    public Class<? extends Knowledge> targetClass() {
        return BlockInfoComponent.class;
    }

    @Override
    public @NotNull String path() {
        return "test";
    }
}
