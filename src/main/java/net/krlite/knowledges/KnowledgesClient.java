package net.krlite.knowledges;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.krlite.knowledges.api.entrypoint.ComponentProvider;
import net.krlite.knowledges.api.entrypoint.DataProvider;
import net.krlite.knowledges.api.representable.PacketByteBufWritable;
import net.krlite.knowledges.config.KnowledgesClientConfig;
import net.krlite.knowledges.impl.component.AbstractInfoComponent;
import net.krlite.knowledges.manager.KnowledgesComponentManager;
import net.krlite.knowledges.manager.KnowledgesDataManager;
import net.krlite.knowledges.manager.KnowledgesManager;
import net.krlite.knowledges.networking.ClientNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KnowledgesClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger(KnowledgesCommon.ID + ":client");

    public static final ConfigHolder<KnowledgesClientConfig> CONFIG;
    public static final KnowledgesClientConfig DEFAULT_CONFIG = new KnowledgesClientConfig();

    static {
        AutoConfig.register(KnowledgesClientConfig.class, PartitioningSerializer.wrap(Toml4jConfigSerializer::new));
        CONFIG = AutoConfig.getConfigHolder(KnowledgesClientConfig.class);

        CONFIG.registerLoadListener((configHolder, knowledgesClientConfig) -> {
            KnowledgesManager.fixKeysAndSort(knowledgesClientConfig.components.enabled);
            KnowledgesManager.fixKeysAndSort(knowledgesClientConfig.data.enabled);

            return ActionResult.PASS;
        });
    }

    public static final KnowledgesComponentManager COMPONENTS = new KnowledgesComponentManager();
    public static final KnowledgesDataManager DATA = new KnowledgesDataManager();

    public static final KnowledgesHud HUD = new KnowledgesHud();

    public static Identifier identifier(String path) {
        return new Identifier(KnowledgesCommon.ID, path);
    }

    public static String localizationKey(String category, String... paths) {
        return category + "." + KnowledgesCommon.ID + "." + String.join(".", paths);
    }

    public static MutableText localize(String category, String... paths) {
        return Text.translatable(localizationKey(category, paths));
    }

    @Override
    public void onInitializeClient() {
        KnowledgesManager.fixKeysAndSort(CONFIG.get().components.enabled);
        KnowledgesManager.fixKeysAndSort(CONFIG.get().data.enabled);

        AbstractInfoComponent.Animation.registerEvents();
        new ClientNetworking().register();

        // Components
        FabricLoader.getInstance().getEntrypointContainers(KnowledgesCommon.ID, ComponentProvider.class).forEach(entrypoint -> {
            ComponentProvider provider = entrypoint.getEntrypoint();
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
        FabricLoader.getInstance().getEntrypointContainers(KnowledgesCommon.ID + ":data", DataProvider.class).forEach(entrypoint -> {
            DataProvider provider = entrypoint.getEntrypoint();
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

        tidyUpConfig();
        CONFIG.save();

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

        ClientTickEvents.END_CLIENT_TICK.register(HUD::tick);
        HudRenderCallback.EVENT.register(((context, tickDelta) -> {
            HUD.render(context, COMPONENTS::render);
        }));
    }

    public static void tidyUpConfig() {
        if (CONFIG.get().components.autoTidiesUp)
            COMPONENTS.tidyUp();
        if (CONFIG.get().data.autoTidiesUp)
            DATA.tidyUp();
    }

    public static void requestDataFor(PacketByteBufWritable writable, Identifier channel) {
        PacketByteBuf buf = PacketByteBufs.create();
        writable.writeToBuf(buf);
        ClientPlayNetworking.send(channel, buf);
    }
}
