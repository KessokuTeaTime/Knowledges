package net.krlite.knowledges.api.representable;

import net.minecraft.entity.Entity;
import net.minecraft.util.hit.EntityHitResult;

import java.util.function.Supplier;

public interface EntityRepresentable extends Representable<EntityHitResult> {
    Entity entity();

    @Override
    default Class<? extends Representable<EntityHitResult>> type() {
        return EntityRepresentable.class;
    }

    interface Builder extends Representable.Builder<EntityHitResult, EntityRepresentable, Builder> {
        default Builder entity(Entity entity) {
            return entitySupplier(() -> entity);
        }

        Builder entitySupplier(Supplier<Entity> entitySupplier);

        @Override
        EntityRepresentable build();

        static Builder append(Builder builder, EntityRepresentable representable) {
            return Representable.Builder.append(builder, representable)
                    .entity(representable.entity());
        }
    }
}
