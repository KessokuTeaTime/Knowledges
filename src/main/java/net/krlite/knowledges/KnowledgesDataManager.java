package net.krlite.knowledges;

import net.krlite.knowledges.api.Data;
import net.krlite.knowledges.config.disabled.DisabledDataConfig;

public class KnowledgesDataManager extends Knowledges.Manager<Data<?>> {
    KnowledgesDataManager() {
        super(new DisabledDataConfig());
    }

    @Override
    protected String localizationPrefix() {
        return "knowledge_data";
    }
}