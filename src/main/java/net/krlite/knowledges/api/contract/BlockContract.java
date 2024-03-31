package net.krlite.knowledges.api.contract;

import net.krlite.knowledges.api.core.path.WithPartialPath;
import net.krlite.knowledges.api.entrypoint.ContractProvider;
import net.krlite.knowledges.api.representable.BlockRepresentable;
import net.minecraft.block.Block;
import org.jetbrains.annotations.NotNull;

public interface BlockContract extends Contract<BlockRepresentable, Block>, WithPartialPath {
    interface Provider extends ContractProvider<BlockContract> {

    }

    @Override
    default Class<BlockRepresentable> targetRepresentableClass() {
        return BlockRepresentable.class;
    }

    @Override
    default @NotNull String currentPath() {
        return "block";
    };
}
