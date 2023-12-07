package net.krlite.knowledges.config;

import net.fabricmc.loader.api.FabricLoader;
import net.krlite.knowledges.Knowledges;
import net.krlite.knowledges.api.Knowledge;
import net.minecraft.util.Identifier;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class DisabledComponents {
	public DisabledComponents() {
		load();
	}

	private static final File storage = FabricLoader.getInstance().getConfigDir().resolve("knowledges_disabled_components.txt").toFile();
	private static final ArrayList<String> banned = new ArrayList<>();

	private void load() {
		if (storage.exists()) {
			try {
				banned.clear();
				banned.addAll(FileUtils.readLines(storage, Charset.defaultCharset()));
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
		else {
			storage.getParentFile().mkdirs();
			save();
		}
	}

	private void save() {
		try {
			FileUtils.writeLines(storage, banned);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public boolean get(Knowledge knowledge) {
		return Knowledges.MANAGER.identifier(knowledge)
				.map(Identifier::toString)
				.filter(banned::contains)
				.isPresent();
	}

	public void set(Knowledge knowledge, boolean flag) {
		Knowledges.MANAGER.identifier(knowledge)
				.map(Identifier::toString)
				.ifPresent(id -> {
					if (flag) {
						if (!banned.contains(id)) {
							banned.add(id);
							save();
						}
					} else {
						if (banned.contains(id)) {
							banned.remove(id);
							save();
						}
					}
				});
	}
}
