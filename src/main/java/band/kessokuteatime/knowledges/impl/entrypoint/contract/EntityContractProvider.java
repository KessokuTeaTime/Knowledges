package band.kessokuteatime.knowledges.impl.entrypoint.contract;

import band.kessokuteatime.knowledges.api.contract.EntityContract;
import band.kessokuteatime.knowledges.impl.contract.entity.AnimalOwnerContract;
import band.kessokuteatime.knowledges.impl.contract.entity.ChickenEggContract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EntityContractProvider implements EntityContract.Provider {
    @Override
    public @NotNull List<Class<? extends EntityContract>> provide() {
        return List.of(
                AnimalOwnerContract.class,
                ChickenEggContract.class
        );
    }
}
