package band.kessokuteatime.knowledges;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import band.kessokuteatime.knowledges.config.ClientConfig;
import band.kessokuteatime.knowledges.manager.ComponentManager;
import band.kessokuteatime.knowledges.manager.DataManager;
import band.kessokuteatime.knowledges.manager.base.EntrypointInvoker;
import band.kessokuteatime.knowledges.manager.base.Manager;
import band.kessokuteatime.knowledges.networking.ClientNetworking;
import net.minecraft.util.ActionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KnowledgesClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger(KnowledgesCommon.ID + "-client");

    public static final ConfigHolder<ClientConfig> CONFIG;
    public static final ClientConfig DEFAULT_CONFIG = new ClientConfig();

    static {
        AutoConfig.register(ClientConfig.class, PartitioningSerializer.wrap(Toml4jConfigSerializer::new));
        CONFIG = AutoConfig.getConfigHolder(ClientConfig.class);

        CONFIG.registerLoadListener((configHolder, clientConfig) -> {
            Manager.fixKeysAndSort(clientConfig.components.available);
            Manager.fixKeysAndSort(clientConfig.data.available);

            return ActionResult.PASS;
        });
    }

    public static final KnowledgesHud HUD = new KnowledgesHud();

    // Managers
    public static final ComponentManager COMPONENTS = new ComponentManager();
    public static final DataManager DATA = new DataManager();

    @Override
    public void onInitializeClient() {
        Manager.fixKeysAndSort(CONFIG.get().components.available);
        Manager.fixKeysAndSort(CONFIG.get().data.available);

        new ClientNetworking().register();

        EntrypointInvoker.COMPONENT.invoke(COMPONENTS::register);
        EntrypointInvoker.DATA.invoke(DATA::register);

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
}
