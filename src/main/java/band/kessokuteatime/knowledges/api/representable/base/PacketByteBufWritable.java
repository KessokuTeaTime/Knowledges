package band.kessokuteatime.knowledges.api.representable.base;

import net.minecraft.network.PacketByteBuf;

public interface PacketByteBufWritable {
    void writeToBuf(PacketByteBuf buf);
}
