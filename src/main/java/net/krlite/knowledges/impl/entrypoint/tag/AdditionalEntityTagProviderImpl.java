package net.krlite.knowledges.impl.entrypoint.tag;

import net.krlite.knowledges.api.entrypoint.AdditionalTagProvider;
import net.krlite.knowledges.api.tag.AdditionalEntityTag;
import net.krlite.knowledges.impl.tag.entity.AnimalOwnerTag;
import net.krlite.knowledges.impl.tag.entity.ChickenEggTag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AdditionalEntityTagProviderImpl implements AdditionalEntityTag.Provider {
    @Override
    public @NotNull List<Class<? extends AdditionalEntityTag>> provide() {
        return List.of(
                AnimalOwnerTag.class,
                ChickenEggTag.class
        );
    }
}
