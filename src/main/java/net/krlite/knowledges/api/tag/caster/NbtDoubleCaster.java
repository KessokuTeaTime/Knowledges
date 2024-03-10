package net.krlite.knowledges.api.tag.caster;

import net.minecraft.nbt.NbtCompound;

public class NbtDoubleCaster extends NbtCaster<Double> {
    public NbtDoubleCaster(String identifier) {
        super(
                identifier,
                NbtCompound::getDouble,
                NbtCompound::putDouble
        );
    }
}
