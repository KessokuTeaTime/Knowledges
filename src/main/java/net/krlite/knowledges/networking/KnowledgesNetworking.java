package net.krlite.knowledges.networking;

import net.krlite.knowledges.KnowledgesCommon;
import net.minecraft.util.Identifier;

public class KnowledgesNetworking {
    public static final Identifier PACKET_PEEK_BLOCK = identifier("packet_peek_block");
    public static final Identifier PACKET_PEEK_ENTITY = identifier("packet_peek_entity");

    public static Identifier identifier(String path) {
        return new Identifier(KnowledgesCommon.ID + "_networking", path);
    }
}
