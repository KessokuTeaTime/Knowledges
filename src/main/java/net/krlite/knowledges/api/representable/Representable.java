package net.krlite.knowledges.api.representable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public interface Representable<H extends HitResult> {
    H hitResult();

    World world();

    PlayerEntity player();

    NbtCompound data();

    /**
     * @return {@code true} if the server has <b>Knowledges</b> installed.
     */
    boolean hasServer();

    Class<? extends Representable<H>> type();

    interface Builder<H extends HitResult, R extends Representable<H>> {
        Builder<H, R> hitResult(H hitResult);

        Builder<H, R> world(World world);

        Builder<H, R> player(PlayerEntity player);

        Builder<H, R> data(NbtCompound data);

        Builder<H, R> hasServer(boolean connected);

        Builder<H, R> create();

        Representable<H> build();

        default Builder<H, R> from(R representable) {
            return create()
                    .hitResult(representable.hitResult())
                    .world(representable.world())
                    .player(representable.player())
                    .data(representable.data())
                    .hasServer(representable.hasServer());
        }
    }
}
