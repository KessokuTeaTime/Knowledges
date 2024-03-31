package net.krlite.knowledges.api.contract.caster;

import net.krlite.knowledges.api.contract.caster.base.NbtCaster;
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
