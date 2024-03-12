package net.krlite.knowledges.impl.entrypoint.contract;

import net.krlite.knowledges.api.contract.EntityContract;
import net.krlite.knowledges.impl.contract.entity.AnimalOwnerContract;
import net.krlite.knowledges.impl.contract.entity.ChickenEggContract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EntityContractProviderImpl implements EntityContract.Provider {
    @Override
    public @NotNull List<Class<? extends EntityContract>> provide() {
        return List.of(
                AnimalOwnerContract.class,
                ChickenEggContract.class
        );
    }
}
