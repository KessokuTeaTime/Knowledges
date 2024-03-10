package net.krlite.knowledges.config.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.krlite.knowledges.KnowledgesClient;
import net.minecraft.client.MinecraftClient;

public class KnowledgesModMenuImpl implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return parent -> new KnowledgesConfigScreen(parent).build();
	}
}
