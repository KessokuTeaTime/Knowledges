package net.krlite.knowledges.api.contract;

import net.krlite.knowledges.api.core.path.WithPartialPath;
import net.krlite.knowledges.api.entrypoint.ContractProvider;
import net.krlite.knowledges.api.representable.EntityRepresentable;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.NotNull;

public interface EntityContract extends Contract<EntityRepresentable, Entity>, WithPartialPath {
    interface Provider extends ContractProvider<EntityContract> {

    }

    @Override
    default Class<EntityRepresentable> targetRepresentableClass() {
        return EntityRepresentable.class;
    }

    @Override
    default @NotNull String currentPath() {
        return "entity";
    };
}
