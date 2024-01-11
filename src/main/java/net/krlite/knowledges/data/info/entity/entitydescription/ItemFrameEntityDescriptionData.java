package net.krlite.knowledges.data.info.entity.entitydescription;

import net.krlite.knowledges.api.Knowledge;
import net.krlite.knowledges.data.info.entity.AbstractEntityDescriptionData;
import net.krlite.knowledges.mixin.common.ItemStackInvoker;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class ItemFrameEntityDescriptionData extends AbstractEntityDescriptionData {
    @Override
    public Optional<MutableText> entityDescription(Entity entity, PlayerEntity player) {
        if (entity.getType() == EntityType.ITEM_FRAME || entity.getType() == EntityType.GLOW_ITEM_FRAME) {
            if (!(entity instanceof ItemFrameEntity itemFrameEntity)) return Optional.empty();
            ItemStack heldItemStack = itemFrameEntity.getHeldItemStack();

            if (!heldItemStack.isEmpty()) {
                MutableText durability = null, enchantments = null, description = null;

                // Durability
                if (heldItemStack.isDamageable()) {
                    if (heldItemStack.isDamaged()) {
                        int damage = heldItemStack.getDamage(), maxDamage = heldItemStack.getMaxDamage();

                        durability = Text.translatable(
                                localizationKey("durability"),
                                maxDamage - damage, maxDamage
                        );
                    }
                }

                // Enchantments
                if (ItemStackInvoker.invokeIsSectionVisible(heldItemStack.getHideFlags(), ItemStack.TooltipSection.ENCHANTMENTS)) {
                    NbtList enchantmentNbtList = heldItemStack.getEnchantments();
                    int available = enchantmentNbtList.size();

                    if (available > 0) {
                        NbtCompound firstEnchantment = enchantmentNbtList.getCompound(0);

                        enchantments = Registries.ENCHANTMENT.getOrEmpty(EnchantmentHelper.getIdFromNbt(firstEnchantment))
                                .map(enchantmentNbt -> enchantmentNbt.getName(EnchantmentHelper.getLevelFromNbt(firstEnchantment)))
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

                // Description
                if (heldItemStack.isOf(Items.CLOCK)) {
                    description = Knowledge.Util.dateAndTime();
                }

                return Stream.of(durability, enchantments, description)
                        .filter(Objects::nonNull)
                        .reduce((p, n) -> p.append("\n").append(n));
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
