package net.krlite.knowledges.config;

import net.krlite.knowledges.Knowledges;
import net.krlite.knowledges.api.Knowledge;
import net.minecraft.util.Identifier;

public class DisabledComponentsConfig extends SimpleDisabledConfig {
	public DisabledComponentsConfig() {
		super("disabled_components");
	}

	public boolean get(Knowledge knowledge) {
		return Knowledges.COMPONENTS.identifier(knowledge)
				.map(Identifier::toString)
				.filter(super::get)
				.isPresent();
	}

	public void set(Knowledge knowledge, boolean flag) {
		Knowledges.COMPONENTS.identifier(knowledge)
				.map(Identifier::toString)
				.ifPresent(key -> super.set(key, flag));
	}
}
