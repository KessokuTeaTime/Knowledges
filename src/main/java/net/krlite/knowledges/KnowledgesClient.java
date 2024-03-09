package net.krlite.knowledges;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.krlite.knowledges.api.entrypoint.KnowledgesComponentProvider;
import net.krlite.knowledges.api.entrypoint.KnowledgesDataProvider;
import net.krlite.knowledges.component.AbstractInfoComponent;
import net.krlite.knowledges.config.KnowledgesConfig;
import net.krlite.knowledges.manager.KnowledgesComponentManager;
import net.krlite.knowledges.manager.KnowledgesDataManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KnowledgesClient implements ClientModInitializer {
    public static final String NAME = "Knowledges", ID = "knowledges";
    public static final Logger LOGGER = LoggerFactory.getLogger(ID);

    public static final ConfigHolder<KnowledgesConfig> CONFIG_HOLDER;
    public static final KnowledgesConfig CONFIG;
    public static final KnowledgesConfig DEFAULT_CONFIG = new KnowledgesConfig();

    public static final KnowledgesComponentManager COMPONENTS = new KnowledgesComponentManager();
    public static final KnowledgesDataManager DATA = new KnowledgesDataManager();

    static {
        AutoConfig.register(KnowledgesConfig.class, PartitioningSerializer.wrap(Toml4jConfigSerializer::new));
        CONFIG_HOLDER = AutoConfig.getConfigHolder(KnowledgesConfig.class);
        CONFIG = CONFIG_HOLDER.get();
    }

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
    public void onInitializeClient() {
        AbstractInfoComponent.Animation.registerEvents();

        // Components
        FabricLoader.getInstance().getEntrypointContainers(ID, KnowledgesComponentProvider.class).forEach(entrypoint -> {
            KnowledgesComponentProvider provider = entrypoint.getEntrypoint();
            var classes = provider.provide();
            if (classes.isEmpty()) return;

            ModContainer mod = entrypoint.getProvider();
            String namespace = mod.getMetadata().getId(), name = mod.getMetadata().getName();

            LOGGER.info(String.format(
                    "Registering %d %s for %s...",
                    classes.size(),
                    classes.size() <= 1 ? "knowledge" : "knowledges",
                    name
            ));

            classes.stream()
                    .distinct()
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
        FabricLoader.getInstance().getEntrypointContainers(ID + "_data", KnowledgesDataProvider.class).forEach(entrypoint -> {
            KnowledgesDataProvider provider = entrypoint.getEntrypoint();
            var classes = provider.provide();
            if (classes.isEmpty()) return;

            ModContainer mod = entrypoint.getProvider();
            String namespace = mod.getMetadata().getId(), name = mod.getMetadata().getName();

            LOGGER.info(String.format(
                    "Registering %d %s for %s...",
                    classes.size(),
                    "data",
                    name
            ));

            classes.stream()
                    .distinct()
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
    }
}
