package band.kessokuteatime.knowledges.api.contract.caster;

import band.kessokuteatime.knowledges.api.contract.caster.base.NbtCaster;
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
