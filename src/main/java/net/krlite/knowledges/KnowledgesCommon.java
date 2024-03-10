package net.krlite.knowledges;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.krlite.knowledges.api.entrypoint.TagProvider;
import net.krlite.knowledges.config.KnowledgesClientConfig;
import net.krlite.knowledges.config.KnowledgesCommonConfig;
import net.krlite.knowledges.config.modmenu.cache.UsernameCache;
import net.krlite.knowledges.manager.KnowledgesTagManager;
import net.krlite.knowledges.networking.ServerNetworking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KnowledgesCommon implements ModInitializer {
    public static final String NAME = "Knowledges", ID = "knowledges";
    public static final Logger LOGGER = LoggerFactory.getLogger(ID + ":common");

    public static final ConfigHolder<KnowledgesCommonConfig> CONFIG_HOLDER;
    public static final KnowledgesCommonConfig CONFIG;
    public static final KnowledgesCommonConfig DEFAULT_CONFIG = new KnowledgesCommonConfig();

    static {
        AutoConfig.register(KnowledgesCommonConfig.class, PartitioningSerializer.wrap(Toml4jConfigSerializer::new));
        CONFIG_HOLDER = AutoConfig.getConfigHolder(KnowledgesCommonConfig.class);
        CONFIG = CONFIG_HOLDER.getConfig();
    }

    public static final KnowledgesTagManager TAGS = new KnowledgesTagManager();

    public static final UsernameCache CACHE_USERNAME = new UsernameCache();

    @Override
    public void onInitialize() {
        TAGS.fixKeys();
        CACHE_USERNAME.load();

        new ServerNetworking().register();

        // Tags
        FabricLoader.getInstance().getEntrypointContainers(ID + ":tags", TagProvider.class).forEach(entrypoint -> {
            TagProvider provider = entrypoint.getEntrypoint();
            var classes = provider.provide();
            if (classes.isEmpty()) return;

            ModContainer mod = entrypoint.getProvider();
            String namespace = mod.getMetadata().getId(), name = mod.getMetadata().getName();

            LOGGER.info(String.format(
                    "Registering %d %s for %s...",
                    classes.size(),
                    classes.size() <= 1 ? "tag" : "tags",
                    name
            ));

            classes.stream()
                    .distinct()
                    .map(clazz -> {
                        try {
                            return clazz.getDeclaredConstructor().newInstance();
                        } catch (Throwable throwable) {
                            throw new RuntimeException(String.format(
                                    "Failed to register tag for %s: constructor not found",
                                    clazz.getName()
                            ), throwable);
                        }
                    })
                    .forEach(tag -> TAGS.register(namespace, tag));
        });

        tidyUpConfig();
        CONFIG_HOLDER.save();
    }

    public static void tidyUpConfig() {
        TAGS.tidyUp();
    }
}
