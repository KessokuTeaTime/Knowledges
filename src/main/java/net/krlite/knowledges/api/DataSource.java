package net.krlite.knowledges.api;

public interface DataSource<K extends Knowledge> {
    K target();

    String name();
}
