package net.krlite.knowledges.impl.entrypoint;

import net.krlite.knowledges.api.entrypoint.TagProvider;
import net.krlite.knowledges.api.tag.AdditionalTag;
import net.krlite.knowledges.impl.tag.block.BeehiveTag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class KnowledgesTagProvider implements TagProvider {
    @Override
    public @NotNull List<Class<? extends AdditionalTag<?, ?>>> provide() {
        return List.of(
            BeehiveTag.class
        );
    }
}
