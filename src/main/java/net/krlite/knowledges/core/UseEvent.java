package net.krlite.knowledges.core;

import net.fabricmc.fabric.api.event.Event;

public interface UseEvent {
    <E extends DataEvent<?>> Event<E> event();

    interface Target<T extends Enum<T> & UseEvent> {
        T target();
    }
}
