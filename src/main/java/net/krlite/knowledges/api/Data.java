package net.krlite.knowledges.api;

import net.krlite.knowledges.Knowledges;
import net.krlite.knowledges.core.DataEvent;
import net.krlite.knowledges.core.localization.LocalizableWithName;
import net.krlite.knowledges.core.Target;
import net.krlite.knowledges.core.path.WithPath;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface Data<T extends Enum<T> & Target, E extends DataEvent> extends WithPath, LocalizableWithName, Target.Consumer<T> {
    E listener();

    default void registerListener() {
        target().event().register(listener());
    }

    Class<? extends Knowledge<?>> knowledgeClass();

    default Optional<Knowledge<?>> knowledge() {
        return Knowledges.COMPONENTS.byClass(knowledgeClass());
    }

    @Override
    default String localizationKey(String... paths) {
        List<String> fullPaths = new ArrayList<>(List.of(path()));
        fullPaths.addAll(List.of(paths));

        return Knowledges.DATA.localizationKey(this, fullPaths.toArray(String[]::new));
    }
}
