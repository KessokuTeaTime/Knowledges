package band.kessokuteatime.knowledges.config.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import band.kessokuteatime.knowledges.KnowledgesClient;
import band.kessokuteatime.knowledges.KnowledgesCommon;

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
