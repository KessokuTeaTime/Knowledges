package net.krlite.knowledges.impl.component.info;

import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.AbstractFieldBuilder;
import net.fabricmc.fabric.api.mininglevel.v1.MiningLevelManager;
import net.krlite.equator.visual.color.Palette;
import net.krlite.equator.visual.color.base.ColorStandard;
import net.krlite.knowledges.KnowledgesClient;
import net.krlite.knowledges.api.data.DataInvoker;
import net.krlite.knowledges.api.data.DataProtocol;
import net.krlite.knowledges.api.proxy.KnowledgeProxy;
import net.krlite.knowledges.api.proxy.RenderProxy;
import net.krlite.knowledges.api.representable.BlockRepresentable;
import net.krlite.knowledges.api.representable.Representable;
import net.krlite.knowledges.config.modmenu.KnowledgesConfigScreen;
import net.krlite.knowledges.impl.component.AbstractInfoComponent;
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
				return protocols -> (representable) -> protocols.stream()
						.map(protocol -> protocol.blockInformation(representable))
						.filter(Optional::isPresent)
						.findFirst()
						.orElse(Optional.empty());
			}
		};

		interface Protocol extends DataProtocol<BlockInfoComponent> {
			Optional<MutableText> blockInformation(BlockRepresentable representable);

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
	public void render(RenderProxy renderProxy, @NotNull Representable<?> representable) {
		if (representable.type() == HitResult.Type.BLOCK && representable instanceof BlockRepresentable blockRepresentable) {
			BlockState blockState = blockRepresentable.blockState();
			World world = blockRepresentable.world();
			PlayerEntity player = blockRepresentable.player();

			MutableText blockName = blockState.getBlock().getName();

			float hardness = blockState.getHardness(world, blockRepresentable.blockPos());
			boolean harvestable = hardness >= 0 && player.canHarvest(blockState);

			String
					namespace = KnowledgeProxy.getNamespace(blockState.getBlock().asItem().getDefaultStack()),
					path = KnowledgeProxy.getId(blockState.getBlock()).getPath();

			boolean resetBlockBreakingProgress = !Animation.Contextual.cancelledBlockBreaking() && Animation.Contextual.rawBlockBreakingProgress() < Animation.Ring.blockBreakingProgress();
			Animation.Ring.blockBreakingProgress(Animation.Contextual.rawBlockBreakingProgress(), resetBlockBreakingProgress);
			Animation.Ring.ringArc(Math.PI * 2 * Animation.Contextual.rawBlockBreakingProgress(), resetBlockBreakingProgress);

			Animation.Ring.ovalColor(harvestable ? Palette.Minecraft.WHITE : Palette.Minecraft.RED);
			Animation.Ring.ringColor(Palette.Minecraft.YELLOW.mix(Palette.Minecraft.GREEN, Animation.Ring.blockBreakingProgress(), ColorStandard.MixMode.PIGMENT));



			// Titles
			titles: {
				Animation.Text.titleRight(blockName);
				Animation.Text.titleLeft(KnowledgeProxy.getModName(namespace));
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
					Optional<MutableText> data = MineableToolInvoker.INSTANCE.invoker().mineableTool(blockState);

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
						BlockInformationInvoker.INSTANCE.invoker().blockInformation(blockRepresentable)
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
	public Function<ConfigEntryBuilder, List<AbstractFieldBuilder<?, ?, ?>>> buildConfigEntries() {
		return entryBuilder -> List.of(
				entryBuilder.startBooleanToggle(
								localize("config", "block_powered_status"),
								KnowledgesClient.CONFIG.get().components.infoBlock.showsBlockPoweredStatus
						)
						.setDefaultValue(KnowledgesClient.DEFAULT_CONFIG.components.infoBlock.showsBlockPoweredStatus)
						.setTooltip(localize("config", "block_powered_status", "tooltip"))
						.setSaveConsumer(value -> KnowledgesClient.CONFIG.get().components.infoBlock.showsBlockPoweredStatus = value)
						.setYesNoTextSupplier(KnowledgesConfigScreen.BooleanSupplier.DISPLAYED_HIDDEN)
		);
	}
}
