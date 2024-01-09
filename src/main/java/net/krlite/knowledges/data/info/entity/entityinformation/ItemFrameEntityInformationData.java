package net.krlite.knowledges.data.info.entity.entityinformation;

import net.krlite.knowledges.data.info.entity.AbstractEntityInformationData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ItemFrameEntityInformationData extends AbstractEntityInformationData {
    @Override
    public Optional<MutableText> fetchInfo(Entity entity, PlayerEntity player) {
        if (entity.getType() == EntityType.ITEM_FRAME || entity.getType() == EntityType.GLOW_ITEM_FRAME) {
            if (!(entity instanceof ItemFrameEntity itemFrameEntity)) return Optional.empty();
            ItemStack heldItemStack = itemFrameEntity.getHeldItemStack();

            if (!heldItemStack.isEmpty()) return Optional.of((MutableText) heldItemStack.getItem().getName());
        }

        return Optional.empty();
    }

    @Override
    public @NotNull String partialPath() {
        return Registries.ITEM.getId(Items.ITEM_FRAME).getPath();
    }
}
