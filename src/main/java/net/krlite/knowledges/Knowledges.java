package net.krlite.knowledges;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.krlite.knowledges.api.entrypoints.ComponentProvider;
import net.krlite.knowledges.api.entrypoints.DataProvider;
import net.krlite.knowledges.core.util.Helper;
import net.krlite.knowledges.core.WithPath;
import net.krlite.knowledges.components.InfoComponent;
import net.krlite.knowledges.config.KnowledgesConfig;
import net.krlite.knowledges.config.disabled.SimpleDisabledConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Knowledges implements ModInitializer {
    public static final String NAME = "Knowledges", ID = "knowledges";
    public static final Logger LOGGER = LoggerFactory.getLogger(ID);
    public static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve(Knowledges.ID);

    public static final KnowledgesConfig CONFIG = new KnowledgesConfig();
    public static final ComponentManager COMPONENTS = new ComponentManager();
    public static final DataManager DATA = new DataManager();

    public static String localizationKey(String category, String... paths) {
        return category + "." + ID + "." + String.join(".", paths);
    }

    public static MutableText localize(String category, String... paths) {
        return Text.translatable(localizationKey(category, paths));
    }

    public static void render(
            @NotNull DrawContext context, @NotNull MinecraftClient client,
            @NotNull PlayerEntity player, @NotNull ClientWorld world
    ) {
        COMPONENTS.asList().forEach(knowledge -> {
            if (COMPONENTS.isEnabled(knowledge)) knowledge.render(context, client, player, world);
        });
    }

    @Override
    public void onInitialize() {
        InfoComponent.Animations.registerEvents();

        // Components
        FabricLoader.getInstance().getEntrypointContainers(ID, ComponentProvider.class).forEach(entrypoint -> {
            ComponentProvider provider = entrypoint.getEntrypoint();
            var classes = provider.provide();
            if (classes.isEmpty()) return;

            ModContainer mod = entrypoint.getProvider();
            String namespace = mod.getMetadata().getId(), name = mod.getMetadata().getName();

            LOGGER.info(String.format(
                    "Registering %d %s for %s.",
                    classes.size(),
                    classes.size() <= 1 ? "knowledge" : "knowledges",
                    name
            ));

            classes.stream()
                    .map(clazz -> {
                        try {
                            return clazz.getDeclaredConstructor().newInstance();
                        } catch (Throwable throwable) {
                            throw new RuntimeException(String.format(
                                    "Failed to register knowledge for %s: constructor not found",
                                    clazz.getName()
                            ), throwable);
                        }
                    })
                    .forEach(knowledge -> COMPONENTS.register(namespace, knowledge));
        });

        // Data
        FabricLoader.getInstance().getEntrypointContainers(ID + "_data", DataProvider.class).forEach(entrypoint -> {
            DataProvider provider = entrypoint.getEntrypoint();
            var classes = provider.provide();
            if (classes.isEmpty()) return;

            ModContainer mod = entrypoint.getProvider();
            String namespace = mod.getMetadata().getId(), name = mod.getMetadata().getName();

            LOGGER.info(String.format(
                    "Registering %d %s for %s.",
                    classes.size(),
                    "data",
                    name
            ));

            classes.stream()
                    .map(clazz -> {
                        try {
                            return clazz.getDeclaredConstructor().newInstance();
                        } catch (Throwable throwable) {
                            throw new RuntimeException(String.format(
                                    "Failed to register knowledge data for %s: constructor not found",
                                    clazz.getName()
                            ), throwable);
                        }
                    })
                    .forEach(data -> DATA.register(namespace, data));
        });

        if (!COMPONENTS.asMap().isEmpty()) {
            LOGGER.info(String.format(
                    "Successfully registered %d %s for %d %s and %d %s for %d %s. %s you wiser! 📚",

                    COMPONENTS.asList().size(),
                    COMPONENTS.asList().size() <= 1 ? "knowledge" : "knowledges",
                    COMPONENTS.asMap().keySet().size(),
                    COMPONENTS.asMap().keySet().size() <= 1 ? "mod" : "mods",

                    DATA.asList().size(),
                    "data",
                    DATA.asMap().keySet().size(),
                    DATA.asMap().keySet().size() <= 1 ? "mod" : "mods",

                    COMPONENTS.asList().size() + DATA.asList().size() <= 1 ? "It makes" : "They make"
            ));
        }

        DATA.asList().forEach(d -> System.out.println(d.knowledge()));
    }

    static abstract class Manager<T extends WithPath> {
        private final HashMap<String, List<T>> map = new HashMap<>();
        private final SimpleDisabledConfig<T> disabled;

        Manager(SimpleDisabledConfig<T> disabled) {
            this.disabled = disabled;
        }

        public Map<String, List<T>> asMap() {
            return Map.copyOf(map);
        }

        public List<T> asList() {
            return asMap().values().stream()
                    .flatMap(List::stream)
                    .toList();
        }

        public Optional<T> byClass(Class<? extends T> tClass) {
            return asList().stream()
                    .filter(tClass::isInstance)
                    .findAny();
        }

        public Optional<T> byId(String namespace, String... paths) {
            return Optional.ofNullable(asMap().get(namespace))
                    .flatMap(list -> list.stream()
                            .filter(t -> t.path().equals(String.join(".", paths)))
                            .findAny());
        }

        public Optional<T> byId(Identifier identifier) {
            return byId(identifier.getNamespace(), identifier.getPath());
        }

        public Optional<String> namespace(T t) {
            return asMap().entrySet().stream()
                    .filter(entry -> entry.getValue().contains(t))
                    .findAny()
                    .map(Map.Entry::getKey);
        }

        public Optional<Identifier> identifier(T t) {
            return namespace(t)
                    .map(namespace -> new Identifier(namespace, t.path()));
        }

        protected abstract String localizationPrefix();

        public String localizationKey(T t, String... paths) {
            String namespace = namespace(t).orElse(Knowledges.ID);
            return localizationPrefix() + "." + namespace + "." + String.join(".", paths);
        }

        public boolean isInNamespace(T t, String namespace) {
            return namespace(t).equals(Optional.of(namespace));
        }

        public boolean isInDefaultNamespace(T t) {
            return isInNamespace(t, Knowledges.ID);
        }

        public boolean isEnabled(T t) {
            return !disabled.get(t);
        }

        public void setEnabled(T t, boolean enabled) {
            disabled.set(t, !enabled);
        }

        void register(String namespace, T t) {
            Helper.Map.fastMerge(map, namespace, t);
        }
    }
}
