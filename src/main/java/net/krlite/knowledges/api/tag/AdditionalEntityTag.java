package net.krlite.knowledges.api.tag;

import net.krlite.knowledges.api.core.path.WithPartialPath;
import net.krlite.knowledges.api.entrypoint.AdditionalTagProvider;
import net.krlite.knowledges.api.representable.EntityRepresentable;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.NotNull;

public interface AdditionalEntityTag extends AdditionalTag<EntityRepresentable, Entity>, WithPartialPath {
    interface Provider extends AdditionalTagProvider<AdditionalEntityTag> {

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
