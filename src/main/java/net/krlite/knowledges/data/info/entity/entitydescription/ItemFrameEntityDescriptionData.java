package net.krlite.knowledges.data.info.entity.entitydescription;

import net.krlite.knowledges.data.info.entity.AbstractEntityDescriptionData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ItemFrameEntityDescriptionData extends AbstractEntityDescriptionData {
    @Override
    public Optional<MutableText> fetchInfo(Entity entity, PlayerEntity player) {
        if (entity.getType() == EntityType.ITEM_FRAME || entity.getType() == EntityType.GLOW_ITEM_FRAME) {
            if (!(entity instanceof ItemFrameEntity itemFrameEntity)) return Optional.empty();
            ItemStack heldItemStack = itemFrameEntity.getHeldItemStack();

            if (!heldItemStack.isEmpty()) {
                // Enchanted Books
                if (heldItemStack.getItem() instanceof EnchantedBookItem) {
                    NbtList enchantments = EnchantedBookItem.getEnchantmentNbt(heldItemStack);
                    int available = enchantments.size();

                    if (available > 0) {
                        NbtCompound firstEnchantment = enchantments.getCompound(0);

                        return Registries.ENCHANTMENT.getOrEmpty(EnchantmentHelper.getIdFromNbt(firstEnchantment))
                                .map(enchantment -> enchantment.getName(EnchantmentHelper.getLevelFromNbt(firstEnchantment)))
                                .map(rawName -> {
                                    MutableText name = Text.translatable(
                                            localizationKey("enchanted_book", "enchantment"),
                                            rawName.getString()
                                    );

                                    if (available > 2) {
                                        return Text.translatable(
                                                localizationKey("enchanted_book", "more_enchantments"),
                                                name.getString(),
                                                available - 1,
                                                // Counts the rest of the enchantments. Use '%2$d' to reference.
                                                available
                                                // Counts all the enchantments. Use '%3$d' to reference.
                                        );
                                    } else if (available > 1) {
                                        return Text.translatable(
                                                localizationKey("enchanted_book", "one_more_enchantment"),
                                                name.getString()
                                        );
                                    } else {
                                        return name;
                                    }
                                });
                    }

                    return Optional.empty();
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public @NotNull String partialPath() {
        return Registries.ITEM.getId(Items.ITEM_FRAME).getPath();
    }
}
