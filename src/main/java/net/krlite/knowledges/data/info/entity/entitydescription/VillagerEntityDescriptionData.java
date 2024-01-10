package net.krlite.knowledges.data.info.entity.entitydescription;

import net.krlite.knowledges.data.info.entity.AbstractEntityDescriptionData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class VillagerEntityDescriptionData extends AbstractEntityDescriptionData {
    @Override
    public Optional<MutableText> entityDescription(Entity entity, PlayerEntity player) {
        if (entity.getType() == EntityType.VILLAGER) {
            if (!(entity instanceof VillagerEntity villagerEntity)) return Optional.empty();

            return Optional.of(Text.translatable(
                    localizationKey("reputation"),
                    villagerEntity.getReputation(player)
            ));
        }

        return Optional.empty();
    }

    @Override
    public @NotNull String partialPath() {
        return Registries.ENTITY_TYPE.getId(EntityType.VILLAGER).getPath();
    }
}
