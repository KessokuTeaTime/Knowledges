package net.krlite.knowledges.api;

import net.krlite.knowledges.Knowledges;
import net.krlite.knowledges.core.DataCallback;
import net.krlite.knowledges.core.localization.LocalizableWithName;
import net.krlite.knowledges.core.HasEvent;
import net.krlite.knowledges.core.path.WithPath;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface Data<T extends Enum<T> & HasEvent, C extends DataCallback<T>> extends WithPath, LocalizableWithName, HasEvent.HasSource<T> {
    C listener();

    default void registerListener() {
        source().event().register();
    }

    Class<? extends Knowledge> knowledgeClass();

    default Optional<Knowledge> knowledge() {
        return Knowledges.COMPONENTS.byClass(knowledgeClass());
    }

    @Override
    default String localizationKey(String... paths) {
        List<String> fullPaths = new ArrayList<>(List.of(path()));
        fullPaths.addAll(List.of(paths));

        return Knowledges.DATA.localizationKey(this, fullPaths.toArray(String[]::new));
    }
}
