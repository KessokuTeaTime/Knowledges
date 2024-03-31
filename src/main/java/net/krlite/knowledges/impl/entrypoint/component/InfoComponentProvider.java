package net.krlite.knowledges.impl.entrypoint.component;

import net.krlite.knowledges.api.entrypoint.ComponentProvider;
import net.krlite.knowledges.impl.component.base.InfoComponent;
import net.krlite.knowledges.impl.component.info.BlockInfoComponent;
import net.krlite.knowledges.impl.component.info.EntityInfoComponent;
import net.krlite.knowledges.impl.component.info.FluidInfoComponent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class InfoComponentProvider implements ComponentProvider<InfoComponent> {
    @Override
    public @NotNull List<Class<? extends InfoComponent>> provide() {
        return List.of(
                net.krlite.knowledges.impl.component.info.InfoComponent.class,

                BlockInfoComponent.class,
                EntityInfoComponent.class,
                FluidInfoComponent.class
        );
    }
}
