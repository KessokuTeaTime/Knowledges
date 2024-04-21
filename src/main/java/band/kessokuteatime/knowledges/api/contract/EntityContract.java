package band.kessokuteatime.knowledges.api.contract;

import band.kessokuteatime.knowledges.api.core.path.WithPartialPath;
import band.kessokuteatime.knowledges.api.entrypoint.ContractProvider;
import band.kessokuteatime.knowledges.api.representable.EntityRepresentable;
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
