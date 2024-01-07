package net.krlite.knowledges.api;

import net.krlite.knowledges.Knowledges;
import net.krlite.knowledges.core.LocalizableWithName;
import net.krlite.knowledges.core.WithPath;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface Data<P, R> extends WithPath, LocalizableWithName {
    Optional<R> get(P param);

    Class<? extends Knowledge> targetClass();

    default Optional<Knowledge> targetKnowledge() {
        return Knowledges.COMPONENTS.byClass(targetClass());
    }

    Identifier target();

    @Override
    default String localizationKey(String... paths) {
        List<String> fullPaths = new ArrayList<>(List.of(path()));
        fullPaths.addAll(List.of(paths));

        return Knowledges.DATA.localizationKey(this, fullPaths.toArray(String[]::new));
    }
}
