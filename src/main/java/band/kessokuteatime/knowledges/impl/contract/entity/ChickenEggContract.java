package band.kessokuteatime.knowledges.impl.contract.entity;

import band.kessokuteatime.knowledges.api.contract.EntityContract;
import band.kessokuteatime.knowledges.api.representable.EntityRepresentable;
import band.kessokuteatime.knowledges.api.contract.caster.NbtIntCaster;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.NotNull;

public class ChickenEggContract implements EntityContract {
    public static final NbtIntCaster NEXT_EGG = new NbtIntCaster("NextEgg");

    @Override
    public boolean isApplicableTo(Entity entity) {
        return entity instanceof ChickenEntity;
    }

    @Override
    public void append(NbtCompound data, EntityRepresentable representable) {
        if (representable.entity() instanceof ChickenEntity chickenEntity) {
            NEXT_EGG.put(data, chickenEntity.eggLayTime);
        }
    }

    @Override
    public @NotNull String partialPath() {
        return "chicken_egg";
    }
}
