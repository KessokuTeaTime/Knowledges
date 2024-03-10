package net.krlite.knowledges.api.tag.caster;

import com.google.common.primitives.Bytes;
import net.minecraft.nbt.NbtCompound;

import java.util.List;

public class NbtByteArrayCaster extends NbtCaster<List<Byte>> {
    public NbtByteArrayCaster(String identifier) {
        super(
                identifier,
                (data, key) -> Bytes.asList(data.getByteArray(key)),
                NbtCompound::putByteArray
        );
    }

    public void put(NbtCompound data, byte[] bytes) {
        super.put(data, Bytes.asList(bytes));
    }
}
