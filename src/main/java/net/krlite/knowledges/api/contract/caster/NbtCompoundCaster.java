package net.krlite.knowledges.api.contract.caster;

import net.krlite.knowledges.api.contract.caster.base.NbtCaster;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;

public class NbtCompoundCaster extends NbtCaster<NbtCompound> {
    public NbtCompoundCaster(String identifier) {
        super(
                identifier,
                NbtCompound::getCompound,
                (data, key, nbtCompound) -> nbtCompound.put(key, nbtCompound)
        );
    }

    public void put(NbtCompound data, BlockEntity blockEntity) {
        super.put(data, blockEntity.createNbt());
    }
}
