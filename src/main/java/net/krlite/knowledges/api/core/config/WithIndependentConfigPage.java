package net.krlite.knowledges.api.core.config;

import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.AbstractFieldBuilder;
import me.shedaniel.clothconfig2.impl.builders.FieldBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public interface WithIndependentConfigPage {
    default boolean requestsConfigPage() {
        return false;
    }

    default Function<ConfigEntryBuilder, List<FieldBuilder<?, ?, ?>>> buildConfigEntries() {
        return configEntryBuilder -> new ArrayList<>();
    }
}
