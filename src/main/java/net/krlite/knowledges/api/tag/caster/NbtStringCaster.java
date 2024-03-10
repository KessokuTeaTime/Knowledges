package net.krlite.knowledges.api.tag.caster;

import net.minecraft.nbt.NbtCompound;

public class NbtStringCaster extends NbtCaster<String> {
    public NbtStringCaster(String identifier) {
        super(
                identifier,
                NbtCompound::getString,
                NbtCompound::putString
        );
    }
}
