package net.krlite.knowledges.config.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.krlite.knowledges.KnowledgesClient;

public class KnowledgesModMenuIntegration implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return parent -> {
			KnowledgesClient.CONFIG.load();
			return new KnowledgesConfigScreen(parent).build();
		};
	}
}
