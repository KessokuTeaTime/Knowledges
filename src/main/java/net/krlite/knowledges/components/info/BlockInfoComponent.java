package net.krlite.knowledges.components.info;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.mininglevel.v1.MiningLevelManager;
import net.krlite.equator.visual.color.Palette;
import net.krlite.equator.visual.color.base.ColorStandard;
import net.krlite.knowledges.Knowledges;
import net.krlite.knowledges.components.InfoComponent;
import net.krlite.knowledges.core.DataEvent;
import net.krlite.knowledges.core.Target;
import net.minecraft.block.BlockState;
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

import java.util.Optional;

public class BlockInfoComponent extends InfoComponent<BlockInfoComponent.BlockInfoTarget> {
	public enum BlockInfoTarget implements Target {
		MINEABLE_TOOL {
			@Override
			public <E extends DataEvent<?>> Event<E> event() {
				return (Event<E>) MineableToolEvent.EVENT;
			}
		};

		public interface MineableToolEvent extends DataEvent<BlockInfoTarget> {
			Event<MineableToolEvent> EVENT = EventFactory.createArrayBacked(
					MineableToolEvent.class,
					listeners -> level -> {
						for (MineableToolEvent listener : listeners) {
							Optional<MutableText> tool = listener.mineableTool(level);
							if (tool.isPresent()) return tool;
						}

						return Optional.empty();
					}
			);

			Optional<MutableText> mineableTool(BlockState blockState);

			@Override
			default BlockInfoTarget target() {
				return MINEABLE_TOOL;
			}
		}
	}

	@Override
	public Class<BlockInfoTarget> targets() {
		return BlockInfoTarget.class;
	}

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
					Optional<MutableText> data = BlockInfoTarget.MineableToolEvent.EVENT.invoker().mineableTool(blockState);

					if (hardness < 0) {
						// Unbreakable
						tool = localize("tool", "unbreakable");
					} else if (data.isPresent()) {
						tool = data.get();
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
					Animations.Texts.subtitleRightBelow(Text.translatable(localizationKey("tool"), tool));
				} else {
					Animations.Texts.subtitleRightBelow(Text.translatable(
							foundSemanticMiningLevel ? localizationKey("tool_and_mining_level_semantic") : localizationKey("tool_and_mining_level"),
							tool, miningLevel
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
					if (!(Info.crosshairBlockEntity().get() instanceof BannerBlockEntity bannerBlockEntity)) break subtitleLeftBelow;
					var patterns = bannerBlockEntity.getPatterns();
					int available = patterns.size() - 1;
					// The first pattern is always the background color, so ignore it

					if (available > 0) {
						patterns.get(1).getFirst().getKey()
								.map(RegistryKey::getValue)
								.map(Identifier::toShortTranslationKey)
								.ifPresent(translationKey -> {
									Text name = Text.translatable(
											localizationKey("banner", "pattern"),
											Text.translatable("block.minecraft.banner." + translationKey + "." + patterns.get(1).getSecond().getName()).getString()
									);

									if (available > 2) {
										Animations.Texts.subtitleLeftBelow(Text.translatable(
												localizationKey("banner", "more_patterns"),
												name.getString(),
												available - 1,
												// Counts the rest of the patterns. Use '%2$d' to reference.
												available
												// Counts all the patterns. Use '%3$d' to reference.
										));
									} else if (available > 1) {
										Animations.Texts.subtitleLeftBelow(Text.translatable(
												localizationKey("banner", "one_more_pattern"),
												name.getString()
										));
									}else {
										Animations.Texts.subtitleLeftBelow(name);
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
	public @NotNull String partialPath() {
		return "block";
	}

	@Override
	public boolean providesTooltip() {
		return true;
	}
}
