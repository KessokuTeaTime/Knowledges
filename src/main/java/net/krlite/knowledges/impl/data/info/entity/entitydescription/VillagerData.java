package net.krlite.knowledges.impl.data.info.entity.entitydescription;

import net.krlite.knowledges.api.representable.EntityRepresentable;
import net.krlite.knowledges.impl.data.info.base.entity.EntityDescriptionData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class VillagerData extends EntityDescriptionData {
    @Override
    public Optional<MutableText> entityDescription(EntityRepresentable representable) {
        if (representable.entity().getType() == EntityType.VILLAGER) {
            if (!(representable.entity() instanceof VillagerEntity villagerEntity)) return Optional.empty();

            return Optional.of(Text.translatable(
                    localizationKey("reputation"),
                    villagerEntity.getReputation(representable.player())
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
