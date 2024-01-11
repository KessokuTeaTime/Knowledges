package net.krlite.knowledges.data.info.entity.entitydescription;

import net.krlite.knowledges.data.info.entity.AbstractEntityDescriptionData;
import net.krlite.knowledges.mixin.common.ItemStackInvoker;
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
    public Optional<MutableText> entityDescription(Entity entity, PlayerEntity player) {
        if (entity.getType() == EntityType.ITEM_FRAME || entity.getType() == EntityType.GLOW_ITEM_FRAME) {
            if (!(entity instanceof ItemFrameEntity itemFrameEntity)) return Optional.empty();
            ItemStack heldItemStack = itemFrameEntity.getHeldItemStack();

            if (!heldItemStack.isEmpty()) {
                MutableText durability = null, description = null;

                // Durability
                if (heldItemStack.isDamageable()) {
                    if (heldItemStack.isDamaged()) {
                        int damage = heldItemStack.getDamage(), maxDamage = heldItemStack.getMaxDamage();

                        durability = Text.translatable(
                                localizationKey("durability"),
                                maxDamage - damage, maxDamage
                        );
                    }
                } else {
                    durability = localize("durability", "unbreakable");
                }

                // Description: Enchantments
                if (ItemStackInvoker.invokeIsSectionVisible(((ItemStackInvoker) heldItemStack).invokeGetHideFlags(), ItemStack.TooltipSection.ENCHANTMENTS)) {
                    NbtList enchantments = heldItemStack.getEnchantments();
                    int available = enchantments.size();

                    if (available > 0) {
                        NbtCompound firstEnchantment = enchantments.getCompound(0);

                        description = Registries.ENCHANTMENT.getOrEmpty(EnchantmentHelper.getIdFromNbt(firstEnchantment))
                                .map(enchantment -> enchantment.getName(EnchantmentHelper.getLevelFromNbt(firstEnchantment)))
                                .map(rawName -> {
                                    MutableText name = Text.translatable(
                                            localizationKey("enchantment"),
                                            rawName.getString()
                                    );

                                    if (available > 2) {
                                        return Text.translatable(
                                                localizationKey("enchantment", "more"),
                                                name.getString(),
                                                available - 1,
                                                // Counts the rest of the enchantments. Use '%2$d' to reference.
                                                available
                                                // Counts all the enchantments. Use '%3$d' to reference.
                                        );
                                    } else if (available > 1) {
                                        return Text.translatable(
                                                localizationKey("enchantment", "one_more"),
                                                name.getString()
                                        );
                                    } else {
                                        return name;
                                    }
                                }).orElse(null);
                    }
                }

                if (durability != null && description != null) {
                    return Optional.of(Text.translatable(
                            localizationKey("description_and_durability"),
                            description, durability
                    ));
                } else {
                    if (durability != null) return Optional.of(durability);
                    if (description != null) return Optional.of(description);
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public boolean providesTooltip() {
        return true;
    }

    @Override
    public @NotNull String partialPath() {
        return Registries.ITEM.getId(Items.ITEM_FRAME).getPath();
    }
}
