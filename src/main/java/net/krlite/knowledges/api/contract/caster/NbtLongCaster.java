package net.krlite.knowledges.api.contract.caster;

import net.krlite.knowledges.api.contract.caster.base.NbtCaster;
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
