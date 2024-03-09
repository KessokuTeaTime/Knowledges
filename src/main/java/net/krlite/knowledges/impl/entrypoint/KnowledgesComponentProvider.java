package net.krlite.knowledges.impl.entrypoint;

import net.krlite.knowledges.api.component.Knowledge;
import net.krlite.knowledges.api.entrypoint.ComponentProvider;
import net.krlite.knowledges.impl.component.ArmorDurabilityComponent;
import net.krlite.knowledges.impl.component.CrosshairComponent;
import net.krlite.knowledges.impl.component.info.BlockInfoComponent;
import net.krlite.knowledges.impl.component.info.EntityInfoComponent;
import net.krlite.knowledges.impl.component.info.FluidInfoComponent;
import net.krlite.knowledges.impl.component.info.InfoComponent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class KnowledgesComponentProvider implements ComponentProvider {
	@Override
	public @NotNull List<Class<? extends Knowledge>> provide() {
		return List.of(
				CrosshairComponent.class,
				InfoComponent.class,

				BlockInfoComponent.class,
				EntityInfoComponent.class,
				FluidInfoComponent.class,

				ArmorDurabilityComponent.class
		);
	}
}
