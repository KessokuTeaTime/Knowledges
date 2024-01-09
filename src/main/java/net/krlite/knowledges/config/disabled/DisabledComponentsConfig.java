package net.krlite.knowledges.config.disabled;

import net.krlite.knowledges.Knowledges;
import net.krlite.knowledges.api.Knowledge;
import net.minecraft.util.Identifier;

public class DisabledComponentsConfig extends AbstractDisabledConfig<Knowledge<?>> {
	public DisabledComponentsConfig() {
		super("disabled_components");
	}

	public boolean get(Knowledge<?> knowledge) {
		return Knowledges.COMPONENTS.identifier(knowledge)
				.map(Identifier::toString)
				.filter(super::get)
				.isPresent();
	}

	public void set(Knowledge<?> knowledge, boolean flag) {
		Knowledges.COMPONENTS.identifier(knowledge)
				.map(Identifier::toString)
				.ifPresent(key -> super.set(key, flag));
	}
}
