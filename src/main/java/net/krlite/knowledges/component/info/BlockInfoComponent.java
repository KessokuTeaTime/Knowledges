package net.krlite.knowledges.component.info;

import net.fabricmc.fabric.api.mininglevel.v1.MiningLevelManager;
import net.krlite.equator.visual.color.Palette;
import net.krlite.equator.visual.color.base.ColorStandard;
import net.krlite.knowledges.Knowledges;
import net.krlite.knowledges.component.AbstractInfoComponent;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class BlockInfoComponent extends AbstractInfoComponent {
	public interface MineableToolDataSource extends DataSource<BlockInfoComponent, MineableToolDataSource>, DataSource.Functional<MineableToolDataSource> {
		Optional<MutableText> mineableTool(BlockState blockState);

		@Override
		default Function<List<MineableToolDataSource>, MineableToolDataSource> function() {
			return listeners -> blockState -> listeners.stream()
					.map(listener -> listener.mineableTool(blockState))
					.filter(Optional::isPresent)
					.findFirst()
					.orElse(Optional.empty());
		}

		@Override
		default Class<BlockInfoComponent> target() {
			return BlockInfoComponent.class;
		}
	}

	public interface BlockInformationDataSource extends DataSource<BlockInfoComponent, BlockInformationDataSource>, DataSource.Functional<BlockInformationDataSource> {
		Optional<MutableText> blockInformation(BlockState blockState, PlayerEntity player);

		@Override
		default Function<List<BlockInformationDataSource>, BlockInformationDataSource> function() {
			return listeners -> (blockState, player) -> listeners.stream()
					.map(listener -> listener.blockInformation(blockState, player))
					.filter(Optional::isPresent)
					.findFirst()
					.orElse(Optional.empty());
		}

		@Override
		default BlockInformationDataSource invoker() {
			return (blockState, player) -> function().apply(listeners()).blockInformation(blockState, player);
		}

		@Override
		default Class<BlockInfoComponent> target() {
			return BlockInfoComponent.class;
		}
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
					Optional<MutableText> data = MineableToolDataSource.invoker().mineableTool(blockState);

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
						BlockInformationCallback.EVENT.invoker().blockInformation(blockState, player).orElse(Text.empty())
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
