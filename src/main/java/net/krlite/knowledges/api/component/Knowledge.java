package net.krlite.knowledges.api.component;

import net.krlite.knowledges.KnowledgesClient;
import net.krlite.knowledges.api.core.config.WithIndependentConfigPage;
import net.krlite.knowledges.api.core.localization.Localizable;
import net.krlite.knowledges.api.core.path.WithPath;
import net.krlite.knowledges.api.proxy.RenderProxy;
import net.krlite.knowledges.api.representable.base.Representable;
import org.jetbrains.annotations.NotNull;

public interface Knowledge extends WithPath, Localizable.WithName, WithIndependentConfigPage {
	void render(RenderProxy renderProxy, @NotNull Representable<?> representable);

	@Override
	default String localizationKey(String... paths) {
		paths[0] = path() + Separator.REALM + paths[0];
		return KnowledgesClient.COMPONENTS.localizationKey(this, paths);
	}
}
