package band.kessokuteatime.knowledges.api.contract.caster;

import band.kessokuteatime.knowledges.api.contract.caster.base.NbtCaster;
import net.minecraft.nbt.NbtCompound;

public class NbtBooleanCaster extends NbtCaster<Boolean> {
    public NbtBooleanCaster(String identifier) {
        super(
                identifier,
                NbtCompound::getBoolean,
                NbtCompound::putBoolean
        );
    }
}
