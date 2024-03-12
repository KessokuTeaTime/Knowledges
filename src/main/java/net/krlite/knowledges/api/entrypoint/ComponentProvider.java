package net.krlite.knowledges.api.entrypoint;

import net.krlite.knowledges.api.component.Knowledge;
import net.krlite.knowledges.api.entrypoint.base.Provider;

public interface ComponentProvider<T extends Knowledge> extends Provider<T> {
    interface General extends ComponentProvider<Knowledge> {

    }
}
