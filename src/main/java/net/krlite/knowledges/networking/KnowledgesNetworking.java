package net.krlite.knowledges.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.krlite.knowledges.KnowledgesClient;
import net.krlite.knowledges.KnowledgesCommon;
import net.krlite.knowledges.impl.representable.KnowledgesBlockRepresentable;
import net.krlite.knowledges.impl.representable.KnowledgesEntityRepresentable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class KnowledgesNetworking {
    public static final Identifier PACKET_PEEK_BLOCK = identifier("packet_peek_block");
    public static final Identifier PACKET_PEEK_ENTITY = identifier("packet_peek_entity");
    public static final Identifier PACKET_RECEIVE_DATA = identifier("packet_receive_data");
    public static final Identifier PACKET_SERVER_PING = identifier("packet_server_ping");

    public static Identifier identifier(String path) {
        return new Identifier(KnowledgesCommon.ID + "_networking", path);
    }

    public static void registerServer() {
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
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> ServerPlayNetworking.send(handler.getPlayer(), PACKET_SERVER_PING, PacketByteBufs.create()));
    }

    public static void registerClient() {
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> KnowledgesClient.HUD.setConnectionStatus(false));
        ClientPlayNetworking.registerGlobalReceiver(KnowledgesNetworking.PACKET_RECEIVE_DATA, (client, handler, buf, responseSender) -> {
            NbtCompound compound = buf.readNbt();
            client.execute(() -> KnowledgesClient.HUD.onReceiveData(compound));
        });
        ClientPlayNetworking.registerGlobalReceiver(KnowledgesNetworking.PACKET_SERVER_PING, (client, handler, buf, responseSender) ->
                client.execute(() -> KnowledgesClient.HUD.setConnectionStatus(true))
        );
    }
}
