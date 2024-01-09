package net.krlite.knowledges.data.info.entity;

import net.krlite.knowledges.components.info.EntityInfoComponent;
import net.krlite.knowledges.data.info.AbstractEntityInfoComponentData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.MutableText;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public abstract class AbstractEntityDescriptionData extends AbstractEntityInfoComponentData<EntityInfoComponent.EntityDescriptionCallback> {
    public abstract Optional<MutableText> fetchInfo(Entity entity, PlayerEntity player);

    @Override
    public EntityInfoComponent.EntityDescriptionCallback callback() {
        return (entity, player) -> shouldProvideNothing() ? Optional.empty() : fetchInfo(entity, player);
    }

    @Override
    public @NotNull String currentPath() {
        return super.currentPath() + ".entity_description";
    }
}
