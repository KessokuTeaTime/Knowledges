package net.krlite.knowledges;

import net.krlite.knowledges.api.Knowledge;
import net.krlite.knowledges.api.KnowledgeContainer;
import net.krlite.knowledges.components.ArmorDurabilityComponent;
import net.krlite.knowledges.components.CrosshairComponent;
import net.krlite.knowledges.components.info.BlockInfoComponent;
import net.krlite.knowledges.components.info.EntityInfoComponent;
import net.krlite.knowledges.components.info.FluidInfoComponent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DefaultComponents implements KnowledgeContainer {
	@Override
	public @NotNull List<Knowledge> register() {
		return List.of(
				new CrosshairComponent(),

				new BlockInfoComponent(),
				new EntityInfoComponent(),
				new FluidInfoComponent(),

				new ArmorDurabilityComponent()
		);
	}
}
