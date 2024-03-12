package net.krlite.knowledges.impl.entrypoint.tag;

import net.krlite.knowledges.api.entrypoint.AdditionalTagProvider;
import net.krlite.knowledges.api.tag.AdditionalBlockTag;
import net.krlite.knowledges.impl.tag.block.BeehiveTag;
import net.krlite.knowledges.impl.tag.block.BrewingStandTag;
import net.krlite.knowledges.impl.tag.block.ChiseledBookshelfTag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AdditionalBlockTagProviderImpl implements AdditionalBlockTag.Provider {
    @Override
    public @NotNull List<Class<? extends AdditionalBlockTag>> provide() {
        return List.of(
                BeehiveTag.class,
                BrewingStandTag.class,
                ChiseledBookshelfTag.class
        );
    }
}
