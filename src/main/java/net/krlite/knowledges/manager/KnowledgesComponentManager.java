package net.krlite.knowledges.manager;

import net.krlite.knowledges.KnowledgesClient;
import net.krlite.knowledges.api.component.Knowledge;
import net.krlite.knowledges.api.representable.Representable;

public class KnowledgesComponentManager extends KnowledgesManager<Knowledge> {
    public KnowledgesComponentManager() {
        super(() -> KnowledgesClient.CONFIG.components.disabled);
    }

    public void render(Representable<?> representable) {
        asList().stream()
                .filter(this::isEnabled)
                .forEach(knowledge -> knowledge.render(representable));
    }

    @Override
    protected String localizationPrefix() {
        return "knowledge";
    }
}
