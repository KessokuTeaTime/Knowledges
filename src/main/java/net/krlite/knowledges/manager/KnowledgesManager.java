package net.krlite.knowledges.manager;

import com.google.common.collect.ImmutableList;
import net.krlite.knowledges.KnowledgesClient;
import net.krlite.knowledges.KnowledgesCommon;
import net.krlite.knowledges.api.core.path.WithPath;
import net.krlite.knowledges.Shortcuts;
import net.minecraft.util.Identifier;

import java.util.*;
import java.util.function.Supplier;

public abstract class KnowledgesManager<T extends WithPath> {
    private final HashMap<String, List<T>> map = new HashMap<>();
    private final Supplier<Map<String, Boolean>> configSupplier;

    KnowledgesManager(Supplier<Map<String, Boolean>> configSupplier) {
        this.configSupplier = configSupplier;
    }

    protected abstract String localizationPrefix();

    public static <V> void fixKeysAndSort(Map<String, V> config) {
        // This fixes a Toml4j issue
        var sortedMap = new TreeMap<String, V>();

        for (String key : ImmutableList.copyOf(config.keySet())) {
            String rawKey = key.replaceAll("^\"*|\"*$", "");
            V value = config.remove(key);

            sortedMap.putIfAbsent(rawKey, value);
        }

        config.putAll(sortedMap);
    }

    public void register(String namespace, T t) {
        Shortcuts.Map.fastMerge(map, namespace, t);
        identifier(t).ifPresent(key -> configSupplier.get().putIfAbsent(key.toString(), true));
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
        String namespace = namespace(t).orElse(KnowledgesCommon.ID);
        return localizationPrefix() + "." + namespace + "." + String.join(".", paths);
    }

    public boolean isInNamespace(T t, String namespace) {
        return namespace(t).equals(Optional.of(namespace));
    }

    public boolean isInDefaultNamespace(T t) {
        return isInNamespace(t, KnowledgesCommon.ID);
    }

    public boolean isEnabled(T t) {
        return identifier(t)
            .map(Identifier::toString)
            .filter(key -> {
                var config = configSupplier.get();

                if (config.containsKey(key)) {
                    return config.get(key);
                } else {
                    config.put(key, true);
                    return true;
                }
            })
            .isPresent();
    }

    public void setEnabled(T t, boolean enabled) {
        identifier(t)
                .map(Identifier::toString)
                .ifPresent(key -> {
                    var config = configSupplier.get();

                    if (enabled && !isEnabled(t)) {
                        config.put(key, true);
                    }

                    if (!enabled && isEnabled(t)) {
                        config.put(key, false);
                    }
                });

        KnowledgesClient.CONFIG.save();
    }

    public void tidyUp() {
        configSupplier.get().keySet()
                .removeIf(key -> {
                    Identifier i = Identifier.tryParse(key);
                    return i == null || byId(i).isEmpty();
                });
    }
}
