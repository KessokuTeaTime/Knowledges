package net.krlite.knowledges.core;

public interface Target {
    interface Provider<T extends Enum<T> & Target> {
        Class<T> targets();
    }

    interface Consumer<T extends Enum<T> & Target> {
        T target();
    }
}
