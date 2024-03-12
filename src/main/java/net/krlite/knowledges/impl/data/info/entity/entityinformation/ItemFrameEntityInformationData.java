package net.krlite.knowledges.impl.data.info.entity.entityinformation;

import net.krlite.knowledges.api.representable.EntityRepresentable;
import net.krlite.knowledges.impl.data.info.base.entity.EntityInformationData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ItemFrameEntityInformationData extends EntityInformationData {
    @Override
    public Optional<MutableText> entityInformation(EntityRepresentable representable) {
        if (representable.entity().getType() == EntityType.ITEM_FRAME || representable.entity().getType() == EntityType.GLOW_ITEM_FRAME) {
            if (!(representable.entity() instanceof ItemFrameEntity itemFrameEntity)) return Optional.empty();
            ItemStack heldItemStack = itemFrameEntity.getHeldItemStack();

            if (!heldItemStack.isEmpty()) return Optional.of((MutableText) heldItemStack.getItem().getName());
        }

        return Optional.empty();
    }

    @Override
    public @NotNull String partialPath() {
        return Registries.ITEM.getId(Items.ITEM_FRAME).getPath();
    }

    @Override
    public boolean providesTooltip() {
        return true;
    }
}
