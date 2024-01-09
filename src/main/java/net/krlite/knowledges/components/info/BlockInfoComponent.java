package net.krlite.knowledges.components.info;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.mininglevel.v1.MiningLevelManager;
import net.krlite.equator.visual.color.Palette;
import net.krlite.equator.visual.color.base.ColorStandard;
import net.krlite.knowledges.Knowledges;
import net.krlite.knowledges.api.Knowledge;
import net.krlite.knowledges.components.AbstractInfoComponent;
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

import java.util.Arrays;
import java.util.Optional;

public class BlockInfoComponent extends AbstractInfoComponent<BlockInfoComponent.BlockInfoTarget> {
	public enum BlockInfoTarget implements Target {
		MINEABLE_TOOL {
			@Override
			public <E extends DataEvent<?, ?>> Event<E> event() {
				return (Event<E>) MineableToolEvent.EVENT;
			}
		},
		BLOCK_INFO {
			@Override
			public <E extends DataEvent<?, ?>> Event<E> event() {
				return (Event<E>) BlockInfoEvent.EVENT;
			}
		};

		public interface MineableToolEvent extends DataEvent<BlockInfoTarget, Optional<MutableText>> {
			Event<MineableToolEvent> EVENT = EventFactory.createArrayBacked(
					MineableToolEvent.class,
					listeners -> blockState -> Arrays.stream(listeners)
							.map(event -> event.mineableTool(blockState))
							.filter(Optional::isPresent)
							.findFirst()
							.orElse(Optional.empty())
			);

			Optional<MutableText> mineableTool(BlockState blockState);

			@Override
			default Optional<MutableText> fallback() {
				return Optional.empty();
			}

			@Override
			default BlockInfoTarget target() {
				return MINEABLE_TOOL;
			}
		}

		public interface BlockInfoEvent extends DataEvent<BlockInfoTarget, Optional<MutableText>> {
			Event<BlockInfoEvent> EVENT = EventFactory.createArrayBacked(
					BlockInfoEvent.class,
					listeners -> (blockState, mainHandStack) -> Arrays.stream(listeners)
							.map(event -> event.blockInfo(blockState, mainHandStack))
							.filter(Optional::isPresent)
							.findFirst()
							.orElse(Optional.empty())
			);

			Optional<MutableText> blockInfo(BlockState blockState, ItemStack mainHandStack);

			@Override
			default Optional<MutableText> fallback() {
				return Optional.empty();
			}

			@Override
			default BlockInfoTarget target() {
				return BLOCK_INFO;
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
				Animations.Texts.subtitleLeftBelow(
						BlockInfoTarget.BlockInfoEvent.EVENT.invoker().blockInfo(blockState, itemStack).orElse(Text.empty())
				);
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
