package net.krlite.knowledges;

import net.krlite.knowledges.api.Data;
import net.krlite.knowledges.api.Knowledge;
import net.krlite.knowledges.config.disabled.DisabledDataConfig;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DataManager extends Knowledges.Manager<Data<?, ?, ?>> {
    DataManager() {
        super(new DisabledDataConfig());
    }

    @Override
    protected String localizationPrefix() {
        return "knowledge_data";
    }

    @Override
    void register(String namespace, Data<?, ?, ?> data) {
        super.register(namespace, data);
        data.registerListener();
    }

    public Map<Knowledge<?>, List<Data<?, ?, ?>>> asClassifiedMap() {
        return Map.copyOf(asList().stream()
                .filter(data -> data.knowledge().isPresent())
                .collect(Collectors.groupingBy(data -> data.knowledge().get())));
    }
}