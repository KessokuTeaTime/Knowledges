package net.krlite.knowledges.api.representable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.Optional;
import java.util.function.Supplier;

public interface BlockRepresentable extends Representable<BlockHitResult> {
    Block block();

    BlockState blockState();

    Optional<BlockEntity> blockEntity();

    BlockPos blockPos();

    Direction side();

    @Override
    default HitResult.Type type() {
        return HitResult.Type.BLOCK;
    }

    interface Builder extends Representable.Builder<BlockHitResult, BlockRepresentable, Builder> {
        Builder blockState(BlockState blockState);

        default Builder blockEntity(BlockEntity blockEntity) {
            return blockEntity != null ? blockEntitySupplier(() -> blockEntity) : this;
        }

        Builder blockEntitySupplier(Supplier<BlockEntity> blockEntitySupplier);

        static Builder append(Builder builder, BlockRepresentable representable) {
            return Representable.Builder.append(builder, representable)
                    .blockState(representable.blockState())
                    .blockEntity(representable.blockEntity().orElse(null));
        }
    }
}
