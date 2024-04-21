package band.kessokuteatime.knowledges.impl.component.info;

import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.FieldBuilder;
import net.krlite.equator.math.algebra.Theory;
import net.krlite.equator.visual.color.Palette;
import band.kessokuteatime.knowledges.KnowledgesClient;
import band.kessokuteatime.knowledges.Util;
import band.kessokuteatime.knowledges.api.proxy.ModProxy;
import band.kessokuteatime.knowledges.api.proxy.RenderProxy;
import band.kessokuteatime.knowledges.api.representable.EntityRepresentable;
import band.kessokuteatime.knowledges.api.representable.base.Representable;
import band.kessokuteatime.knowledges.impl.component.base.InfoComponent;
import band.kessokuteatime.knowledges.config.modmenu.KnowledgesConfigScreen;
import band.kessokuteatime.knowledges.api.data.transfer.DataInvoker;
import band.kessokuteatime.knowledges.api.data.transfer.DataProtocol;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class EntityInfoComponent extends InfoComponent {
	public interface EntityInformation extends DataInvoker<EntityInfoComponent, EntityInformation.Protocol> {
		EntityInformation INVOKER = new EntityInformation() {
			@Override
			public @NotNull Function<List<Protocol>, Protocol> protocolStream() {
				return protocols -> (representable) -> Util.Text.combineToMultiline(
						protocols.stream()
								.map(protocol -> protocol.entityInformation(representable))
								.filter(Optional::isPresent)
								.toList()
				);
			}
		};

		@FunctionalInterface
		interface Protocol extends DataProtocol<EntityInfoComponent> {
			Optional<MutableText> entityInformation(EntityRepresentable representable);

			@Override
			default DataInvoker<EntityInfoComponent, ?> dataInvoker() {
				return EntityInformation.INVOKER;
			}
		}

		@Override
		default @NotNull Class<EntityInfoComponent> targetKnowledgeClass() {
			return EntityInfoComponent.class;
		}
	}

	public interface EntityDescription extends DataInvoker<EntityInfoComponent, EntityDescription.Protocol> {
		EntityDescription INVOKER = new EntityDescription() {
			@Override
			public @NotNull Function<List<Protocol>, Protocol> protocolStream() {
				return protocols -> (representable) -> Util.Text.combineToMultiline(
						protocols.stream()
								.map(protocol -> protocol.entityDescription(representable))
								.filter(Optional::isPresent)
								.toList()
				);
			}
		};

		@FunctionalInterface
		interface Protocol extends DataProtocol<EntityInfoComponent> {
			Optional<MutableText> entityDescription(EntityRepresentable representable);

			@Override
			default DataInvoker<EntityInfoComponent, ?> dataInvoker() {
				return EntityDescription.INVOKER;
			}
		}

		@Override
		default @NotNull Class<EntityInfoComponent> targetKnowledgeClass() {
			return EntityInfoComponent.class;
		}
	}

	@Override
	public void render(RenderProxy renderProxy, @NotNull Representable<?> representable) {
		if (representable.type() == HitResult.Type.ENTITY && representable instanceof EntityRepresentable entityRepresentable) {
			Entity entity = entityRepresentable.entity();
			PlayerEntity player = entityRepresentable.player();

			MutableText entityName = entity.getDisplayName().copy();

			String
					namespace = Registries.ENTITY_TYPE.getId(entity.getType()).getNamespace(),
					path = Registries.ENTITY_TYPE.getId(entity.getType()).getPath();

			// Titles
			titles: {
				Animation.Text.titleRight(entityName);
				Animation.Text.titleLeft(ModProxy.getModName(Registries.ENTITY_TYPE.getId(entity.getType()).getNamespace()));
			}

			switch (entity.getType().getSpawnGroup()) {
				case MONSTER -> {
					Animation.Ring.ringColor(Palette.Minecraft.RED);
					Animation.Ring.ovalColor(Palette.Minecraft.RED);
				}
				case WATER_CREATURE, UNDERGROUND_WATER_CREATURE -> {
					Animation.Ring.ringColor(Palette.Minecraft.AQUA);
					Animation.Ring.ovalColor(Palette.Minecraft.WHITE);
				}
				case WATER_AMBIENT -> {
					Animation.Ring.ringColor(Palette.Minecraft.BLUE);
					Animation.Ring.ovalColor(Palette.Minecraft.BLUE);
				}
				case MISC -> {
					Animation.Ring.ringColor(Palette.Minecraft.WHITE);
					Animation.Ring.ovalColor(Palette.Minecraft.WHITE);
				}
				case AMBIENT -> {
					Animation.Ring.ringColor(Palette.Minecraft.YELLOW);
					Animation.Ring.ovalColor(Palette.Minecraft.WHITE);
				}
				default -> {
					Animation.Ring.ringColor(Palette.Minecraft.GREEN);
					Animation.Ring.ovalColor(Palette.Minecraft.WHITE);
				}
			}

			if (entity.isInvulnerable()) {
				Animation.Ring.ovalColor(Palette.Minecraft.LIGHT_PURPLE);
			}

			if (!entity.isInvulnerable() && entity instanceof LivingEntity livingEntity) {
				float health = livingEntity.getHealth(), maxHealth = livingEntity.getMaxHealth();
				boolean notDamaged = health == maxHealth;
				double arc = Math.PI * 2 * (health / maxHealth);

				if (Animation.Contextual.entityWasNotDamaged() && !notDamaged) {
					Animation.Ring.ringArc(Math.min(arc + Math.PI / 3, Math.PI * 2 - Theory.EPSILON), true);
				}

				Animation.Ring.ringArc(arc);
				Animation.Text.numericHealth(health);
				Animation.Contextual.entityWasNotDamaged(notDamaged);
			}

			// Right Above
			if (MinecraftClient.getInstance().options.advancedItemTooltips) subtitleRightAbove: {
				Animation.Text.subtitleRightAbove(net.minecraft.text.Text.literal(path));
			} else {
				Animation.Text.subtitleRightAbove(net.minecraft.text.Text.empty());
			}

			// Right Below
			subtitleRightBelow: {
				Animation.Text.subtitleRightBelow(
						EntityInformation.INVOKER.invoker().entityInformation(entityRepresentable).orElse(net.minecraft.text.Text.empty())
				);
			}

			// Left Above
			if (MinecraftClient.getInstance().options.advancedItemTooltips) subtitleLeftAbove: {
				Animation.Text.subtitleLeftAbove(net.minecraft.text.Text.literal(namespace));
			} else {
				Animation.Text.subtitleLeftAbove(net.minecraft.text.Text.empty());
			}

			// Left Below
			subtitleLeftBelow: {
				Animation.Text.subtitleLeftBelow(
						EntityDescription.INVOKER.invoker().entityDescription(entityRepresentable).orElse(net.minecraft.text.Text.empty())
				);
			}
		} else {
			Animation.Text.clearNumericHealth();
			Animation.Contextual.entityWasNotDamaged(true);
		}
	}

	@Override
	public @NotNull String partialPath() {
		return "entity";
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
								localizeForConfig("numeric_health"),
								KnowledgesClient.CONFIG.get().components.infoEntity.showsNumericHealth
						)
						.setDefaultValue(KnowledgesClient.DEFAULT_CONFIG.components.infoEntity.showsNumericHealth)
						.setTooltip(localizeTooltipForConfig("numeric_health"))
						.setSaveConsumer(value -> KnowledgesClient.CONFIG.get().components.infoEntity.showsNumericHealth = value)
						.setYesNoTextSupplier(KnowledgesConfigScreen.BooleanSupplier.DISPLAYED_HIDDEN)
		);
	}
}
