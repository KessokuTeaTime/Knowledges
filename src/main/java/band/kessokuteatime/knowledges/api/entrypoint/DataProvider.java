package band.kessokuteatime.knowledges.api.entrypoint;

import band.kessokuteatime.knowledges.api.data.Data;
import band.kessokuteatime.knowledges.api.entrypoint.base.Provider;

public interface DataProvider<T extends Data<?>> extends Provider<T> {
    interface Global extends DataProvider<Data<?>> {
    }
}
