package band.kessokuteatime.knowledges.manager;

import band.kessokuteatime.knowledges.KnowledgesClient;
import band.kessokuteatime.knowledges.api.data.Data;
import band.kessokuteatime.knowledges.api.component.Knowledge;
import band.kessokuteatime.knowledges.api.data.transfer.DataInvoker;
import band.kessokuteatime.knowledges.api.data.transfer.DataProtocol;
import band.kessokuteatime.knowledges.manager.base.EntrypointInvoker;
import band.kessokuteatime.knowledges.manager.base.Manager;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DataManager extends Manager<Data<?>> {
    public DataManager() {
        super(() -> KnowledgesClient.CONFIG.get().data.available);
    }

    @Override
    protected @NotNull EntrypointInvoker entrypoint() {
        return EntrypointInvoker.DATA;
    }

    public Map<? extends DataInvoker<?, ?>, List<Data<?>>> asDataInvokerClassifiedMap() {
        return asList().stream()
                .collect(Collectors.groupingBy(DataProtocol::dataInvoker));
    }

    public <K extends Knowledge> List<Data<K>> fromDataInvoker(DataInvoker<K, ?> dataInvoker) {
        return asDataInvokerClassifiedMap().getOrDefault(dataInvoker, new ArrayList<>()).stream()
                .map(data -> (Data<K>) data)
                .toList();
    }

    public <K extends Knowledge, P extends DataProtocol<K>> List<P> availableProtocols(DataInvoker<K, P> dataInvoker) {
        return fromDataInvoker(dataInvoker).stream()
                .filter(this::isEnabled)
                .map(data -> (P) data)
                .toList();
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