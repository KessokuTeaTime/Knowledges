package band.kessokuteatime.knowledges.impl.data.info.entity.entitydescription;

import band.kessokuteatime.knowledges.Util;
import band.kessokuteatime.knowledges.api.proxy.LocalizationProxy;
import band.kessokuteatime.knowledges.api.representable.EntityRepresentable;
import band.kessokuteatime.knowledges.impl.data.info.base.entity.EntityDescriptionData;
import band.kessokuteatime.knowledges.mixin.common.ItemStackInvoker;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ItemFrameData extends EntityDescriptionData {
    @Override
    public Optional<MutableText> entityDescription(EntityRepresentable representable) {
        if (representable.entity().getType() == EntityType.ITEM_FRAME || representable.entity().getType() == EntityType.GLOW_ITEM_FRAME) {
            if (!(representable.entity() instanceof ItemFrameEntity itemFrameEntity)) return Optional.empty();
            ItemStack heldItemStack = itemFrameEntity.getHeldItemStack();

            if (!heldItemStack.isEmpty()) {
                MutableText durabilityText = null, enchantmentText = null, descriptionText = null;

                // Durability
                if (heldItemStack.isDamageable()) {
                    if (heldItemStack.isDamaged()) {
                        int damage = heldItemStack.getDamage(), maxDamage = heldItemStack.getMaxDamage();

                        durabilityText = Text.translatable(
                                localizationKey("durability"),
                                maxDamage - damage, maxDamage
                        );
                    }
                }

                // Enchantments
                if (ItemStackInvoker.invokeIsSectionVisible(heldItemStack.getHideFlags(), ItemStack.TooltipSection.ENCHANTMENTS)) {
                    NbtList enchantments = heldItemStack.getEnchantments();
                    int available = enchantments.size();

                    if (available > 0) {
                        NbtCompound firstEnchantment = enchantments.getCompound(0);

                        enchantmentText = Registries.ENCHANTMENT.getOrEmpty(EnchantmentHelper.getIdFromNbt(firstEnchantment))
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

                if (heldItemStack.isOf(Items.CLOCK)) {
                    descriptionText = LocalizationProxy.dateAndTime();
                }

                if (heldItemStack.getItem() instanceof MusicDiscItem musicDiscItem) {
                    descriptionText = musicDiscItem.getDescription().setStyle(Style.EMPTY);
                }

                return Util.Text.combineToMultiline(durabilityText, enchantmentText, descriptionText);
            }
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
