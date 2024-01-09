package net.krlite.knowledges.core;

public interface DataCallbackContainer<C extends DataCallback<C>> {
    C callback();
}
