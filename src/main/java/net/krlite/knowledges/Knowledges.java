package net.krlite.knowledges;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.krlite.knowledges.api.KnowledgeContainer;
import net.krlite.knowledges.components.InfoComponent;
import net.krlite.knowledges.config.KnowledgesBanList;
import net.krlite.knowledges.config.KnowledgesConfig;
import net.minecraft.block.enums.Instrument;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.player.PlayerEntity;
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
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class Knowledges implements ModInitializer {
	public static final String NAME = "Knowledges", ID = "knowledges";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	public static final KnowledgesConfig CONFIG = new KnowledgesConfig();
	private static final KnowledgesBanList banList = new KnowledgesBanList();
	private static final ArrayList<Knowledge> knowledges = new ArrayList<>();
	private static int knowledgesCount = 0;

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
		return localize("instrument", instrument.name());
	}

	public static int knowledgesCount() {
		return knowledgesCount;
	}

	public static ArrayList<Knowledge> knowledges() {
		return new ArrayList<>(knowledges);
	}

	public static String localizationKey(String category, String... paths) {
		return category + "." + ID + "." + String.join(".", paths);
	}

	public static MutableText localize(String key) {
		return Text.translatable(key);
	}

	public static MutableText localize(String category, String... paths) {
		return localize(localizationKey(category, paths));
	}

	private static void register(Knowledge knowledge) {
		knowledges.add(knowledge);
	}

	public static boolean knowledgeState(Knowledge knowledge) {
		return !banList.isBanned(knowledge.name());
	}

	public static void knowledgeState(Knowledge knowledge, boolean state) {
		banList.setBanned(knowledge.name(), !state);
	}

	@Override
	public void onInitialize() {
		InfoComponent.Animations.registerEvents();

		LOGGER.info("Initializing components for " + NAME + "...");

		FabricLoader.getInstance().getEntrypointContainers("knowledges", KnowledgeContainer.class).forEach(entrypoint -> {
			KnowledgeContainer container = entrypoint.getEntrypoint();
			container.register().forEach(Knowledges::register);

			knowledgesCount += container.register().size();
		});

		LOGGER.info("Successfully registered " + knowledgesCount + " knowledge. You're now full of knowledge! 📚");
	}

	public static void render(
			@NotNull DrawContext context, @NotNull MinecraftClient client,
			@NotNull PlayerEntity player, @NotNull ClientWorld world
	) {
		knowledges.forEach(knowledge -> {
			if (!banList.isBanned(knowledge.name()))
				knowledge.render(context, client, player, world);
		});
	}

	public static double mapToPower(double x, double power, double threshold) {
		return threshold + (1 - threshold) * Math.pow(x, power);
	}
}
