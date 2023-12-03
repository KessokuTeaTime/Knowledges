package net.krlite.knowledges;

import net.krlite.knowledges.api.Knowledge;
import net.krlite.knowledges.api.KnowledgeContainer;
import net.krlite.knowledges.components.ArmorDurabilityComponent;
import net.krlite.knowledges.components.CrosshairComponent;
import net.krlite.knowledges.components.info.BlockInfoComponent;
import net.krlite.knowledges.components.info.EntityInfoComponent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class DefaultComponents implements KnowledgeContainer {
	@Override
	public @NotNull ArrayList<Knowledge> register() {
		final ArrayList<Knowledge> knowledges = new ArrayList<>();

		knowledges.add(new CrosshairComponent());

		knowledges.add(new BlockInfoComponent());
		knowledges.add(new EntityInfoComponent());

		knowledges.add(new ArmorDurabilityComponent());

		return knowledges;
	}
}
