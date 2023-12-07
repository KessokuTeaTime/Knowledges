package net.krlite.knowledges;

import net.krlite.knowledges.api.Knowledge;
import net.krlite.knowledges.config.DisabledComponents;
import net.minecraft.util.Identifier;

import java.util.*;

public class ComponentsManager {
    private final DisabledComponents disabled = new DisabledComponents();
    private final HashMap<String, List<Knowledge>> components = new HashMap<>();

    public Map<String, List<Knowledge>> asMap() {
        return Map.copyOf(components);
    }

    public List<Knowledge> asList() {
        return asMap().values().stream()
                .flatMap(List::stream)
                .toList();
    }

    public Optional<Knowledge> byId(String namespace, String... paths) {
        return Optional.ofNullable(asMap().get(namespace))
                .flatMap(list -> list.stream()
                        .filter(knowledge -> knowledge.path().equals(String.join(".", paths)))
                        .findAny());
    }

    public Optional<String> namespace(Knowledge component) {
        return asMap().entrySet().stream()
                .filter(entry -> entry.getValue().contains(component))
                .findAny()
                .map(Map.Entry::getKey);
    }

    public Optional<Identifier> identifier(Knowledge component) {
        return namespace(component)
                .map(namespace -> new Identifier(namespace, component.path()));
    }

    public boolean isInNamespace(Knowledge component, String namespace) {
        return namespace(component).equals(Optional.of(namespace));
    }

    public boolean isInDefaultNamespace(Knowledge component) {
        return isInNamespace(component, Knowledges.ID);
    }

    public boolean isEnabled(Knowledge component) {
        return !disabled.get(component);
    }

    public void setEnabled(Knowledge component, boolean enabled) {
        disabled.set(component, !enabled);
    }

    void register(String namespace, Knowledge component) {
        Helper.List.fastMerge(components, namespace, component);
    }
}
