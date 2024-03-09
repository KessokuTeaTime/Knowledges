package net.krlite.knowledges.manager;

import net.krlite.knowledges.KnowledgesClient;
import net.krlite.knowledges.core.path.WithPath;
import net.krlite.knowledges.core.util.Helper;
import net.minecraft.util.Identifier;

import java.util.*;
import java.util.function.Supplier;

public abstract class AbstractManager<T extends WithPath> {
    private final HashMap<String, List<T>> map = new HashMap<>();
    private final Supplier<ArrayList<String>> supplier;

    AbstractManager(Supplier<ArrayList<String>> supplier) {
        this.supplier = supplier;
    }

    protected abstract String localizationPrefix();

    public void register(String namespace, T t) {
        Helper.Map.fastMerge(map, namespace, t);
    }

    public Map<String, List<T>> asMap() {
        return Map.copyOf(map);
    }

    public List<T> asList() {
        return asMap().values().stream()
                .flatMap(List::stream)
                .toList();
    }

    public Optional<T> byClass(Class<? extends T> tClass) {
        return asList().stream()
                .filter(tClass::isInstance)
                .findAny();
    }

    public Optional<T> byId(String namespace, String... paths) {
        return Optional.ofNullable(asMap().get(namespace))
                .flatMap(list -> list.stream()
                        .filter(t -> t.path().equals(String.join(".", paths)))
                        .findAny());
    }

    public Optional<T> byId(Identifier identifier) {
        return byId(identifier.getNamespace(), identifier.getPath());
    }

    public Optional<String> namespace(T t) {
        return asMap().entrySet().stream()
                .filter(entry -> entry.getValue().contains(t))
                .findAny()
                .map(Map.Entry::getKey);
    }

    public Optional<Identifier> identifier(T t) {
        return namespace(t)
                .map(namespace -> new Identifier(namespace, t.path()));
    }

    public String localizationKey(T t, String... paths) {
        String namespace = namespace(t).orElse(KnowledgesClient.ID);
        return localizationPrefix() + "." + namespace + "." + String.join(".", paths);
    }

    public boolean isInNamespace(T t, String namespace) {
        return namespace(t).equals(Optional.of(namespace));
    }

    public boolean isInDefaultNamespace(T t) {
        return isInNamespace(t, KnowledgesClient.ID);
    }

    public boolean isEnabled(T t) {
        return identifier(t)
            .map(Identifier::toString)
            .filter(supplier.get()::contains)
            .isEmpty();
    }

    public void setEnabled(T t, boolean enabled) {
        identifier(t)
                .map(Identifier::toString)
                .ifPresent(key -> {
                    if (enabled && !isEnabled(t)) {
                        supplier.get().remove(key);
                    }

                    if (!enabled && isEnabled(t)) {
                        supplier.get().add(key);
                    }
                });

        KnowledgesClient.CONFIG_HOLDER.save();
    }
}
