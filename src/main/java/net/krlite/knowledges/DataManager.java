package net.krlite.knowledges;

import net.krlite.knowledges.api.Data;
import net.krlite.knowledges.base.Helper;
import net.krlite.knowledges.config.DisabledDataConfig;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class DataManager {
    private final DisabledDataConfig disabled = new DisabledDataConfig();
    private final HashMap<String, HashMap<Identifier, List<Data<?, ?>>>> map = new HashMap<>();

    public Map<String, HashMap<Identifier, List<Data<?, ?>>>> asMap() {
        return Map.copyOf(map);
    }

    public HashMap<Identifier, List<Data<?, ?>>> asClassifiedMap() {
        return asMap().values().stream()
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.groupingBy(Map.Entry::getKey))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .map(Map.Entry::getValue)
                                .flatMap(List::stream)
                                .toList(),
                        (prev, next) -> next,
                        HashMap::new
                ));
    }

    public List<Data<?, ?>> asList() {
        return asClassifiedMap().values().stream()
                .flatMap(List::stream)
                .toList();
    }

    public Optional<Data<?, ?>> byId(String namespace, String... paths) {
        return Optional.ofNullable(asMap().get(namespace))
                .flatMap(map -> map.values().stream()
                        .flatMap(List::stream)
                        .filter(data -> data.path().equals(String.join(".", paths)))
                        .findAny());
    }

    public Optional<String> namespace(Data<?, ?> data) {
        return asMap().entrySet().stream()
                .filter(entry -> entry.getValue().values().stream()
                        .anyMatch(list -> list.contains(data)))
                .findAny()
                .map(Map.Entry::getKey);
    }

    public Optional<Identifier> identifier(Data<?, ?> data) {
        return namespace(data)
                .map(namespace -> new Identifier(namespace, data.path()));
    }

    public boolean isInNamespace(Data<?, ?> data, String namespace) {
        return namespace(data).equals(Optional.of(namespace));
    }

    public boolean isInDefaultNamespace(Data<?, ?> data) {
        return isInNamespace(data, Knowledges.ID);
    }

    public boolean isEnabled(Data<?, ?> data) {
        return !disabled.get(data);
    }

    public void setEnabled(Data<?, ?> data, boolean enabled) {
        disabled.set(data, !enabled);
    }

    <C extends Data<?, ?>> void register(String namespace, C data) {
        if (!asMap().containsKey(namespace)) map.put(namespace, new HashMap<>());
        Helper.Map.fastMerge(map.get(namespace), data.target(), data);
    }
}
