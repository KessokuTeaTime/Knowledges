package net.krlite.knowledges.components.info;

import net.krlite.equator.visual.color.Palette;
import net.krlite.knowledges.Knowledges;
import net.krlite.knowledges.components.InfoComponent;
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
import org.jetbrains.annotations.Nullable;

public class EntityInfoComponent extends InfoComponent {
	@Override
	public void render(@NotNull DrawContext context, @NotNull MinecraftClient client, @NotNull PlayerEntity player, @NotNull ClientWorld world) {
		super.render(context, client, player, world);
		@Nullable Entity entity = Info.crosshairEntity();

		if (entity != null) {
			MutableText entityName = entity.getDisplayName().copy();

			// Titles
			titles: {
				Animations.Texts.titleRight(entityName);
				Animations.Texts.titleLeft(Knowledges.getModName(Registries.ENTITY_TYPE.getId(entity.getType()).getNamespace()));
			}

			if (entity.isInvulnerable()) {
				Animations.Ring.ringColor(Palette.Minecraft.LIGHT_PURPLE);
				Animations.Ring.ovalColor(Palette.Minecraft.LIGHT_PURPLE);
			} else {
				switch (entity.getType().getSpawnGroup()) {
					case MONSTER -> {
						Animations.Ring.ringColor(Palette.Minecraft.RED);
						Animations.Ring.ovalColor(Palette.Minecraft.RED);
					}
					case WATER_CREATURE -> {
						Animations.Ring.ringColor(Palette.Minecraft.AQUA);
						Animations.Ring.ovalColor(Palette.Minecraft.WHITE);
					}
					case MISC -> {
						Animations.Ring.ringColor(Palette.Minecraft.GRAY);
						Animations.Ring.ovalColor(Palette.Minecraft.DARK_GRAY);
					}
					case AMBIENT -> {
						Animations.Ring.ringColor(Palette.Minecraft.WHITE);
						Animations.Ring.ovalColor(Palette.Minecraft.WHITE);
					}
					default -> {
						Animations.Ring.ringColor(Palette.Minecraft.GREEN);
						Animations.Ring.ovalColor(Palette.Minecraft.WHITE);
					}
				}
			}

			if (!entity.isInvulnerable() && entity instanceof LivingEntity livingEntity) {
				double health = livingEntity.getHealth() / livingEntity.getMaxHealth();

				Animations.Ring.ringRadians(Math.PI * 2 * health);
			}

			// Left Above
			subtitleLeftAbove: {
				Animations.Texts.subtitleLeftAbove(Text.literal(Registries.ENTITY_TYPE.getId(entity.getType()).getNamespace()));
			}
		}
	}

	@Override
	public @NotNull String infoId() {
		return "entity";
	}

	@Override
	public boolean provideTooltip() {
		return true;
	}
}
