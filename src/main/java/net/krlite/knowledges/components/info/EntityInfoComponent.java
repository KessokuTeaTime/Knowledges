package net.krlite.knowledges.components.info;

import net.krlite.equator.visual.color.Palette;
import net.krlite.knowledges.components.InfoComponent;
import net.krlite.knowledges.core.Target;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.village.VillagerData;
import org.jetbrains.annotations.NotNull;

public class EntityInfoComponent extends InfoComponent<EntityInfoComponent.EntityInfoTarget> {
	public enum EntityInfoTarget implements Target {
		TEST;
	}

	@Override
	public Class<EntityInfoTarget> targets() {
		return EntityInfoTarget.class;
	}

	@Override
	public void render(@NotNull DrawContext context, @NotNull MinecraftClient client, @NotNull PlayerEntity player, @NotNull ClientWorld world) {
		super.render(context, client, player, world);

		Info.crosshairEntity().ifPresent(entity -> {
			MutableText entityName = entity.getDisplayName().copy();

			String
					namespace = Registries.ENTITY_TYPE.getId(entity.getType()).getNamespace(),
					path = Registries.ENTITY_TYPE.getId(entity.getType()).getPath();

			// Titles
			titles: {
				Animations.Texts.titleRight(entityName);
				Animations.Texts.titleLeft(Util.getModName(Registries.ENTITY_TYPE.getId(entity.getType()).getNamespace()));
			}

			switch (entity.getType().getSpawnGroup()) {
				case MONSTER -> {
					Animations.Ring.ringColor(Palette.Minecraft.RED);
					Animations.Ring.ovalColor(Palette.Minecraft.RED);
				}
				case WATER_CREATURE, UNDERGROUND_WATER_CREATURE -> {
					Animations.Ring.ringColor(Palette.Minecraft.AQUA);
					Animations.Ring.ovalColor(Palette.Minecraft.WHITE);
				}
				case WATER_AMBIENT -> {
					Animations.Ring.ringColor(Palette.Minecraft.BLUE);
					Animations.Ring.ovalColor(Palette.Minecraft.BLUE);
				}
				case MISC -> {
					Animations.Ring.ringColor(Palette.Minecraft.WHITE);
					Animations.Ring.ovalColor(Palette.Minecraft.WHITE);
				}
				case AMBIENT -> {
					Animations.Ring.ringColor(Palette.Minecraft.YELLOW);
					Animations.Ring.ovalColor(Palette.Minecraft.WHITE);
				}
				default -> {
					Animations.Ring.ringColor(Palette.Minecraft.GREEN);
					Animations.Ring.ovalColor(Palette.Minecraft.WHITE);
				}
			}

			if (entity.isInvulnerable()) {
				Animations.Ring.ovalColor(Palette.Minecraft.LIGHT_PURPLE);
			}

			if (!entity.isInvulnerable() && entity instanceof LivingEntity livingEntity) {
				double health = livingEntity.getHealth() / livingEntity.getMaxHealth();

				Animations.Ring.ringRadians(Math.PI * 2 * health);
			}

			// Right Above
			if (client.options.advancedItemTooltips) subtitleRightAbove: {
				Animations.Texts.subtitleRightAbove(Text.literal(path));
			} else {
				Animations.Texts.subtitleRightAbove(Text.empty());
			}

			// Right Below
			subtitleRightBelow: {
				if (entity.getType() == EntityType.PAINTING) {
					((PaintingEntity) entity).getVariant().getKey().ifPresent((key) ->
							Animations.Texts.subtitleRightBelow(Text.translatable(
									key.getValue().toTranslationKey("painting", "title")
							)));

					break subtitleRightBelow;
				}

				if (entity.getType() == EntityType.ITEM_FRAME || entity.getType() == EntityType.GLOW_ITEM_FRAME) {
					if (!(entity instanceof ItemFrameEntity itemFrameEntity)) break subtitleRightBelow;
					ItemStack heldItemStack = itemFrameEntity.getHeldItemStack();

					if (!heldItemStack.isEmpty()) {
						Animations.Texts.subtitleRightBelow(heldItemStack.getItem().getName());

						break subtitleRightBelow;
					}
				}

				if (entity.getType() == EntityType.VILLAGER) {
					if (!(entity instanceof VillagerEntity villagerEntity)) break subtitleRightBelow;
					VillagerData villagerData = villagerEntity.getVillagerData();

					Animations.Texts.subtitleRightBelow(Text.translatable(
							localizationKey("villager", "profession_and_level"),
							Text.translatable("entity.minecraft.villager." + villagerData.getProfession().id()).getString(),
							villagerData.getLevel()
					));

					break subtitleRightBelow;
				}

				Animations.Texts.subtitleRightBelow(Text.empty());
			}

			// Left Above
			if (client.options.advancedItemTooltips) subtitleLeftAbove: {
				Animations.Texts.subtitleLeftAbove(Text.literal(namespace));
			} else {
				Animations.Texts.subtitleLeftAbove(Text.empty());
			}

			// Left Below
			subtitleLeftBelow: {
				if (entity.getType() == EntityType.ITEM_FRAME || entity.getType() == EntityType.GLOW_ITEM_FRAME) {
					if (!(entity instanceof ItemFrameEntity itemFrameEntity)) break subtitleLeftBelow;
					ItemStack heldItemStack = itemFrameEntity.getHeldItemStack();

					if (!heldItemStack.isEmpty()) {
						// Enchanted Books
						if (heldItemStack.getItem() instanceof EnchantedBookItem) {
							NbtList enchantments = EnchantedBookItem.getEnchantmentNbt(heldItemStack);
							int available = enchantments.size();

							if (available > 0) {
								NbtCompound firstEnchantment = enchantments.getCompound(0);

								Registries.ENCHANTMENT.getOrEmpty(EnchantmentHelper.getIdFromNbt(firstEnchantment))
										.map(enchantment -> enchantment.getName(EnchantmentHelper.getLevelFromNbt(firstEnchantment)))
										.ifPresent(rawName -> {
											Text name = Text.translatable(
													localizationKey("enchanted_book", "enchantment"),
													rawName.getString()
											);

											if (available > 2) {
												Animations.Texts.subtitleLeftBelow(Text.translatable(
														localizationKey("enchanted_book", "more_enchantments"),
														name.getString(),
														available - 1,
														// Counts the rest of the enchantments. Use '%2$d' to reference.
														available
														// Counts all the enchantments. Use '%3$d' to reference.
												));
											} else if (available > 1) {
												Animations.Texts.subtitleLeftBelow(Text.translatable(
														localizationKey("enchanted_book", "one_more_enchantment"),
														name.getString()
												));
											}else {
												Animations.Texts.subtitleLeftBelow(name);
											}
										});

								break subtitleLeftBelow;
							}
						}

						break subtitleLeftBelow;
					}
				}

				if (entity.getType() == EntityType.VILLAGER) {
					if (!(entity instanceof VillagerEntity villagerEntity)) break subtitleLeftBelow;

					Animations.Texts.subtitleLeftBelow(Text.translatable(
							localizationKey("villager", "reputation"),
							villagerEntity.getReputation(player)
					));

					break subtitleLeftBelow;
				}

				Animations.Texts.subtitleLeftBelow(Text.empty());
			}
		});
	}

	@Override
	public @NotNull String partialPath() {
		return "entity";
	}

	@Override
	public boolean providesTooltip() {
		return true;
	}
}
