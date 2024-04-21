package band.kessokuteatime.knowledges.api.data;

import band.kessokuteatime.knowledges.KnowledgesClient;
import band.kessokuteatime.knowledges.api.component.Knowledge;
import band.kessokuteatime.knowledges.api.core.config.WithIndependentConfigPage;
import band.kessokuteatime.knowledges.api.core.localization.Localizable;
import band.kessokuteatime.knowledges.api.core.path.WithPath;
import band.kessokuteatime.knowledges.api.data.transfer.DataProtocol;

/**
 * A data which provides extendable information for a {@link Knowledge}.
 * @param <K>
 */
public interface Data<K extends Knowledge> extends DataProtocol<K>, WithPath, Localizable.WithName, WithIndependentConfigPage {
    @Override
    default String localizationKey(String... paths) {
        paths[0] = path() + Separator.REALM + paths[0];
        return KnowledgesClient.DATA.localizationKey(this, paths);
    }
}
