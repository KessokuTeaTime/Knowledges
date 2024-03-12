package net.krlite.knowledges.api.entrypoint;

import net.krlite.knowledges.api.data.Data;

public interface DataProvider<T extends Data<?>> extends Provider<T> {
    interface General extends DataProvider<Data<?>> {

    }
}
