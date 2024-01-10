package net.krlite.knowledges;

import net.krlite.knowledges.api.Data;
import net.krlite.knowledges.api.DataSource;
import net.krlite.knowledges.api.Knowledge;
import net.krlite.knowledges.config.disabled.DisabledDataConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class KnowledgesDataManager extends Knowledges.Manager<Data<?, ?>> {
    KnowledgesDataManager() {
        super(new DisabledDataConfig());
    }

    @Override
    protected String localizationPrefix() {
        return "knowledge_data";
    }

    public Map<Class<DataSource<?, ?>>, List<Data<?, ?>>> asSourceClassifiedMap() {
        return asList().stream()
                .collect(Collectors.groupingBy(data -> (Class<DataSource<?, ?>>) data.source()));
    }

    public <K extends Knowledge, S extends DataSource<K, S>> List<Data<K, S>> fromSource(Class<? extends DataSource<K, S>> sourceClass) {
        return asSourceClassifiedMap().getOrDefault(sourceClass, new ArrayList<>()).stream()
                .map(data -> (Data<K, S>) data)
                .collect(Collectors.toList());
    }

    public <K extends Knowledge, S extends DataSource<K, S>> List<S> availableListenersFromSource(Class<? extends DataSource<K, S>> sourceClass) {
        return fromSource(sourceClass).stream()
                .filter(this::isEnabled)
                .map(data -> (S) data)
                .collect(Collectors.toList());
    }
}