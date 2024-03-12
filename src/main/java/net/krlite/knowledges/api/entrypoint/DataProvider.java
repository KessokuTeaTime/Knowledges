package net.krlite.knowledges.api.entrypoint;

import net.krlite.knowledges.api.data.Data;
import net.krlite.knowledges.api.entrypoint.base.Provider;

public interface DataProvider<T extends Data<?>> extends Provider<T> {
    interface Global extends DataProvider<Data<?>> {
    }
}
