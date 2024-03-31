package net.krlite.knowledges.api.representable;

import net.krlite.knowledges.api.representable.base.Representable;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

import java.util.function.Supplier;

public interface EntityRepresentable extends Representable<EntityHitResult> {
    Entity entity();

    @Override
    default HitResult.Type type() {
        return HitResult.Type.ENTITY;
    }

    interface Builder extends Representable.Builder<EntityHitResult, EntityRepresentable, Builder> {
        default Builder entity(Entity entity) {
            return entitySupplier(() -> entity);
        }

        Builder entitySupplier(Supplier<Entity> entitySupplier);

        static Builder append(Builder builder, EntityRepresentable representable) {
            return Representable.Builder.append(builder, representable)
                    .entity(representable.entity());
        }
    }
}
