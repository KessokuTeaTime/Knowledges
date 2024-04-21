package band.kessokuteatime.knowledges.api.contract;

import band.kessokuteatime.knowledges.api.core.path.WithPartialPath;
import band.kessokuteatime.knowledges.api.entrypoint.ContractProvider;
import band.kessokuteatime.knowledges.api.representable.BlockRepresentable;
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
