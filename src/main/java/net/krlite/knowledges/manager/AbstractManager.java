package net.krlite.knowledges.manager;

import net.krlite.knowledges.Knowledges;
import net.krlite.knowledges.config.disabled.AbstractDisabledConfig;
import net.krlite.knowledges.core.path.WithPath;
import net.krlite.knowledges.core.util.Helper;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class AbstractManager<T extends WithPath> {
    private final HashMap<String, List<T>> map = new HashMap<>();
    private final AbstractDisabledConfig<T> disabled;

    AbstractManager(AbstractDisabledConfig<T> disabled) {
        this.disabled = disabled;
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
        String namespace = namespace(t).orElse(Knowledges.ID);
        return localizationPrefix() + "." + namespace + "." + String.join(".", paths);
    }

    public boolean isInNamespace(T t, String namespace) {
        return namespace(t).equals(Optional.of(namespace));
    }

    public boolean isInDefaultNamespace(T t) {
        return isInNamespace(t, Knowledges.ID);
    }

    public boolean isEnabled(T t) {
        return !disabled.get(t);
    }

    public void setEnabled(T t, boolean enabled) {
        disabled.set(t, !enabled);
    }
}
