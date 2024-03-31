package net.krlite.knowledges.impl.entrypoint.component;

import net.krlite.knowledges.api.component.Knowledge;
import net.krlite.knowledges.impl.component.ArmorDurabilityComponent;
import net.krlite.knowledges.impl.component.CrosshairComponent;
import net.krlite.knowledges.impl.component.info.BlockInfoComponent;
import net.krlite.knowledges.impl.component.info.EntityInfoComponent;
import net.krlite.knowledges.impl.component.info.FluidInfoComponent;
import net.krlite.knowledges.impl.component.info.InfoComponent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ComponentProvider implements net.krlite.knowledges.api.entrypoint.ComponentProvider.Global {
	@Override
	public @NotNull List<Class<? extends Knowledge>> provide() {
		return List.of(
				CrosshairComponent.class,
				ArmorDurabilityComponent.class
		);
	}
}
