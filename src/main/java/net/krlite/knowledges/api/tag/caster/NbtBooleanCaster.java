package net.krlite.knowledges.api.tag.caster;

import net.minecraft.nbt.NbtCompound;

public class NbtBooleanCaster extends NbtCaster<Boolean> {
    public NbtBooleanCaster(String identifier) {
        super(
                identifier,
                NbtCompound::getBoolean,
                NbtCompound::putBoolean
        );
    }
}
