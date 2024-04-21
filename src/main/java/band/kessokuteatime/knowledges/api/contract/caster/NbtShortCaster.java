package band.kessokuteatime.knowledges.api.contract.caster;

import band.kessokuteatime.knowledges.api.contract.caster.base.NbtCaster;
import net.minecraft.nbt.NbtCompound;

public class NbtShortCaster extends NbtCaster<Short> {
    public NbtShortCaster(String identifier) {
        super(
                identifier,
                NbtCompound::getShort,
                NbtCompound::putShort
        );
    }
}
