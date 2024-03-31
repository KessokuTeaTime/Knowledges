package net.krlite.knowledges.api.component;

import net.krlite.knowledges.KnowledgesClient;
import net.krlite.knowledges.api.core.config.WithIndependentConfigPage;
import net.krlite.knowledges.api.core.localization.Localizable;
import net.krlite.knowledges.api.core.path.WithPath;
import net.krlite.knowledges.api.data.Data;
import net.krlite.knowledges.api.data.transfer.DataInvoker;
import net.krlite.knowledges.api.proxy.LayoutProxy;
import net.krlite.knowledges.api.proxy.LocalizationProxy;
import net.krlite.knowledges.api.proxy.ModProxy;
import net.krlite.knowledges.api.proxy.RenderProxy;
import net.krlite.knowledges.api.representable.base.Representable;
import net.minecraft.client.gui.DrawContext;
import org.jetbrains.annotations.NotNull;

/**
 * A component which accepts an {@link Representable} and renders through a {@link RenderProxy}.
 *
 * <h3>Extendable Data</h3>
 *
 * Use {@link Data} for extendable rendering information, such as fetching textual descriptions from certain context.
 * To begin with, implement a {@link DataInvoker} class and refer to its documents.
 *
 * @see DataInvoker
 * @see RenderProxy
 * @see Representable
 * @see LayoutProxy
 * @see LocalizationProxy
 * @see ModProxy
 */
public interface Knowledge extends WithPath, Localizable.WithName, WithIndependentConfigPage {
	/**
	 * Renders the component.
	 * @param renderProxy	the {@link RenderProxy} handing the {@link DrawContext} instance.
	 * @param representable	the {@link Representable} handing the remote data.
	 */
	void render(RenderProxy renderProxy, @NotNull Representable<?> representable);

	@Override
	default String localizationKey(String... paths) {
		paths[0] = path() + Separator.REALM + paths[0];
		return KnowledgesClient.COMPONENTS.localizationKey(this, paths);
	}
}
