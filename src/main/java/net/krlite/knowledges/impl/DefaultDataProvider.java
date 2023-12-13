package net.krlite.knowledges.impl;

import net.krlite.knowledges.api.entrypoints.DataProvider;
import net.krlite.knowledges.data.TestData;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DefaultDataProvider implements DataProvider {
    @Override
    public @NotNull List<Class<? extends Data<?, ?>>> provide() {
        return List.of(
                TestData.class
        );
    }
}
