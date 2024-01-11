package net.krlite.knowledges.manager;

import net.krlite.knowledges.api.Knowledge;
import net.krlite.knowledges.config.disabled.DisabledComponentsConfig;

public class KnowledgesComponentManager extends AbstractManager<Knowledge> {
    public KnowledgesComponentManager() {
        super(new DisabledComponentsConfig());
    }

    @Override
    protected String localizationPrefix() {
        return "knowledge";
    }
}
