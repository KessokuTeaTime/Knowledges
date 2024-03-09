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
        List<String> fullPaths = new ArrayList<>(List.of(path()));
        fullPaths.addAll(List.of(paths));

        return KnowledgesClient.DATA.localizationKey(this, fullPaths.toArray(String[]::new));
    }
}
