package net.krlite.knowledges.api.data;

import net.krlite.knowledges.KnowledgesClient;
import net.krlite.knowledges.api.component.Knowledge;
import net.krlite.knowledges.api.core.config.WithIndependentConfigPage;
import net.krlite.knowledges.api.core.localization.Localizable;
import net.krlite.knowledges.api.core.path.WithPath;

import java.util.ArrayList;
import java.util.List;

public interface Data<K extends Knowledge> extends DataProtocol<K>, WithPath, Localizable.WithName, WithIndependentConfigPage {
    @Override
    default String localizationKey(String... paths) {
        paths[0] = path() + Separator.REALM + paths[0];
        return KnowledgesClient.DATA.localizationKey(this, paths);
    }
}
