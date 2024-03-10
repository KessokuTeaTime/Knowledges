package net.krlite.knowledges;

import net.fabricmc.api.ModInitializer;
import net.krlite.knowledges.networking.ServerNetworking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KnowledgesCommon implements ModInitializer {
    public static final String NAME = "Knowledges", ID = "knowledges";
    public static final Logger LOGGER = LoggerFactory.getLogger(ID + ":common");

    @Override
    public void onInitialize() {
        new ServerNetworking().register();
    }
}
