package net.krlite.knowledges.data;

import net.krlite.knowledges.api.Data;
import net.krlite.knowledges.api.Knowledge;
import net.krlite.knowledges.components.InfoComponent;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class TestData implements Data<InfoComponent.InfoTarget, Integer, String> {
    @Override
    public Optional<String> get(Integer param) {
        return Optional.ofNullable(String.valueOf(param));
    }

    @Override
    public InfoComponent.InfoTarget target() {
        return InfoComponent.InfoTarget.TEST;
    }

    @Override
    public Class<? extends Knowledge<?>> targetClass() {
        return InfoComponent.class;
    }

    @Override
    public @NotNull String path() {
        return "test";
    }
}
