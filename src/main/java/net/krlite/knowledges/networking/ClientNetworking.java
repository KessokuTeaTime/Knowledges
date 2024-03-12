package net.krlite.knowledges.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.krlite.knowledges.KnowledgesClient;
import net.krlite.knowledges.api.representable.PacketByteBufWritable;
import net.krlite.knowledges.networking.base.KnowledgesNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class ClientNetworking implements KnowledgesNetworking {
    public static void requestDataFor(PacketByteBufWritable writable, Identifier channel) {
        PacketByteBuf buf = PacketByteBufs.create();
        writable.writeToBuf(buf);
        ClientPlayNetworking.send(channel, buf);
    }

    @Override
    public void register() {
        ClientPlayNetworking.registerGlobalReceiver(KnowledgesNetworking.PACKET_RECEIVE_DATA, new ReceiveData());
        ClientPlayNetworking.registerGlobalReceiver(KnowledgesNetworking.PACKET_SERVER_PING, new ServerPing());
        ClientPlayConnectionEvents.DISCONNECT.register(new PlayerDisconnect());
    }

    public static class ReceiveData implements ClientPlayNetworking.PlayChannelHandler {
        @Override
        public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
            NbtCompound compound = buf.readNbt();
            client.execute(() -> KnowledgesClient.HUD.onReceiveData(compound));
        }
    }

    public static class ServerPing implements ClientPlayNetworking.PlayChannelHandler {
        @Override
        public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
            client.execute(() -> KnowledgesClient.HUD.setConnectionStatus(true));
        }
    }

    public static class PlayerDisconnect implements ClientPlayConnectionEvents.Disconnect {
        @Override
        public void onPlayDisconnect(ClientPlayNetworkHandler handler, MinecraftClient client) {
            KnowledgesClient.HUD.setConnectionStatus(false);
        }
    }
}
