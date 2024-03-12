package net.krlite.knowledges.api.contract.caster;

import net.krlite.knowledges.api.contract.caster.base.NbtCaster;
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
