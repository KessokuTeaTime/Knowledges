package net.krlite.knowledges.api.tag.caster;

import net.minecraft.nbt.NbtCompound;

public class NbtLongCaster extends NbtCaster<Long> {
    public NbtLongCaster(String identifier) {
        super(
                identifier,
                NbtCompound::getLong,
                NbtCompound::putLong
        );
    }
}
