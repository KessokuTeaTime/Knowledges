package band.kessokuteatime.knowledges.api.component;

import band.kessokuteatime.knowledges.KnowledgesClient;
import band.kessokuteatime.knowledges.api.core.config.WithIndependentConfigPage;
import band.kessokuteatime.knowledges.api.core.localization.Localizable;
import band.kessokuteatime.knowledges.api.core.path.WithPath;
import band.kessokuteatime.knowledges.api.data.Data;
import band.kessokuteatime.knowledges.api.data.transfer.DataInvoker;
import band.kessokuteatime.knowledges.api.proxy.LayoutProxy;
import band.kessokuteatime.knowledges.api.proxy.LocalizationProxy;
import band.kessokuteatime.knowledges.api.proxy.ModProxy;
import band.kessokuteatime.knowledges.api.proxy.RenderProxy;
import band.kessokuteatime.knowledges.api.representable.base.Representable;
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
