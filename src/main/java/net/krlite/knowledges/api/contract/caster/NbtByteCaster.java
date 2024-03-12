package net.krlite.knowledges.api.contract.caster;

import net.krlite.knowledges.api.contract.caster.base.NbtCaster;
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
