package band.kessokuteatime.knowledges.impl.data.info.entity.entityinformation;

import band.kessokuteatime.knowledges.api.representable.EntityRepresentable;
import band.kessokuteatime.knowledges.impl.data.info.base.entity.EntityInformationData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class VillagerData extends EntityInformationData {
    @Override
    public Optional<MutableText> entityInformation(EntityRepresentable representable) {
        if (representable.entity().getType() == EntityType.VILLAGER) {
            if (!(representable.entity() instanceof VillagerEntity villagerEntity)) return Optional.empty();
            net.minecraft.village.VillagerData villagerData = villagerEntity.getVillagerData();

            return Optional.of(Text.translatable(
                    localizationKey("profession_and_level"),
                    Text.translatable("entity.minecraft.villager." + villagerData.getProfession().id()).getString(),
                    villagerData.getLevel()
            ));
        }

        return Optional.empty();
    }

    @Override
    public @NotNull String partialPath() {
        return Registries.ENTITY_TYPE.getId(EntityType.VILLAGER).getPath();
    }

    @Override
    public boolean providesTooltip() {
        return true;
    }
}
