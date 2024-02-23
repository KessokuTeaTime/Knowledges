package net.krlite.knowledges.manager;

import net.krlite.knowledges.Knowledges;
import net.krlite.knowledges.api.Knowledge;
import net.krlite.knowledges.config.disabled.DisabledComponentsConfig;

public class KnowledgesComponentManager extends AbstractManager<Knowledge> {
    public KnowledgesComponentManager() {
        super(() -> Knowledges.CONFIG.components.disabled);
    }

    @Override
    protected String localizationPrefix() {
        return "knowledge";
    }
}
