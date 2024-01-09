package net.krlite.knowledges.core;

public interface DataCallback<T extends Enum<T> & HasEvent> extends HasEvent.HasSource<T> {
}
