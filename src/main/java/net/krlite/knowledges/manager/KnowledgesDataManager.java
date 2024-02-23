package net.krlite.knowledges.manager;

import net.krlite.knowledges.Knowledges;
import net.krlite.knowledges.api.Data;
import net.krlite.knowledges.api.Knowledge;
import net.krlite.knowledges.config.disabled.DisabledDataConfig;
import net.krlite.knowledges.core.data.DataInvoker;
import net.krlite.knowledges.core.data.DataProtocol;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class KnowledgesDataManager extends AbstractManager<Data<?>> {
    public KnowledgesDataManager() {
        super(() -> Knowledges.CONFIG.data.disabled);
    }

    @Override
    protected String localizationPrefix() {
        return "knowledge_data";
    }

    public Map<? extends DataInvoker<?, ?>, List<Data<?>>> asDataInvokerClassifiedMap() {
        return asList().stream()
                .collect(Collectors.groupingBy(data -> data.dataInvoker()));
    }

    public <K extends Knowledge> List<Data<K>> fromDataInvoker(DataInvoker<K, ?> dataInvoker) {
        return asDataInvokerClassifiedMap().getOrDefault(dataInvoker, new ArrayList<>()).stream()
                .map(data -> (Data<K>) data)
                .collect(Collectors.toList());
    }

    public <K extends Knowledge, P extends DataProtocol<K>> List<P> availableProtocols(DataInvoker<K, P> dataInvoker) {
        return fromDataInvoker(dataInvoker).stream()
                .filter(this::isEnabled)
                .map(data -> (P) data)
                .collect(Collectors.toList());
    }

    public Map<String, Map<Knowledge, List<Data<?>>>> asNamespaceKnowledgeClassifiedMap() {
        return Map.copyOf(asMap().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .filter(data -> data.dataInvoker().targetKnowledge().isPresent())
                                .collect(Collectors.groupingBy(data -> data.dataInvoker().targetKnowledge().get()))
                ))
        );
    }
}