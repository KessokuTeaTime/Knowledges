package net.krlite.knowledges.config;

import net.krlite.knowledges.Knowledges;
import net.krlite.knowledges.api.Knowledge;
import net.minecraft.util.Identifier;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class DisabledComponentsConfig extends SimpleDisabledConfig {
	public DisabledComponentsConfig() {
		super("disabled_components");
	}

	public boolean get(Knowledge knowledge) {
		return Knowledges.MANAGER.identifier(knowledge)
				.map(Identifier::toString)
				.filter(super::get)
				.isPresent();
	}

	public void set(Knowledge knowledge, boolean flag) {
		Knowledges.MANAGER.identifier(knowledge)
				.map(Identifier::toString)
				.ifPresent(key -> super.set(key, flag));
	}
}
