package net.krlite.knowledges.api.representable;

import net.minecraft.entity.Entity;
import net.minecraft.util.hit.EntityHitResult;

public interface EntityRepresentable extends Representable<EntityHitResult> {
    Entity entity();

    @Override
    default Class<? extends Representable<EntityHitResult>> type() {
        return EntityRepresentable.class;
    }

    interface Builder extends Representable.Builder<EntityHitResult, EntityRepresentable> {
        Builder entity(Entity entity);

        @Override
        Builder create();

        @Override
        EntityRepresentable build();

        @Override
        default Builder from(EntityRepresentable representable) {
            return ((Builder) Representable.Builder.super.from(representable))
                    .entity(representable.entity());
        }
    }
}
