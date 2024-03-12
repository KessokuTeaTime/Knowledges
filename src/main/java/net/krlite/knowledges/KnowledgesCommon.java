package net.krlite.knowledges;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.krlite.knowledges.config.CommonConfig;
import net.krlite.knowledges.config.cache.UsernameCache;
import net.krlite.knowledges.manager.ContractManager;
import net.krlite.knowledges.manager.base.EntrypointInvoker;
import net.krlite.knowledges.manager.base.Manager;
import net.krlite.knowledges.networking.ServerNetworking;
import net.minecraft.util.ActionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KnowledgesCommon implements ModInitializer {
    public static final String NAME = "Knowledges", ID = "knowledges";
    public static final Logger LOGGER = LoggerFactory.getLogger(ID);


    public static final ConfigHolder<CommonConfig> CONFIG;
    public static final CommonConfig DEFAULT_CONFIG = new CommonConfig();

    static {
        AutoConfig.register(CommonConfig.class, PartitioningSerializer.wrap(Toml4jConfigSerializer::new));
        CONFIG = AutoConfig.getConfigHolder(CommonConfig.class);

        CONFIG.registerLoadListener((configHolder, commonConfig) -> {
            Manager.fixKeysAndSort(commonConfig.contracts.available);

            return ActionResult.PASS;
        });
    }

    public static final UsernameCache CACHE_USERNAME = new UsernameCache();

    // Managers
    public static final ContractManager CONTRACTS = new ContractManager();

    @Override
    public void onInitialize() {
        CACHE_USERNAME.load();
        Manager.fixKeysAndSort(CONFIG.get().contracts.available);

        new ServerNetworking().register();

        EntrypointInvoker.CONTRACT.invoke(CONTRACTS::register);

        tidyUpConfig();
        CONFIG.save();
    }

    public static void tidyUpConfig() {
        CONTRACTS.tidyUp();
    }
}
