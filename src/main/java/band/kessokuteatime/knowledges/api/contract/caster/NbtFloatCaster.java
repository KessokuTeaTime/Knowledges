package band.kessokuteatime.knowledges.api.contract.caster;

import band.kessokuteatime.knowledges.api.contract.caster.base.NbtCaster;
import net.minecraft.nbt.NbtCompound;

public class NbtFloatCaster extends NbtCaster<Float> {
    public NbtFloatCaster(String identifier) {
        super(
                identifier,
                NbtCompound::getFloat,
                NbtCompound::putFloat
        );
    }
}
