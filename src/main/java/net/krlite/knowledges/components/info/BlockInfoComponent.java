package net.krlite.knowledges.components.info;

import net.fabricmc.fabric.api.mininglevel.v1.MiningLevelManager;
import net.krlite.equator.visual.color.Palette;
import net.krlite.equator.visual.color.base.ColorStandard;
import net.krlite.knowledges.Knowledges;
import net.krlite.knowledges.components.InfoComponent;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockInfoComponent extends InfoComponent {
	@Override
	public void render(@NotNull DrawContext context, @NotNull MinecraftClient client, @NotNull PlayerEntity player, @NotNull ClientWorld world) {
		super.render(context, client, player, world);
		@Nullable BlockState blockState = Info.crosshairBlock();

		if (blockState != null) {
			MutableText blockName = blockState.getBlock().getName();

			float hardness = blockState.getHardness(world, Info.crosshairBlockPos());
			boolean harvestable = hardness >= 0 && player.canHarvest(blockState);

			// Titles
			titles: {
				Animations.Texts.titleRight(blockName);
				Animations.Texts.titleLeft(Knowledges.getModName(Knowledges.getNamespace(blockState.getBlock().asItem().getDefaultStack())));
			}

			Animations.Ring.ovalColor(harvestable ? Palette.Minecraft.WHITE : Palette.Minecraft.RED);

			Animations.Ring.ringRadians(Math.PI * 2 * Animations.Ring.blockBreakingProgress());
			Animations.Ring.ringColor(Palette.Minecraft.YELLOW.mix(Palette.Minecraft.GREEN, Animations.Ring.blockBreakingProgress(), ColorStandard.MixMode.PIGMENT));

			ItemStack itemStack = player.getMainHandStack();

			// Right Above
			if (client.options.advancedItemTooltips && !itemStack.isEmpty()) subtitleRightAbove: {
				if (itemStack.isOf(Items.NOTE_BLOCK)) {
					Animations.Texts.subtitleRightAbove(Knowledges.getInstrumentName(blockState.getInstrument()));
					break subtitleRightAbove;
				}

				Animations.Texts.subtitleRightAbove(Text.literal(Registries.BLOCK.getId(blockState.getBlock()).getPath()));
			} else {
				Animations.Texts.subtitleRightAbove(Text.empty());
			}

			// Right Below
			subtitleRightBelow: {
				MutableText prefix = Text.empty(), postfix = Text.empty();

				prefix: {
					if (hardness < 0) {
						// Unbreakable
						prefix = localize("harvest", "unbreakable");
						break prefix;
					}

					if (blockState.isIn(BlockTags.PICKAXE_MINEABLE)) {
						prefix = localize("harvest", "pickaxe");
						break prefix;
					}

					if (blockState.isIn(BlockTags.AXE_MINEABLE)) {
						prefix = localize("harvest", "axe");
						break prefix;
					}

					if (blockState.isIn(BlockTags.HOE_MINEABLE)) {
						prefix = localize("harvest", "hoe");
						break prefix;
					}

					if (blockState.isIn(BlockTags.SHOVEL_MINEABLE)) {
						prefix = localize("harvest", "shovel");
						break prefix;
					}
				}

				postfix: {
					int requiredLevel = MiningLevelManager.getRequiredMiningLevel(blockState);
					System.out.println(requiredLevel);
					if (requiredLevel <= 0) break postfix;

					String baseLocalizationKey = localizationKey("mining_level");
					String levelLocalizationKey = localizationKey("mining_level", String.valueOf(requiredLevel));
					MutableText localization = Knowledges.localize(levelLocalizationKey);

					postfix = localization.getString().equals(levelLocalizationKey) ? Text.translatable(baseLocalizationKey, requiredLevel) : localization;
				}

				if (prefix.equals(Text.empty()) && postfix.equals(Text.empty())) {
					Animations.Texts.subtitleLeftBelow(Text.empty());
				} else if (prefix.equals(Text.empty())) {
					Animations.Texts.subtitleRightBelow(postfix);
				} else if (postfix.equals(Text.empty())) {
					Animations.Texts.subtitleRightBelow(prefix);
				} else {
					Animations.Texts.subtitleRightBelow(Text.translatable(
							localizationKey("harvest_and_mining_level"),
							prefix.getString(), postfix.getString()
					));
				}
			}

			// Left Above
			subtitleLeftAbove: {
				Animations.Texts.subtitleLeftAbove(Text.literal(Knowledges.getNamespace(blockState.getBlock().asItem().getDefaultStack())));
			}
		}
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
