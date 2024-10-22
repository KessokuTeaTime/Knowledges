package band.kessokuteatime.knowledges.impl.component.info;

import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.FieldBuilder;
import net.fabricmc.fabric.api.mininglevel.v1.MiningLevelManager;
import net.krlite.equator.visual.color.Palette;
import net.krlite.equator.visual.color.base.ColorStandard;
import band.kessokuteatime.knowledges.KnowledgesClient;
import band.kessokuteatime.knowledges.Util;
import band.kessokuteatime.knowledges.api.data.transfer.DataInvoker;
import band.kessokuteatime.knowledges.api.data.transfer.DataProtocol;
import band.kessokuteatime.knowledges.api.proxy.ModProxy;
import band.kessokuteatime.knowledges.api.proxy.RenderProxy;
import band.kessokuteatime.knowledges.api.representable.BlockRepresentable;
import band.kessokuteatime.knowledges.api.representable.base.Representable;
import band.kessokuteatime.knowledges.config.modmenu.KnowledgesConfigScreen;
import band.kessokuteatime.knowledges.impl.component.base.InfoComponent;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class BlockInfoComponent extends InfoComponent {
	public interface MineableTool extends DataInvoker<BlockInfoComponent, MineableTool.Protocol> {
		MineableTool INVOKER = new MineableTool() {
			@Override
			public @NotNull Function<List<Protocol>, Protocol> protocolStream() {
				return protocols -> blockState -> Util.Text.combineToMultiline(
						protocols.stream()
								.map(protocol -> protocol.mineableToolName(blockState))
								.filter(Optional::isPresent)
								.toList()
				);
			}
		};

		@FunctionalInterface
		interface Protocol extends DataProtocol<BlockInfoComponent> {
			Optional<MutableText> mineableToolName(BlockState blockState);

			@Override
			default DataInvoker<BlockInfoComponent, ?> dataInvoker() {
				return MineableTool.INVOKER;
			}
		}

		@Override
		default @NotNull Class<BlockInfoComponent> targetKnowledgeClass() {
			return BlockInfoComponent.class;
		}
	}

	public interface BlockInformation extends DataInvoker<BlockInfoComponent, BlockInformation.Protocol> {
		BlockInformation INVOKER = new BlockInformation() {
			@Override
			public @NotNull Function<List<Protocol>, Protocol> protocolStream() {
				return protocols -> (representable) -> Util.Text.combineToMultiline(
						protocols.stream()
								.map(protocol -> protocol.blockInformation(representable))
								.filter(Optional::isPresent)
								.toList()
				);
			}
		};

		@FunctionalInterface
		interface Protocol extends DataProtocol<BlockInfoComponent> {
			Optional<MutableText> blockInformation(BlockRepresentable representable);

			@Override
			default DataInvoker<BlockInfoComponent, ?> dataInvoker() {
				return BlockInformation.INVOKER;
			}
		}

		@Override
		default @NotNull Class<BlockInfoComponent> targetKnowledgeClass() {
			return BlockInfoComponent.class;
		}
	}

	@Override
	public void render(RenderProxy renderProxy, @NotNull Representable<?> representable) {
		if (representable.type() == HitResult.Type.BLOCK && representable instanceof BlockRepresentable blockRepresentable) {
			BlockState blockState = blockRepresentable.blockState();
			World world = blockRepresentable.world();
			PlayerEntity player = blockRepresentable.player();

			MutableText blockName = blockState.getBlock().getName();

			float hardness = blockState.getHardness(world, blockRepresentable.blockPos());
			boolean harvestable = hardness >= 0 && player.canHarvest(blockState);

			String
					namespace = ModProxy.getNamespace(blockState.getBlock().asItem().getDefaultStack()),
					path = ModProxy.getId(blockState.getBlock()).getPath();

			boolean resetBlockBreakingProgress = !Animation.Contextual.cancelledBlockBreaking() && Animation.Contextual.rawBlockBreakingProgress() < Animation.Ring.blockBreakingProgress();
			Animation.Ring.blockBreakingProgress(Animation.Contextual.rawBlockBreakingProgress(), resetBlockBreakingProgress);
			Animation.Ring.ringArc(Math.PI * 2 * Animation.Contextual.rawBlockBreakingProgress(), resetBlockBreakingProgress);

			Animation.Ring.ovalColor(harvestable ? Palette.Minecraft.WHITE : Palette.Minecraft.RED);
			Animation.Ring.ringColor(Palette.Minecraft.YELLOW.mix(Palette.Minecraft.GREEN, Animation.Ring.blockBreakingProgress(), ColorStandard.MixMode.PIGMENT));



			// Titles
			titles: {
				Animation.Text.titleRight(blockName);
				Animation.Text.titleLeft(ModProxy.getModName(namespace));
			}

			// Right Above
			if (MinecraftClient.getInstance().options.advancedItemTooltips) subtitleRightAbove: {
				Animation.Text.subtitleRightAbove(net.minecraft.text.Text.literal(path));
			} else {
				Animation.Text.subtitleRightAbove(net.minecraft.text.Text.empty());
			}

			// Right Below
			subtitleRightBelow: {
				MutableText tool, miningLevel = null;
				boolean foundSemanticMiningLevel = false;

				tool: {
					Optional<MutableText> data = MineableTool.INVOKER.invoker().mineableToolName(blockState);

					if (hardness < 0) {
						// Unbreakable
						tool = localize("tool", "unbreakable");
					} else if (data.isPresent()) {
						tool = data.get();
					} else {
						Animation.Text.subtitleRightBelow(net.minecraft.text.Text.empty());
						break subtitleRightBelow;
					}
				}

				miningLevel: {
					int requiredLevel = MiningLevelManager.getRequiredMiningLevel(blockState);
					if (requiredLevel <= 0) break miningLevel;

					String localizationKey = localizationKey("mining_level");
					String localizationKeyWithLevel = localizationKey("mining_level", String.valueOf(requiredLevel));
					MutableText localizationWithLevel = net.minecraft.text.Text.translatable(localizationKeyWithLevel);

					foundSemanticMiningLevel = !localizationWithLevel.getString().equals(localizationKeyWithLevel);
					miningLevel = foundSemanticMiningLevel ? localizationWithLevel : net.minecraft.text.Text.translatable(localizationKey, requiredLevel);
				}

				if (miningLevel == null) {
					Animation.Text.subtitleRightBelow(net.minecraft.text.Text.translatable(localizationKey("tool"), tool));
				} else {
					Animation.Text.subtitleRightBelow(net.minecraft.text.Text.translatable(
							foundSemanticMiningLevel ? localizationKey("tool_and_mining_level_semantic") : localizationKey("tool_and_mining_level"),
							tool, miningLevel
					));
				}
			}

			// Left Above
			if (MinecraftClient.getInstance().options.advancedItemTooltips) subtitleLeftAbove: {
				Animation.Text.subtitleLeftAbove(net.minecraft.text.Text.literal(namespace));
			} else {
				Animation.Text.subtitleLeftAbove(net.minecraft.text.Text.empty());
			}

			// Left Below
			subtitleLeftBelow: {
				boolean powered = KnowledgesClient.CONFIG.get().components.infoBlock.showsBlockPoweredStatus
						&& world.isReceivingRedstonePower(blockRepresentable.blockPos());

				Animation.Text.subtitleLeftBelow(
						BlockInformation.INVOKER.invoker().blockInformation(blockRepresentable)
								.orElse(powered ? localize("powered") : net.minecraft.text.Text.empty())
				);
			}
		}
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
	public Function<ConfigEntryBuilder, List<FieldBuilder<?, ?, ?>>> buildConfigEntries() {
		return entryBuilder -> List.of(
				entryBuilder.startBooleanToggle(
								localizeForConfig("block_powered_status"),
								KnowledgesClient.CONFIG.get().components.infoBlock.showsBlockPoweredStatus
						)
						.setDefaultValue(KnowledgesClient.DEFAULT_CONFIG.components.infoBlock.showsBlockPoweredStatus)
						.setTooltip(localizeTooltipForConfig("block_powered_status"))
						.setSaveConsumer(value -> KnowledgesClient.CONFIG.get().components.infoBlock.showsBlockPoweredStatus = value)
						.setYesNoTextSupplier(KnowledgesConfigScreen.BooleanSupplier.DISPLAYED_HIDDEN)
		);
	}
}
