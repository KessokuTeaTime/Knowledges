package net.krlite.knowledges.impl.representable;

import net.krlite.knowledges.api.representable.BlockRepresentable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.function.Supplier;

public class KnowledgesBlockRepresentable extends KnowledgesRepresentable<BlockHitResult> implements BlockRepresentable {
    private final BlockState blockState;
    private final Supplier<BlockEntity> blockEntitySupplier;

    public KnowledgesBlockRepresentable(Builder builder) {
        super(builder);
        this.blockState = builder.blockState;
        this.blockEntitySupplier = builder.blockEntitySupplier;
    }

    @Override
    public Block block() {
        return blockState.getBlock();
    }

    @Override
    public BlockState blockState() {
        return blockState;
    }

    @Override
    public BlockEntity blockEntity() {
        return blockEntitySupplier.get();
    }

    @Override
    public BlockPos blockPos() {
        return hitResult().getBlockPos();
    }

    @Override
    public Direction side() {
        return hitResult().getSide();
    }

    public static final class Builder extends KnowledgesRepresentable.Builder<BlockHitResult> implements BlockRepresentable.Builder {
        private BlockState blockState = Blocks.AIR.getDefaultState();
        private Supplier<BlockEntity> blockEntitySupplier;

        @Override
        public Builder hitResult(BlockHitResult hitResult) {
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
        public Builder blockState(BlockState blockState) {
            this.blockState = blockState;
            return this;
        }

        @Override
        public Builder blockEntitySupplier(Supplier<BlockEntity> blockEntitySupplier) {
            this.blockEntitySupplier = blockEntitySupplier;
            return this;
        }

        @Override
        public KnowledgesBlockRepresentable build() {
            return new KnowledgesBlockRepresentable(this);
        }

        public static Builder create() {
            return new Builder();
        }

        public static Builder from(BlockRepresentable representable) {
            return (Builder) BlockRepresentable.Builder.append(create(), representable);
        }
    }
}
