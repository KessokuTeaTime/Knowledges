package net.krlite.knowledges.api.tag.caster;

import net.minecraft.nbt.NbtCompound;

public class NbtShortCaster extends NbtCaster<Short> {
    public NbtShortCaster(String identifier) {
        super(
                identifier,
                NbtCompound::getShort,
                NbtCompound::putShort
        );
    }
}
