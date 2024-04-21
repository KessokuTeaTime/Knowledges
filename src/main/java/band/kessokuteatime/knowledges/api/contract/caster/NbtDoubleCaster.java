package band.kessokuteatime.knowledges.api.contract.caster;

import band.kessokuteatime.knowledges.api.contract.caster.base.NbtCaster;
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
