package band.kessokuteatime.knowledges.api.entrypoint;

import band.kessokuteatime.knowledges.api.component.Knowledge;
import band.kessokuteatime.knowledges.api.entrypoint.base.Provider;

public interface ComponentProvider<T extends Knowledge> extends Provider<T> {
    interface Global extends ComponentProvider<Knowledge> {
    }
}
