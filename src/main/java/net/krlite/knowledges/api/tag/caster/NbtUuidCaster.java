package net.krlite.knowledges.api.tag.caster;

import net.minecraft.nbt.NbtCompound;

import java.util.UUID;

public class NbtUuidCaster extends NbtCaster<UUID> {
    public NbtUuidCaster(String identifier) {
        super(
                identifier,
                NbtCompound::getUuid,
                NbtCompound::putUuid
        );
    }
}
