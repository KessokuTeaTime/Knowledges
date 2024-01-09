package net.krlite.knowledges;

import net.krlite.knowledges.api.Knowledge;
import net.krlite.knowledges.config.disabled.DisabledComponentsConfig;

public class ComponentManager extends Knowledges.Manager<Knowledge<?>> {
    public ComponentManager() {
        super(new DisabledComponentsConfig());
    }

    @Override
    protected String localizationPrefix() {
        return "knowledge";
    }
}
