package net.krlite.knowledges.networking;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.krlite.knowledges.KnowledgesCommon;
import net.krlite.knowledges.impl.representable.KnowledgesBlockRepresentable;
import net.krlite.knowledges.impl.representable.KnowledgesEntityRepresentable;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class KnowledgesNetworking {
    public static final Identifier PACKET_PEEK_BLOCK = identifier("packet_peek_block");
    public static final Identifier PACKET_PEEK_ENTITY = identifier("packet_peek_entity");
    public static final Identifier PACKET_RECEIVE_DATA = identifier("packet_receive_data");

    public static Identifier identifier(String path) {
        return new Identifier(KnowledgesCommon.ID + "_networking", path);
    }

    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(PACKET_PEEK_BLOCK, (server, player, handler, buf, responseSender) -> {
            KnowledgesBlockRepresentable.onRequest(buf, player, server::execute, compound -> {
                PacketByteBuf data = PacketByteBufs.create();
                data.writeNbt(compound);
                responseSender.sendPacket(PACKET_RECEIVE_DATA, data);
            });
        });
        ServerPlayNetworking.registerGlobalReceiver(PACKET_PEEK_ENTITY, (server, player, handler, buf, responseSender) -> {
            KnowledgesEntityRepresentable.onRequest(buf, player, server::execute, compound -> {
                PacketByteBuf data = PacketByteBufs.create();
                data.writeNbt(compound);
                responseSender.sendPacket(PACKET_RECEIVE_DATA, data);
            });
        });
    }
}
