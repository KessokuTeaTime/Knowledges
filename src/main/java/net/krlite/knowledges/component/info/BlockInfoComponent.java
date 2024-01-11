package net.krlite.knowledges.component.info;

import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.AbstractFieldBuilder;
import net.fabricmc.fabric.api.mininglevel.v1.MiningLevelManager;
import net.krlite.equator.visual.color.Palette;
import net.krlite.equator.visual.color.base.ColorStandard;
import net.krlite.knowledges.Knowledges;
import net.krlite.knowledges.component.AbstractInfoComponent;
import net.krlite.knowledges.config.KnowledgesConfig;
import net.krlite.knowledges.config.modmenu.KnowledgesConfigScreen;
import net.krlite.knowledges.core.data.DataInvoker;
import net.krlite.knowledges.core.data.DataProtocol;
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
	public interface MineableToolInvoker extends DataInvoker<BlockInfoComponent, MineableToolInvoker.Protocol> {
		MineableToolInvoker INSTANCE = new MineableToolInvoker() {
			@Override
			public @NotNull Function<List<Protocol>, Protocol> protocolStream() {
				return protocols -> blockState -> protocols.stream()
						.map(protocol -> protocol.mineableTool(blockState))
						.filter(Optional::isPresent)
						.findFirst()
						.orElse(Optional.empty());
			}
		};

		interface Protocol extends DataProtocol<BlockInfoComponent> {
			Optional<MutableText> mineableTool(BlockState blockState);

			@Override
			default DataInvoker<BlockInfoComponent, ?> dataInvoker() {
				return MineableToolInvoker.INSTANCE;
			}
		}

		@Override
		default @NotNull Class<BlockInfoComponent> targetKnowledgeClass() {
			return BlockInfoComponent.class;
		}
	}

	public interface BlockInformationInvoker extends DataInvoker<BlockInfoComponent, BlockInformationInvoker.Protocol> {
		BlockInformationInvoker INSTANCE = new BlockInformationInvoker() {
			@Override
			public @NotNull Function<List<Protocol>, Protocol> protocolStream() {
				return protocols -> (blockState, player) -> protocols.stream()
						.map(protocol -> protocol.blockInformation(blockState, player))
						.filter(Optional::isPresent)
						.findFirst()
						.orElse(Optional.empty());
			}
		};

		interface Protocol extends DataProtocol<BlockInfoComponent> {
			Optional<MutableText> blockInformation(BlockState blockState, PlayerEntity player);

			@Override
			default DataInvoker<BlockInfoComponent, ?> dataInvoker() {
				return BlockInformationInvoker.INSTANCE;
			}
		}

		@Override
		default @NotNull Class<BlockInfoComponent> targetKnowledgeClass() {
			return BlockInfoComponent.class;
		}
	}

	@Override
	public void render(@NotNull DrawContext context, @NotNull MinecraftClient client, @NotNull PlayerEntity player, @NotNull ClientWorld world) {
		Info.crosshairBlockState().ifPresent(blockState -> {
			MutableText blockName = blockState.getBlock().getName();

			float hardness = Info.crosshairBlockPos()
					.map(blockPos -> blockState.getHardness(world, blockPos))
					.orElse(0F);
			boolean harvestable = hardness >= 0 && player.canHarvest(blockState);

			String
					namespace = Util.namespace(blockState.getBlock().asItem().getDefaultStack()),
					path = Registries.BLOCK.getId(blockState.getBlock()).getPath();

			Animations.Ring.ovalColor(harvestable ? Palette.Minecraft.WHITE : Palette.Minecraft.RED);
			Animations.Ring.ringColor(Palette.Minecraft.YELLOW.mix(Palette.Minecraft.GREEN, Animations.Ring.blockBreakingProgress(), ColorStandard.MixMode.PIGMENT));

			// Titles
			titles: {
				Animations.Texts.titleRight(blockName);
				Animations.Texts.titleLeft(Util.modName(namespace));
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
					Optional<MutableText> data = MineableToolInvoker.INSTANCE.invoker().mineableTool(blockState);

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
				boolean powered = Knowledges.CONFIG.infoBlockShowPoweredStatus()
						&& Info.crosshairBlockPos().map(world::isReceivingRedstonePower).orElse(false);

				Animations.Texts.subtitleLeftBelow(
						BlockInformationInvoker.INSTANCE.invoker().blockInformation(blockState, player)
								.orElse(powered ? localize("powered") : Text.empty())
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

	@Override
	public boolean requestsConfigPage() {
		return true;
	}

	@Override
	public Function<ConfigEntryBuilder, List<AbstractFieldBuilder<?, ?, ?>>> buildConfigEntries() {
		return entryBuilder -> List.of(
				entryBuilder.startBooleanToggle(
								localize("config", "show_powered_status"),
								Knowledges.CONFIG.infoBlockShowPoweredStatus()
						)
						.setDefaultValue(KnowledgesConfig.Default.INFO_BLOCK_SHOW_POWERED_STATUS)
						.setTooltip(localize("config", "show_powered_status", "tooltip"))
						.setSaveConsumer(Knowledges.CONFIG::infoBlockShowPoweredStatus)
						.setYesNoTextSupplier(KnowledgesConfigScreen.DISPLAYED_HIDDEN_SUPPLIER)
		);
	}
}
