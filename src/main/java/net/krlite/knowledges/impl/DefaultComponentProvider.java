package net.krlite.knowledges.impl;

import net.krlite.knowledges.api.Knowledge;
import net.krlite.knowledges.api.entrypoint.ComponentProvider;
import net.krlite.knowledges.component.ArmorDurabilityComponent;
import net.krlite.knowledges.component.CrosshairComponent;
import net.krlite.knowledges.component.info.BlockInfoComponent;
import net.krlite.knowledges.component.info.EntityInfoComponent;
import net.krlite.knowledges.component.info.FluidInfoComponent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DefaultComponentProvider implements ComponentProvider {
	@Override
	public @NotNull List<Class<? extends Knowledge>> provide() {
		return List.of(
				CrosshairComponent.class,

				BlockInfoComponent.class,
				EntityInfoComponent.class,
				FluidInfoComponent.class,

				ArmorDurabilityComponent.class
		);
	}
}
