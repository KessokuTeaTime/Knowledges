package net.krlite.knowledges.components.info;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.krlite.equator.visual.color.Palette;
import net.krlite.knowledges.components.AbstractInfoComponent;
import net.krlite.knowledges.core.datacallback.DataCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.village.VillagerData;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;

public class EntityInfoComponent extends AbstractInfoComponent {
	public interface EntityInformationCallback extends DataCallback<EntityInformationCallback> {
		Event<EntityInformationCallback> EVENT = EventFactory.createArrayBacked(
				EntityInformationCallback.class,
				listeners -> (entity, player) -> Arrays.stream(listeners)
						.map(listener -> listener.entityInformation(entity, player))
						.filter(Optional::isPresent)
						.findFirst()
						.orElse(Optional.empty())
		);

		Optional<MutableText> entityInformation(Entity entity, PlayerEntity player);

		@Override
		default Event<EntityInformationCallback> event() {
			return EVENT;
		}

		@Override
		default String name() {
			return "Entity Information";
		}
	}

	public interface EntityDescriptionCallback extends DataCallback<EntityDescriptionCallback> {
		Event<EntityDescriptionCallback> EVENT = EventFactory.createArrayBacked(
				EntityDescriptionCallback.class,
				listeners -> (entity, player) -> Arrays.stream(listeners)
						.map(listener -> listener.entityDescription(entity, player))
						.filter(Optional::isPresent)
						.findFirst()
						.orElse(Optional.empty())
		);

		Optional<MutableText> entityDescription(Entity entity, PlayerEntity player);

		@Override
		default Event<EntityDescriptionCallback> event() {
			return EVENT;
		}

		@Override
		default String name() {
			return "Entity Description";
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
						EntityInformationCallback.EVENT.invoker().entityInformation(entity, player).orElse(Text.empty())
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
						EntityDescriptionCallback.EVENT.invoker().entityDescription(entity, player).orElse(Text.empty())
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
