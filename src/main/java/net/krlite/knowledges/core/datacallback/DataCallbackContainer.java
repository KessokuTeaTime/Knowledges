package net.krlite.knowledges.core.datacallback;

public interface DataCallbackContainer<C extends DataCallback<C>> {
    C callback();
}
