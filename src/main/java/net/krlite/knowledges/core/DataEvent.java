package net.krlite.knowledges.core;

public interface DataEvent<T extends Enum<T> & UseEvent> extends UseEvent.Target<T> {
}
