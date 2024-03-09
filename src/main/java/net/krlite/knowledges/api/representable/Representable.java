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

    HitResult.Type type();

    interface Builder<H extends HitResult, R extends Representable<H>, B extends Builder<H, R, B>> {
        B hitResult(H hitResult);

        B world(World world);

        B player(PlayerEntity player);

        B data(NbtCompound data);

        B hasServer(boolean hasServer);

        R build();

        static <H extends HitResult, R extends Representable<H>, B extends Builder<H, R, B>> B append(B builder, R representable) {
            return builder
                    .hitResult(representable.hitResult())
                    .world(representable.world())
                    .player(representable.player())
                    .data(representable.data())
                    .hasServer(representable.hasServer());
        }
    }
}
