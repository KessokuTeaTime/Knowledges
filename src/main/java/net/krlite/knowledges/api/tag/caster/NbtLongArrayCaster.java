package net.krlite.knowledges.api.tag.caster;

import net.minecraft.nbt.NbtCompound;

import java.util.Arrays;
import java.util.List;

public class NbtLongArrayCaster extends NbtCaster<List<Long>> {
    public NbtLongArrayCaster(String identifier) {
        super(
                identifier,
                (data, key) -> Arrays.stream(data.getLongArray(key)).boxed().toList(),
                NbtCompound::putLongArray
        );
    }

    public void set(NbtCompound data, long[] longs) {
        super.set(data, Arrays.stream(longs).boxed().toList());
    }
}
