package net.krlite.knowledges.networking;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.krlite.knowledges.KnowledgesCommon;
import net.krlite.knowledges.api.representable.impl.BlockRepresentableImpl;
import net.krlite.knowledges.api.representable.impl.EntityRepresentableImpl;
import net.krlite.knowledges.networking.base.KnowledgesNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class ServerNetworking implements KnowledgesNetworking {
    @Override
    public void register() {
        ServerPlayNetworking.registerGlobalReceiver(PACKET_PEEK_BLOCK, new PeekBlock());
        ServerPlayNetworking.registerGlobalReceiver(PACKET_PEEK_ENTITY, new PeekEntity());
        ServerPlayConnectionEvents.JOIN.register(new PlayerJoin());
    }

    public static class PeekBlock implements ServerPlayNetworking.PlayChannelHandler {
        @Override
        public void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
            BlockRepresentableImpl.onRequest(buf, player, server::execute, compound -> {
                PacketByteBuf data = PacketByteBufs.create();
                data.writeNbt(compound);
                responseSender.sendPacket(PACKET_RECEIVE_DATA, data);
            });
        }
    }

    public static class PeekEntity implements ServerPlayNetworking.PlayChannelHandler {
        @Override
        public void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
            EntityRepresentableImpl.onRequest(buf, player, server::execute, compound -> {
                PacketByteBuf data = PacketByteBufs.create();
                data.writeNbt(compound);
                responseSender.sendPacket(PACKET_RECEIVE_DATA, data);
            });
        }
    }

    public static class PlayerJoin implements ServerPlayConnectionEvents.Join {
        @Override
        public void onPlayReady(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
            ServerPlayerEntity player = handler.getPlayer();
            ServerPlayNetworking.send(player, PACKET_SERVER_PING, PacketByteBufs.create());
            KnowledgesCommon.CACHE_USERNAME.put(player);
        }
    }
}
