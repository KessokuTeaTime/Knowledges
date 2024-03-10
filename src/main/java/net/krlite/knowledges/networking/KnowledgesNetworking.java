package net.krlite.knowledges.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.krlite.knowledges.KnowledgesClient;
import net.krlite.knowledges.KnowledgesCommon;
import net.krlite.knowledges.impl.representable.KnowledgesBlockRepresentable;
import net.krlite.knowledges.impl.representable.KnowledgesEntityRepresentable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public interface KnowledgesNetworking {
    Identifier PACKET_PEEK_BLOCK = identifier("packet_peek_block");
    Identifier PACKET_PEEK_ENTITY = identifier("packet_peek_entity");
    Identifier PACKET_RECEIVE_DATA = identifier("packet_receive_data");
    Identifier PACKET_SERVER_PING = identifier("packet_server_ping");

    static Identifier identifier(String path) {
        return new Identifier(KnowledgesCommon.ID + "_networking", path);
    }

    void register();
}
