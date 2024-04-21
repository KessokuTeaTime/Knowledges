package band.kessokuteatime.knowledges.manager;

import band.kessokuteatime.knowledges.KnowledgesCommon;
import band.kessokuteatime.knowledges.api.contract.BlockContract;
import band.kessokuteatime.knowledges.api.contract.Contract;
import band.kessokuteatime.knowledges.api.contract.EntityContract;
import band.kessokuteatime.knowledges.manager.base.EntrypointInvoker;
import band.kessokuteatime.knowledges.manager.base.Manager;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ContractManager extends Manager<Contract<?, ?>> {
    public ContractManager() {
        super(() -> KnowledgesCommon.CONFIG.get().contracts.available);
    }

    @Override
    protected @NotNull EntrypointInvoker entrypoint() {
        return EntrypointInvoker.CONTRACT;
    }

    public <T extends Contract<?, ?>> List<T> ofSpecifiedType(Class<T> tClass) {
        return asList().stream()
                .filter(t -> tClass.isAssignableFrom(t.getClass()))
                .map(t -> (T) t)
                .toList();
    }

    public List<BlockContract> byBlock(Block block) {
        return ofSpecifiedType(BlockContract.class).stream()
                .filter(t -> t.isApplicableTo(block))
                .toList();
    }

    public List<EntityContract> byEntity(Entity entity) {
        return ofSpecifiedType(EntityContract.class).stream()
                .filter(t -> t.isApplicableTo(entity))
                .toList();
    }
}
