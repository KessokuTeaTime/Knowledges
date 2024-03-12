package net.krlite.knowledges.api.contract.caster;

import net.krlite.knowledges.api.contract.caster.base.NbtCaster;
import net.minecraft.nbt.NbtCompound;

import java.util.Arrays;
import java.util.List;

public class NbtIntArrayCaster extends NbtCaster<List<Integer>> {
    public NbtIntArrayCaster(String identifier) {
        super(
                identifier,
                (data, key) -> Arrays.stream(data.getIntArray(key)).boxed().toList(),
                NbtCompound::putIntArray
        );
    }

    public void put(NbtCompound data, int[] integers) {
        super.put(data, Arrays.stream(integers).boxed().toList());
    }
}
