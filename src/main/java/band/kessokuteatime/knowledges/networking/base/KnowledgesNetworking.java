package band.kessokuteatime.knowledges.networking.base;

import band.kessokuteatime.knowledges.KnowledgesCommon;
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
