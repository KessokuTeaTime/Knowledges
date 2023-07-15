package net.krlite.knowledges.config;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class KnowledgesBanList {
	public KnowledgesBanList() {
		load();
	}

	private static final File storage = FabricLoader.getInstance().getConfigDir().resolve("knowledges_ban_list.txt").toFile();
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

	public void setBanned(String name, boolean flag) {
		if (flag) {
			if (!banned.contains(name)) {
				banned.add(name);
				save();
			}
		} else {
			if (banned.contains(name)) {
				banned.remove(name);
				save();
			}
		}
	}

	public void setBanned(Text name, boolean flag) {
		setBanned(name.getString(), flag);
	}

	public boolean isBanned(String name) {
		return banned.stream().anyMatch(name::equals);
	}

	public boolean isBanned(Text name) {
		return isBanned(name.getString());
	}
}
