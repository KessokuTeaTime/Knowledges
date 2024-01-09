package net.krlite.knowledges.core;

public interface DataEvent<T extends Enum<T> & Target> extends Target.Consumer<T> {
}
