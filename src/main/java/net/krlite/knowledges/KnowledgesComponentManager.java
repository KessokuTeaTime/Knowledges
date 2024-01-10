package net.krlite.knowledges;

import net.krlite.knowledges.api.Knowledge;
import net.krlite.knowledges.config.disabled.DisabledComponentsConfig;

public class KnowledgesComponentManager extends Knowledges.Manager<Knowledge> {
    public KnowledgesComponentManager() {
        super(new DisabledComponentsConfig());
    }

    @Override
    protected String localizationPrefix() {
        return "knowledge";
    }
}
