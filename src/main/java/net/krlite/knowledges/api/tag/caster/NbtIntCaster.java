package net.krlite.knowledges.api.tag.caster;

import net.minecraft.nbt.NbtCompound;

public class NbtIntCaster extends NbtCaster<Integer> {
    public NbtIntCaster(String identifier) {
        super(
                identifier,
                NbtCompound::getInt,
                NbtCompound::putInt
        );
    }
}
