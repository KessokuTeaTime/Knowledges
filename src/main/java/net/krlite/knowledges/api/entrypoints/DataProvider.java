package net.krlite.knowledges.api.entrypoints;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface DataProvider {
    @NotNull List<Class<? extends Data<?, ?>>> provide();
}
