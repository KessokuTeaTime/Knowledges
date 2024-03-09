package net.krlite.knowledges.impl.data.info.entity.entityinformation;

import net.krlite.knowledges.api.representable.EntityRepresentable;
import net.krlite.knowledges.impl.data.info.entity.AbstractEntityInformationData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.village.VillagerData;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class VillagerEntityInformationData extends AbstractEntityInformationData {
    @Override
    public Optional<MutableText> entityInformation(EntityRepresentable representable) {
        if (representable.entity().getType() == EntityType.VILLAGER) {
            if (!(representable.entity() instanceof VillagerEntity villagerEntity)) return Optional.empty();
            VillagerData villagerData = villagerEntity.getVillagerData();

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
