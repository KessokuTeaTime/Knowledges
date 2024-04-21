package band.kessokuteatime.knowledges.manager.base;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import band.kessokuteatime.knowledges.KnowledgesCommon;
import band.kessokuteatime.knowledges.api.core.localization.Localizable;
import band.kessokuteatime.knowledges.api.entrypoint.ComponentProvider;
import band.kessokuteatime.knowledges.api.entrypoint.ContractProvider;
import band.kessokuteatime.knowledges.api.entrypoint.DataProvider;
import band.kessokuteatime.knowledges.api.entrypoint.base.Provider;

import java.util.function.BiConsumer;

public enum EntrypointInvoker {
    COMPONENT(true, ComponentProvider.class, "component"),
    DATA(true, DataProvider.class, "data", "data", "data"),
    CONTRACT(false, ContractProvider.class, "contract");

    @FunctionalInterface
    interface Registerable<T extends Provider<?>, A> {
        BiConsumer<String, A> consumer();

        default void register(EntrypointInvoker invoker, Class<T> clazz) {
            FabricLoader.getInstance().getEntrypointContainers(invoker.entrypointPath(), clazz).forEach(entrypoint -> {
                T provider = entrypoint.getEntrypoint();
                var classes = provider.provideAll();
                if (classes.isEmpty()) return;

                ModContainer mod = entrypoint.getProvider();
                String namespace = mod.getMetadata().getId(), name = mod.getMetadata().getName();

                KnowledgesCommon.LOGGER.info(String.format(
                        "Registering %d %s for %s...",
                        classes.size(),
                        classes.size() <= 1 ? invoker.single : invoker.plural,
                        name
                ));

                classes.stream()
                        .distinct()
                        .map(c -> {
                            try {
                                return (A) c.getDeclaredConstructor().newInstance();
                            } catch (Throwable throwable) {
                                throw new RuntimeException(String.format(
                                        "Failed to register %s for %s: constructor not found!",
                                        invoker.single,
                                        c.getName()
                                ), throwable);
                            }
                        })
                        .forEach(a -> consumer().accept(namespace, a));
            });
        }
    }

    private final boolean isClientSide;
    private final String path;
    private final String single, plural;
    private final Class<?> clazz;

    EntrypointInvoker(boolean isClientSide, Class<?> clazz, String path, String single, String plural) {
        this.isClientSide = isClientSide;
        this.clazz = clazz;
        this.path = path;
        this.single = single;
        this.plural = plural;
    }

    EntrypointInvoker(boolean isClientSide, Class<?> clazz, String path) {
        this(isClientSide, clazz, path, path, path + "s");
    }

    public String path() {
        return path;
    }

    public String entrypointPath() {
        return KnowledgesCommon.ID + Localizable.Separator.REALM + path();
    }

    public boolean isClientSide() {
        return isClientSide;
    }

    public <T extends Provider<?>, A> void invoke(BiConsumer<String, A> consumer) {
        try {
            ((Registerable<T, A>) () -> consumer).register(this, (Class<T>) clazz);
        } catch (Exception e) {
            KnowledgesCommon.LOGGER.error("Failed invoking register methods for class {}!", clazz, e);
        }
    }
}
