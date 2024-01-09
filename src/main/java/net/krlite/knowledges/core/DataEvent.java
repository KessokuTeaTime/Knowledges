package net.krlite.knowledges.core;

public interface DataEvent<T extends Enum<T> & Target, R> extends Target.Consumer<T> {
    R fallback();
}
