package band.kessokuteatime.knowledges.impl.entrypoint.contract;

import band.kessokuteatime.knowledges.api.contract.BlockContract;
import band.kessokuteatime.knowledges.impl.contract.block.BeehiveContract;
import band.kessokuteatime.knowledges.impl.contract.block.BrewingStandContract;
import band.kessokuteatime.knowledges.impl.contract.block.ChiseledBookshelfContract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BlockContractProvider implements BlockContract.Provider {
    @Override
    public @NotNull List<Class<? extends BlockContract>> provide() {
        return List.of(
                BeehiveContract.class,
                BrewingStandContract.class,
                ChiseledBookshelfContract.class
        );
    }
}
