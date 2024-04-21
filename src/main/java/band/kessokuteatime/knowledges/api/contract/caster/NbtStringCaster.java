package band.kessokuteatime.knowledges.api.contract.caster;

import band.kessokuteatime.knowledges.api.contract.caster.base.NbtCaster;
import net.minecraft.nbt.NbtCompound;

public class NbtStringCaster extends NbtCaster<String> {
    public NbtStringCaster(String identifier) {
        super(
                identifier,
                NbtCompound::getString,
                NbtCompound::putString
        );
    }
}
