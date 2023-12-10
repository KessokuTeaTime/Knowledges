package net.krlite.knowledges;

import net.krlite.knowledges.api.Knowledge;
import net.krlite.knowledges.config.disabled.DisabledComponentsConfig;

public class ComponentsManager extends Knowledges.Manager<Knowledge> {
    public ComponentsManager() {
        super(new DisabledComponentsConfig());
    }

    @Override
    protected String localizationPrefix() {
        return "knowledge";
    }
}
