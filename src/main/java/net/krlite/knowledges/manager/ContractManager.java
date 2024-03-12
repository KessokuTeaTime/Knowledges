package net.krlite.knowledges.manager;

import net.krlite.knowledges.KnowledgesCommon;
import net.krlite.knowledges.api.contract.BlockContract;
import net.krlite.knowledges.api.contract.Contract;
import net.krlite.knowledges.api.contract.EntityContract;
import net.krlite.knowledges.manager.base.Manager;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;

import java.util.List;

public class ContractManager extends Manager<Contract<?, ?>> {
    public ContractManager() {
        super(() -> KnowledgesCommon.CONFIG.get().tags.enabled);
    }

    @Override
    protected String localizationPrefix() {
        return "tag";
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
