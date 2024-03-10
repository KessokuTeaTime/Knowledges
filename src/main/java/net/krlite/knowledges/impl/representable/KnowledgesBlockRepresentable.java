package net.krlite.knowledges.impl.representable;

import com.google.common.base.Suppliers;
import net.krlite.knowledges.KnowledgesCommon;
import net.krlite.knowledges.api.proxy.KnowledgeProxy;
import net.krlite.knowledges.api.representable.BlockRepresentable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.function.Consumer;
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
    public Optional<BlockEntity> blockEntity() {
        return Optional.ofNullable(blockEntitySupplier).map(Supplier::get);
    }

    @Override
    public BlockPos blockPos() {
        return hitResult().getBlockPos();
    }

    @Override
    public Direction side() {
        return hitResult().getSide();
    }

    public static void onRequest(PacketByteBuf buf, ServerPlayerEntity player, Consumer<Runnable> executor, Consumer<NbtCompound> responseSender) {
        KnowledgesBlockRepresentable representable = Builder.from(buf, player).build();
        executor.accept(() -> {
            BlockPos pos = representable.blockPos();
            ServerWorld world = player.getServerWorld();
            if (!world.canSetBlock(pos)) return;

            Optional<BlockEntity> blockEntity = representable.blockEntity();
            if (blockEntity.isEmpty()) return;

            var tags = KnowledgesCommon.TAGS.byBlock(representable.block());
            if (tags.isEmpty()) return;

            NbtCompound compound = representable.data();
            tags.forEach(t -> t.append(compound, representable));

            compound.putInt("x", pos.getX());
            compound.putInt("y", pos.getY());
            compound.putInt("z", pos.getZ());
            compound.putString("id", KnowledgeProxy.getId(blockEntity.get().getType()).toString());

            responseSender.accept(compound);
        });
    }

    @Override
    public void writeToBuf(PacketByteBuf buf) {
        buf.writeBlockHitResult(hitResult());
        buf.writeVarInt(Block.getRawIdFromState(blockState()));
    }

    public static final class Builder extends KnowledgesRepresentable.Builder<BlockHitResult> implements BlockRepresentable.Builder {
        private BlockState blockState = Blocks.AIR.getDefaultState();
        private Supplier<BlockEntity> blockEntitySupplier;

        @Override
        public Builder hitResultSupplier(Supplier<BlockHitResult> hitResultSupplier) {
            this.hitResultSupplier = hitResultSupplier;
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

        public static Builder from(PacketByteBuf buf, ServerPlayerEntity player) {
            Builder builder = create();

            builder.world(player.getWorld());
            builder.player(player);
            builder.hitResult(buf.readBlockHitResult());
            builder.blockState(Block.getStateFromRawId(buf.readVarInt()));

            if (builder.blockState.hasBlockEntity()) {
                // Performs actions on the main thread
                builder.blockEntitySupplier(Suppliers.memoize(() -> builder.world.getBlockEntity(builder.hitResultSupplier.get().getBlockPos())));
            }

            return builder;
        }
    }
}
