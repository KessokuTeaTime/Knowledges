package net.krlite.knowledges.manager;

import net.krlite.knowledges.KnowledgesClient;
import net.krlite.knowledges.api.component.Knowledge;
import net.krlite.knowledges.api.proxy.RenderProxy;
import net.krlite.knowledges.api.representable.base.Representable;
import net.krlite.knowledges.manager.base.EntrypointInvoker;
import net.krlite.knowledges.manager.base.Manager;
import org.jetbrains.annotations.NotNull;

public class ComponentManager extends Manager<Knowledge> {
    public ComponentManager() {
        super(() -> KnowledgesClient.CONFIG.get().components.available);
    }

    @Override
    protected @NotNull EntrypointInvoker entrypoint() {
        return EntrypointInvoker.COMPONENT;
    }

    public void render(RenderProxy renderProxy, Representable<?> representable) {
        asList().stream()
                .filter(this::isEnabled)
                .forEach(knowledge -> knowledge.render(renderProxy, representable));
    }
}
