package net.krlite.knowledges.core.datacallback;

import net.fabricmc.fabric.api.event.Event;

public interface DataCallback<C extends DataCallback<C>> {
    Event<C> event();

    String name();
}
