package net.krlite.knowledges.core;

import net.fabricmc.fabric.api.event.Event;

public interface HasEvent {
    <E extends DataCallback<?>> Event<E> event();

    interface HasSource<T extends Enum<T> & HasEvent> {
        T source();
    }
}
