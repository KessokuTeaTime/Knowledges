package net.krlite.knowledges.manager;

import net.krlite.knowledges.KnowledgesClient;
import net.krlite.knowledges.api.component.Knowledge;
import net.krlite.knowledges.api.proxy.RenderProxy;
import net.krlite.knowledges.api.representable.Representable;
import net.minecraft.client.gui.DrawContext;

public class KnowledgesComponentManager extends KnowledgesManager<Knowledge> {
    public KnowledgesComponentManager() {
        super(() -> KnowledgesClient.CONFIG.components.map);
    }

    public void render(RenderProxy renderProxy, Representable<?> representable) {
        asList().stream()
                .filter(this::isEnabled)
                .forEach(knowledge -> knowledge.render(renderProxy, representable));
    }

    @Override
    protected String localizationPrefix() {
        return "knowledge";
    }
}
