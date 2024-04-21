package band.kessokuteatime.knowledges.api.core.path;

import band.kessokuteatime.knowledges.api.core.localization.Localizable;
import org.jetbrains.annotations.NotNull;

public interface WithPartialPath extends WithPath {
    @NotNull String partialPath();

    @NotNull String currentPath();

    @Override
    default @NotNull String path() {
        return currentPath() + Localizable.Separator.RANK + partialPath();
    }
}
