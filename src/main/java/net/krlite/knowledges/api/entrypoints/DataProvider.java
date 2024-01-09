package net.krlite.knowledges.api.entrypoints;

import net.krlite.knowledges.api.Data;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface DataProvider {
    @NotNull List<Class<? extends Data<?, ?, ?>>> provide();
}
