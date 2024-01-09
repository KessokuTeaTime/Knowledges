package net.krlite.knowledges.core;

import net.fabricmc.fabric.api.event.Event;

public interface Target {
    <E extends DataEvent<?, ?>> Event<E> event();

    interface Provider<T extends Enum<T> & Target> {
        Class<T> targets();
    }

    interface Consumer<T extends Enum<T> & Target> {
        T target();
    }
}
