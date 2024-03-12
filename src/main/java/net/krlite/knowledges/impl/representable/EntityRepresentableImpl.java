package net.krlite.knowledges.impl.representable;

import com.google.common.base.Suppliers;
import net.krlite.knowledges.KnowledgesCommon;
import net.krlite.knowledges.api.representable.EntityRepresentable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class EntityRepresentableImpl extends RepresentableImpl<EntityHitResult> implements EntityRepresentable {
    private final Supplier<Entity> entitySupplier;

    public EntityRepresentableImpl(Builder builder) {
        super(builder);
        this.entitySupplier = builder.entitySupplier;
    }

    @Override
    public Entity entity() {
        return entitySupplier.get();
    }
    
    public static void onRequest(PacketByteBuf buf, ServerPlayerEntity player, Consumer<Runnable> executor, Consumer<NbtCompound> responseSender) {
        EntityRepresentableImpl representable = Builder.from(buf, player).build();
        executor.accept(() -> {
            Entity entity = representable.entity();
            if (entity == null) return;

            var tags = KnowledgesCommon.TAGS.byEntity(representable.entity());
            if (tags.isEmpty()) return;

            NbtCompound compound = representable.data();
            tags.forEach(t -> t.append(compound, representable));

            compound.putInt("id", entity.getId());

            responseSender.accept(compound);
        });
    }

    @Override
    public void writeToBuf(PacketByteBuf buf) {
        buf.writeVarInt(entity().getId());
        
        Vec3d hitPos = hitResult().getPos();
        buf.writeDouble(hitPos.getX());
        buf.writeDouble(hitPos.getY());
        buf.writeDouble(hitPos.getZ());
    }

    public static class Builder extends RepresentableImpl.Builder<EntityHitResult> implements EntityRepresentable.Builder {
        private Supplier<Entity> entitySupplier;

        @Override
        public Builder entitySupplier(Supplier<Entity> entitySupplier) {
            this.entitySupplier = entitySupplier;
            return this;
        }

        @Override
        public Builder hitResultSupplier(Supplier<EntityHitResult> hitResultSupplier) {
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
        public EntityRepresentableImpl build() {
            return new EntityRepresentableImpl(this);
        }

        public static Builder create() {
            return new Builder();
        }

        public static Builder from(EntityRepresentable representable) {
            return (Builder) EntityRepresentable.Builder.append(create(), representable);
        }
        
        public static Builder from(PacketByteBuf buf, ServerPlayerEntity player) {
            Builder builder = create();
            
            builder.world(player.getWorld());
            builder.player(player);
            
            int entityId = buf.readVarInt();
            double x = buf.readDouble(), y = buf.readDouble(), z = buf.readDouble();
            
            // Performs actions on the main thread
            Supplier<Entity> entitySupplier = Suppliers.memoize(() -> builder.world.getEntityById(entityId));
            builder.entitySupplier(entitySupplier);
            builder.hitResultSupplier(Suppliers.memoize(() -> new EntityHitResult(entitySupplier.get(), new Vec3d(x, y, z))));
            
            return builder;
        }
    }
}
