package net.krlite.knowledges.api.entrypoint;

import net.krlite.knowledges.api.component.Knowledge;

public interface ComponentProvider<T extends Knowledge> extends Provider<T> {
    interface General extends ComponentProvider<Knowledge> {

    }
}
