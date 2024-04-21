package band.kessokuteatime.knowledges.impl.entrypoint.component;

import band.kessokuteatime.knowledges.api.component.Knowledge;
import band.kessokuteatime.knowledges.impl.component.ArmorDurabilityComponent;
import band.kessokuteatime.knowledges.impl.component.CrosshairComponent;
import band.kessokuteatime.knowledges.impl.component.info.BlockInfoComponent;
import band.kessokuteatime.knowledges.impl.component.info.EntityInfoComponent;
import band.kessokuteatime.knowledges.impl.component.info.FluidInfoComponent;
import band.kessokuteatime.knowledges.impl.component.info.InfoComponent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ComponentProvider implements band.kessokuteatime.knowledges.api.entrypoint.ComponentProvider.Global {
	@Override
	public @NotNull List<Class<? extends Knowledge>> provide() {
		return List.of(
				CrosshairComponent.class,
				ArmorDurabilityComponent.class
		);
	}
}
