package band.kessokuteatime.knowledges.manager;

import band.kessokuteatime.knowledges.KnowledgesClient;
import band.kessokuteatime.knowledges.api.component.Knowledge;
import band.kessokuteatime.knowledges.api.proxy.RenderProxy;
import band.kessokuteatime.knowledges.api.representable.base.Representable;
import band.kessokuteatime.knowledges.manager.base.EntrypointInvoker;
import band.kessokuteatime.knowledges.manager.base.Manager;
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
