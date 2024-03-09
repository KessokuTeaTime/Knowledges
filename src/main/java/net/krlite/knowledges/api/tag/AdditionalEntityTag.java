package net.krlite.knowledges.api.tag;

import net.krlite.knowledges.api.representable.EntityRepresentable;
import net.minecraft.entity.Entity;

public interface AdditionalEntityTag extends AdditionalTag<EntityRepresentable, Entity> {
    @Override
    default Class<EntityRepresentable> targetRepresentableClass() {
        return EntityRepresentable.class;
    }
}
