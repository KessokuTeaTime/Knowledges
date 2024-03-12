package net.krlite.knowledges.impl.entrypoint.contract;

import net.krlite.knowledges.api.contract.AdditionalEntityTag;
import net.krlite.knowledges.impl.contract.entity.AnimalOwnerContract;
import net.krlite.knowledges.impl.contract.entity.ChickenEggContract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AdditionalEntityTagProviderImpl implements AdditionalEntityTag.Provider {
    @Override
    public @NotNull List<Class<? extends AdditionalEntityTag>> provide() {
        return List.of(
                AnimalOwnerContract.class,
                ChickenEggContract.class
        );
    }
}
