package net.krlite.knowledges.impl.representable;

import net.krlite.knowledges.api.representable.Representable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public class EmptyRepresentable extends KnowledgesRepresentable<HitResult> implements Representable<HitResult> {
    public EmptyRepresentable(Builder builder) {
        super(builder);
    }

    @Override
    public HitResult.Type type() {
        return HitResult.Type.MISS;
    }

    public static class Builder extends KnowledgesRepresentable.Builder<HitResult> implements Representable.Builder<HitResult, EmptyRepresentable, Builder> {
        @Override
        public Builder hitResult(HitResult hitResult) {
            this.hitResultSupplier = hitResult;
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
        public EmptyRepresentable build() {
            return new EmptyRepresentable(this);
        }

        public static Builder create() {
            return new Builder();
        }

        public static Builder from(EmptyRepresentable representable) {
            return Representable.Builder.append(create(), representable);
        }
    }
}
