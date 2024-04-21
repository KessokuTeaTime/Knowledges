package band.kessokuteatime.knowledges.api.representable.base;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

import java.util.function.Supplier;

public interface Representable<H extends HitResult> extends PacketByteBufWritable {
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
        default B hitResult(H hitResult) {
            return hitResultSupplier(() -> hitResult);
        }

        B hitResultSupplier(Supplier<H> hitResultSupplier);

        B world(World world);

        B player(PlayerEntity player);

        B data(NbtCompound data);

        B hasServer(boolean hasServer);

        R build();

        static <H extends HitResult, R extends Representable<H>, B extends Builder<H, R, B>> B append(B builder, R representable) {
            return builder
                    .hitResultSupplier(representable::hitResult)
                    .world(representable.world())
                    .player(representable.player())
                    .data(representable.data())
                    .hasServer(representable.hasServer());
        }
    }
}
