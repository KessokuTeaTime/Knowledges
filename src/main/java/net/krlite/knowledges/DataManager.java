package net.krlite.knowledges;

import net.krlite.knowledges.api.Data;
import net.krlite.knowledges.config.disabled.DisabledDataConfig;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DataManager extends Knowledges.Manager<Data<?, ?>> {
    DataManager() {
        super(new DisabledDataConfig());
    }

    @Override
    protected String localizationPrefix() {
        return "knowledge_data";
    }

    public Map<Identifier, List<Data<?, ?>>> asClassifiedMap() {
        return Map.copyOf(asList().stream()
                .collect(Collectors.groupingBy(Data::target)));
    }
}
