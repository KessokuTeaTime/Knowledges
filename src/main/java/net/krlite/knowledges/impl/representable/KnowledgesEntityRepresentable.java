package net.krlite.knowledges.impl.representable;

import net.krlite.knowledges.api.representable.EntityRepresentable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

import java.util.function.Supplier;

public class KnowledgesEntityRepresentable extends KnowledgesRepresentable<EntityHitResult> implements EntityRepresentable {
    private final Supplier<Entity> entitySupplier;

    public KnowledgesEntityRepresentable(Builder builder) {
        super(builder);
        this.entitySupplier = builder.entitySupplier;
    }

    @Override
    public Entity entity() {
        return entitySupplier.get();
    }

    public static class Builder extends KnowledgesRepresentable.Builder<EntityHitResult> implements EntityRepresentable.Builder {
        private Supplier<Entity> entitySupplier;

        @Override
        public Builder entitySupplier(Supplier<Entity> entitySupplier) {
            this.entitySupplier = entitySupplier;
            return this;
        }

        @Override
        public Builder hitResult(EntityHitResult hitResult) {
            this.hitResult = hitResult;
            return this;
        }

        @Override
        public Builder world(World world) {
            this.world = world;
            return this;
        }

        @Override
        public Builder player(PlayerEntity player) {
            this.player = player;
            return this;
        }

        @Override
        public Builder data(NbtCompound data) {
            this.data = data;
            return this;
        }

        @Override
        public Builder hasServer(boolean hasServer) {
            this.hasServer = hasServer;
            return this;
        }

        @Override
        public EntityRepresentable build() {
            return new KnowledgesEntityRepresentable(this);
        }

        public static Builder create() {
            return new Builder();
        }

        public static Builder from(EntityRepresentable representable) {
            return (Builder) EntityRepresentable.Builder.append(create(), representable);
        }
    }
}
