package net.krlite.knowledges.component.info;

import net.krlite.equator.visual.color.Palette;
import net.krlite.knowledges.component.AbstractInfoComponent;
import net.krlite.knowledges.core.data.DataInvoker;
import net.krlite.knowledges.core.data.DataProtocol;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class EntityInfoComponent extends AbstractInfoComponent {
	public interface EntityInformationInvoker extends DataInvoker<EntityInfoComponent, EntityInfoComponent.EntityInformationInvoker.Protocol> {
		EntityInformationInvoker INSTANCE = new EntityInformationInvoker() {
			@Override
			public @NotNull Function<List<Protocol>, Protocol> protocolStream() {
				return protocols -> (entity, player) -> protocols.stream()
						.map(protocol -> protocol.entityInformation(entity, player))
						.filter(Optional::isPresent)
						.findFirst()
						.orElse(Optional.empty());
			}
		};

		interface Protocol extends DataProtocol<EntityInfoComponent> {
			Optional<MutableText> entityInformation(Entity entity, PlayerEntity player);

			@Override
			default DataInvoker<EntityInfoComponent, ?> dataInvoker() {
				return EntityInformationInvoker.INSTANCE;
			}
		}

		@Override
		default @NotNull Class<EntityInfoComponent> targetKnowledgeClass() {
			return EntityInfoComponent.class;
		}
	}

	public interface EntityDescriptionInvoker extends DataInvoker<EntityInfoComponent, EntityInfoComponent.EntityDescriptionInvoker.Protocol> {
		EntityDescriptionInvoker INSTANCE = new EntityDescriptionInvoker() {
			@Override
			public @NotNull Function<List<Protocol>, Protocol> protocolStream() {
				return protocols -> (entity, player) -> protocols.stream()
						.map(protocol -> protocol.entityDescription(entity, player))
						.filter(Optional::isPresent)
						.findFirst()
						.orElse(Optional.empty());
			}
		};

		interface Protocol extends DataProtocol<EntityInfoComponent> {
			Optional<MutableText> entityDescription(Entity entity, PlayerEntity player);

			@Override
			default DataInvoker<EntityInfoComponent, ?> dataInvoker() {
				return EntityDescriptionInvoker.INSTANCE;
			}
		}

		@Override
		default @NotNull Class<EntityInfoComponent> targetKnowledgeClass() {
			return EntityInfoComponent.class;
		}
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
				Animations.Texts.subtitleRightBelow(
						EntityInformationInvoker.INSTANCE.invoker().entityInformation(entity, player).orElse(Text.empty())
				);
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
						EntityDescriptionInvoker.INSTANCE.invoker().entityDescription(entity, player).orElse(Text.empty())
				);
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
