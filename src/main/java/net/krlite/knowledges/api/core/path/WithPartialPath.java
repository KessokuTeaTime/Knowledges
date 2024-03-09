package net.krlite.knowledges.api.core.path;

import org.jetbrains.annotations.NotNull;

public interface WithPartialPath extends WithPath {
    @NotNull String partialPath();

    @NotNull String currentPath();

    @Override
    default @NotNull String path() {
        return currentPath() + "." + partialPath();
    }
}
