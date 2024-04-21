package band.kessokuteatime.knowledges.api.contract.caster;

import band.kessokuteatime.knowledges.api.contract.caster.base.NbtCaster;
import net.minecraft.nbt.NbtCompound;

import java.util.UUID;

public class NbtUuidCaster extends NbtCaster<UUID> {
    public NbtUuidCaster(String identifier) {
        super(
                identifier,
                NbtCompound::getUuid,
                NbtCompound::putUuid
        );
    }
}
