package net.krlite.knowledges.api.proxy;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.krlite.knowledges.KnowledgesClient;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.Instrument;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class KnowledgeProxy {
    public static Identifier getId(Block block) {
        return Registries.BLOCK.getId(block);
    }

    public static Identifier getId(BlockEntityType<?> blockEntityType) {
        return Registries.BLOCK_ENTITY_TYPE.getId(blockEntityType);
    }

    public static Identifier getId(PaintingVariant paintingVariant) {
        return Registries.PAINTING_VARIANT.getId(paintingVariant);
    }

    public static MutableText getModName(String namespace) {
        return Text.literal(FabricLoader.getInstance().getModContainer(namespace)
                .map(ModContainer::getMetadata)
                .map(ModMetadata::getName)
                .orElse(""));
    }

    public static String getNamespace(ItemStack itemStack) {
        String namespace = Registries.ITEM.getId(itemStack.getItem()).getNamespace();

        // Enchanted Book
        if (itemStack.getItem() instanceof EnchantedBookItem) {
            NbtList nbtList = EnchantedBookItem.getEnchantmentNbt(itemStack);
            String enchantmentNamespace = null;

            for (int i = 0; i < nbtList.size(); i++) {
                Identifier id = Identifier.tryParse(nbtList.getCompound(i).getString("id"));
                if (id != null) {
                    if (enchantmentNamespace == null) {
                        enchantmentNamespace = id.getNamespace();
                    } else if (!enchantmentNamespace.equals(id.getNamespace())) {
                        enchantmentNamespace = null;
                        break;
                    }
                }
            }

            if (enchantmentNamespace != null) {
                namespace = enchantmentNamespace;
            }
        }

        // Potion and Tipped Arrow
        if (itemStack.getItem() instanceof PotionItem || itemStack.getItem() instanceof TippedArrowItem) {
            NbtCompound nbtCompound = itemStack.getNbt();
            Potion potion = nbtCompound == null ? Potions.EMPTY : Potion.byId(nbtCompound.getString("Potion"));
            namespace = Registries.POTION.getId(potion).getNamespace();
        }

        // Painting
        if (itemStack.isOf(Items.PAINTING)) {
            NbtCompound nbtCompound = itemStack.getNbt();
            String paintingNamespace = null;

            if (nbtCompound != null && nbtCompound.contains("EntityTag", 10)) {
                NbtCompound entityTagCompound = nbtCompound.getCompound("EntityTag");

                paintingNamespace = PaintingEntity.readVariantFromNbt(entityTagCompound)
                        .flatMap(RegistryEntry::getKey)
                        .map(RegistryKey::getRegistry)
                        .map(Identifier::getNamespace)
                        .orElse(null);
            }

            if (paintingNamespace != null) {
                namespace = paintingNamespace;
            }
        }

        return namespace;
    }

    public static MutableText getInstrumentName(Instrument instrument) {
        return KnowledgesClient.localize("instrument", instrument.asString());
    }

    public static MutableText getDateAndTime() {
        if (MinecraftClient.getInstance().world == null) return Text.empty();

        long time = MinecraftClient.getInstance().world.getTimeOfDay() + 24000 / 4; // Offset to morning
        long day = time / 24000;
        double percentage = (time % 24000) / 24000.0;

        int hour = (int) (24 * percentage), minute = (int) (60 * ((percentage * 24) % 1));

        return Text.translatable(
                KnowledgesClient.localizationKey("util", "date_and_time"),
                String.valueOf(day), String.format("%02d", hour), String.format("%02d", minute)
        );
    }
}
