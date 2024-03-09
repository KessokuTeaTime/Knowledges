package net.krlite.knowledges.api.representable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public interface BlockRepresentable extends Representable<BlockHitResult> {
    Block block();

    BlockState blockState();

    BlockEntity blockEntity();

    BlockPos blockPos();

    Direction direction();

    @Override
    default Class<? extends Representable<BlockHitResult>> type() {
        return BlockRepresentable.class;
    }

    interface Builder extends Representable.Builder<BlockHitResult, BlockRepresentable> {
        Builder block(Block block);

        Builder blockState(BlockState blockState);

        Builder blockEntity(BlockEntity blockEntity);

        Builder blockPos(BlockPos blockPos);

        Builder direction(Direction direction);

        @Override
        Builder create();

        @Override
        BlockRepresentable build();

        @Override
        default Builder from(BlockRepresentable representable) {
            return ((Builder) Representable.Builder.super.from(representable))
                    .block(representable.block())
                    .blockState(representable.blockState())
                    .blockEntity(representable.blockEntity())
                    .blockPos(representable.blockPos())
                    .direction(representable.direction());
        }
    }
}
