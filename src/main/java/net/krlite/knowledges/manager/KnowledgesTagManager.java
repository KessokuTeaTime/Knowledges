package net.krlite.knowledges.manager;

import net.krlite.knowledges.KnowledgesClient;
import net.krlite.knowledges.api.tag.AdditionalTag;

public class KnowledgesTagManager extends KnowledgesManager<AdditionalTag<?>> {
    public KnowledgesTagManager() {
        super(() -> KnowledgesClient.CONFIG.tags.map);
    }

    @Override
    protected String localizationPrefix() {
        return "tag";
    }
}
