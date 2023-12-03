package net.krlite.knowledges.components.info;

import net.fabricmc.fabric.api.mininglevel.v1.MiningLevelManager;
import net.krlite.equator.visual.color.Palette;
import net.krlite.equator.visual.color.base.ColorStandard;
import net.krlite.knowledges.Knowledges;
import net.krlite.knowledges.components.InfoComponent;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class BlockInfoComponent extends InfoComponent {
	@Override
	public void render(@NotNull DrawContext context, @NotNull MinecraftClient client, @NotNull PlayerEntity player, @NotNull ClientWorld world) {
		super.render(context, client, player, world);

		Info.crosshairBlockState().ifPresent(blockState -> {
			MutableText blockName = blockState.getBlock().getName();

			float hardness = Info.crosshairBlockPos()
					.map(blockPos -> blockState.getHardness(world, blockPos))
					.orElse(0F);
			boolean harvestable = hardness >= 0 && player.canHarvest(blockState);

			String
					namespace = Util.getNamespace(blockState.getBlock().asItem().getDefaultStack()),
					path = Registries.BLOCK.getId(blockState.getBlock()).getPath();
			ItemStack itemStack = player.getMainHandStack();

			Animations.Ring.ovalColor(harvestable ? Palette.Minecraft.WHITE : Palette.Minecraft.RED);

			Animations.Ring.ringRadians(Math.PI * 2 * Animations.Ring.blockBreakingProgress());
			Animations.Ring.ringColor(Palette.Minecraft.YELLOW.mix(Palette.Minecraft.GREEN, Animations.Ring.blockBreakingProgress(), ColorStandard.MixMode.PIGMENT));

			// Titles
			titles: {
				Animations.Texts.titleRight(blockName);
				Animations.Texts.titleLeft(Util.getModName(namespace));
			}

			// Right Above
			if (client.options.advancedItemTooltips) subtitleRightAbove: {
				Animations.Texts.subtitleRightAbove(Text.literal(path));
			} else {
				Animations.Texts.subtitleRightAbove(Text.empty());
			}

			// Right Below
			subtitleRightBelow: {
				MutableText tool, miningLevel = null;
				boolean foundSemanticMiningLevel = false;

				tool: {
					if (hardness < 0) {
						// Unbreakable
						tool = localize("tool", "unbreakable");
					} else if (blockState.isIn(BlockTags.PICKAXE_MINEABLE)) {
						tool = localize("tool", "pickaxe");
					} else if (blockState.isIn(BlockTags.AXE_MINEABLE)) {
						tool = localize("tool", "axe");
					} else if (blockState.isIn(BlockTags.HOE_MINEABLE)) {
						tool = localize("tool", "hoe");
					} else if (blockState.isIn(BlockTags.SHOVEL_MINEABLE)) {
						tool = localize("tool", "shovel");
					} else {
						Animations.Texts.subtitleRightBelow(Text.empty());
						break subtitleRightBelow;
					}
				}

				miningLevel: {
					int requiredLevel = MiningLevelManager.getRequiredMiningLevel(blockState);
					if (requiredLevel <= 0) break miningLevel;

					String localizationKey = localizationKey("mining_level");
					String localizationKeyWithLevel = localizationKey("mining_level", String.valueOf(requiredLevel));
					MutableText localizationWithLevel = Knowledges.localize(localizationKeyWithLevel);

					foundSemanticMiningLevel = !localizationWithLevel.getString().equals(localizationKeyWithLevel);
					miningLevel = foundSemanticMiningLevel ? localizationWithLevel : Text.translatable(localizationKey, requiredLevel);
				}

				if (miningLevel == null) {
					Animations.Texts.subtitleRightBelow(tool);
				} else {
					Animations.Texts.subtitleRightBelow(Text.translatable(
							foundSemanticMiningLevel ? localizationKey("harvest_and_mining_level_semantic") : localizationKey("harvest_and_mining_level"),
							tool.getString(), miningLevel.getString()
					));
				}
			}

			// Left Above
			if (client.options.advancedItemTooltips) subtitleLeftAbove: {
				Animations.Texts.subtitleLeftAbove(Text.literal(namespace));
			} else {
				Animations.Texts.subtitleLeftAbove(Text.empty());
			}

			// Left Below
			subtitleLeftBelow: {
				if (itemStack.isOf(Items.NOTE_BLOCK)) {
					Animations.Texts.subtitleLeftBelow(Util.getInstrumentName(blockState.getInstrument()));
					break subtitleLeftBelow;
				}

				if (blockState.isIn(BlockTags.BANNERS) && Info.crosshairBlockEntity().isPresent()) {
					var patterns = ((BannerBlockEntity) Info.crosshairBlockEntity().get()).getPatterns();

					if (patterns.size() > 1) {
						// The first pattern is always the background color, so ignore it

						patterns.get(1).getFirst().getKey()
								.map(RegistryKey::getValue)
								.map(Identifier::toShortTranslationKey)
								.ifPresent(translationKey -> {
									Text pattern = Text.translatable("block.minecraft.banner." + translationKey + "." + patterns.get(1).getSecond().getName());
									if (patterns.size() > 2) {
										Animations.Texts.subtitleLeftBelow(Text.translatable(
												localizationKey("banner", "patterns"),
												pattern.getString(), patterns.size() - 2
												// Should be something like *<pattern> and <number> more*, so the argument
												// is subtracted by 2.
										));
									} else {
										Animations.Texts.subtitleLeftBelow(pattern);
									}
								});

						break subtitleLeftBelow;
					}
				}

				Animations.Texts.subtitleLeftBelow(Text.empty());
			}
		});
	}

	@Override
	public @NotNull String infoId() {
		return "block";
	}

	@Override
	public boolean provideTooltip() {
		return true;
	}
}
