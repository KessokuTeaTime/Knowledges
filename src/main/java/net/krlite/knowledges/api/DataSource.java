package net.krlite.knowledges.api;

import net.krlite.knowledges.Knowledges;

import java.util.List;
import java.util.function.Function;

public interface DataSource<K extends Knowledge, S extends DataSource<K, S>> {
    Class<K> target();

    default List<S> listeners() {
        return Knowledges.DATA.availableListenersFromSource(this);
    }

    interface Functional<S extends DataSource<?, S>> {
        Function<List<S>, S> function();
    }
}
