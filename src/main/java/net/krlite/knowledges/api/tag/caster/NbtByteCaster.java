package net.krlite.knowledges.api.tag.caster;

import net.minecraft.nbt.NbtCompound;

public class NbtByteCaster extends NbtCaster<Byte> {
    public NbtByteCaster(String identifier) {
        super(
                identifier,
                NbtCompound::getByte,
                NbtCompound::putByte
        );
    }
}
