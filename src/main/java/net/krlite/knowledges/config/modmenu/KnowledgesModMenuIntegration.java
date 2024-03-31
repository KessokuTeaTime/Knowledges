package net.krlite.knowledges.config.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.krlite.knowledges.KnowledgesClient;
import net.krlite.knowledges.KnowledgesCommon;

public class KnowledgesModMenuIntegration implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return parent -> {
			KnowledgesCommon.CONFIG.load();
			KnowledgesClient.CONFIG.load();

			return new KnowledgesConfigScreen(parent).build();
		};
	}
}
