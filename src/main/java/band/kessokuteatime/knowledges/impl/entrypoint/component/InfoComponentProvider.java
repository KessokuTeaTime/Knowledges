package band.kessokuteatime.knowledges.impl.entrypoint.component;

import band.kessokuteatime.knowledges.api.entrypoint.ComponentProvider;
import band.kessokuteatime.knowledges.impl.component.base.InfoComponent;
import band.kessokuteatime.knowledges.impl.component.info.BlockInfoComponent;
import band.kessokuteatime.knowledges.impl.component.info.EntityInfoComponent;
import band.kessokuteatime.knowledges.impl.component.info.FluidInfoComponent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class InfoComponentProvider implements ComponentProvider<InfoComponent> {
    @Override
    public @NotNull List<Class<? extends InfoComponent>> provide() {
        return List.of(
                band.kessokuteatime.knowledges.impl.component.info.InfoComponent.class,

                BlockInfoComponent.class,
                EntityInfoComponent.class,
                FluidInfoComponent.class
        );
    }
}
