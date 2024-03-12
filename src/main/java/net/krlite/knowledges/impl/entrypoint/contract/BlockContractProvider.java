package net.krlite.knowledges.impl.entrypoint.contract;

import net.krlite.knowledges.api.contract.BlockContract;
import net.krlite.knowledges.impl.contract.block.BeehiveContract;
import net.krlite.knowledges.impl.contract.block.BrewingStandContract;
import net.krlite.knowledges.impl.contract.block.ChiseledBookshelfContract;
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
