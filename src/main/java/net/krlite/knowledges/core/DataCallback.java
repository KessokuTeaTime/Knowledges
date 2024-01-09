package net.krlite.knowledges.core;

import net.fabricmc.fabric.api.event.Event;

public interface DataCallback<C> {
    Event<C> event();

    String name();
}
