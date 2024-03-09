package net.krlite.knowledges.manager;

import net.krlite.knowledges.KnowledgesClient;
import net.krlite.knowledges.api.Knowledge;

public class KnowledgesComponentManager extends AbstractManager<Knowledge> {
    public KnowledgesComponentManager() {
        super(() -> KnowledgesClient.CONFIG.components.disabled);
    }

    @Override
    protected String localizationPrefix() {
        return "knowledge";
    }
}
