package net.krlite.knowledges.impl.representable;

import net.krlite.knowledges.api.representable.Representable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

import java.util.function.Supplier;

public abstract class RepresentableImpl<H extends HitResult> implements Representable<H> {
    private final Supplier<H> hitResultSupplier;
    private final World world;
    private final PlayerEntity player;
    private final NbtCompound data;
    private final boolean hasServer;

    protected RepresentableImpl(Supplier<H> hitResultSupplier, World world, PlayerEntity player, NbtCompound data, boolean hasServer) {
        this.hitResultSupplier = hitResultSupplier;
        this.world = world;
        this.player = player;
        this.data = data != null ? data : new NbtCompound();
        this.hasServer = hasServer;
    }

    protected <B extends Builder<H>> RepresentableImpl(B builder) {
        this(builder.hitResultSupplier, builder.world, builder.player, builder.data, builder.hasServer);
    }

    @Override
    public H hitResult() {
        return hitResultSupplier.get();
    }

    @Override
    public World world() {
        return world;
    }

    @Override
    public PlayerEntity player() {
        return player;
    }

    @Override
    public NbtCompound data() {
        return data;
    }

    @Override
    public boolean hasServer() {
        return hasServer;
    }

    public static class Builder<H extends HitResult> {
        protected Supplier<H> hitResultSupplier;
        protected World world;
        protected PlayerEntity player;
        protected NbtCompound data;
        protected boolean hasServer;
    }
}
