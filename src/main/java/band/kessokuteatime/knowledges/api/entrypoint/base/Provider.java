package band.kessokuteatime.knowledges.api.entrypoint.base;

import band.kessokuteatime.knowledges.KnowledgesCommon;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * An entrypoint to provide api implementations for register.
 * @param <T>   the type of the provided data.
 */
public interface Provider<T> {
    @NotNull List<Class<? extends T>> provide();

    default @NotNull List<Class<? extends Provider<? extends T>>> provideNested() {
        return new ArrayList<>();
    }

    default @NotNull List<? extends Class<? extends T>> provideAll() {
        List<? extends Provider<? extends T>> nested = provideNested().stream()
                .map(c -> {
                    try {
                        return c.getDeclaredConstructor().newInstance();
                    } catch (Exception e) {
                        KnowledgesCommon.LOGGER.error("Failed creating child provider {} for {}: constructor not found!", c, getClass(), e);
                    }

                    return null;
                })
                .filter(Objects::nonNull)
                .toList();

        List<? extends Class<? extends T>> nestedProvided = nested.stream()
                .map(Provider::provideAll)
                .flatMap(List::stream)
                .toList();

        var provided = new ArrayList<>(List.copyOf(provide()));
        if (provided.isEmpty()) return nestedProvided;

        provided.addAll(nestedProvided);
        return provided;
    }
}
