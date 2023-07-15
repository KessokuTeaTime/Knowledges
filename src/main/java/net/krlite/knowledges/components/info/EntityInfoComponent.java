package net.krlite.knowledges.components.info;

import net.krlite.equator.visual.color.Palette;
import net.krlite.knowledges.Knowledges;
import net.krlite.knowledges.components.InfoComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EntityInfoComponent extends InfoComponent {
	@Override
	public void render(@NotNull MatrixStack matrixStack, @NotNull MinecraftClient client, @NotNull PlayerEntity player, @NotNull ClientWorld world) {
		super.render(matrixStack, client, player, world);
		@Nullable Entity entity = Info.crosshairEntity();

		if (entity != null) {
			MutableText entityName = entity.getDisplayName().copy();
			Knowledges.Animations.textLength(entityName.getString().length());

			Knowledges.Animations.text(entityName);

			if (entity.isInvulnerable()) {
				Knowledges.Animations.ringColor(Palette.Minecraft.LIGHT_PURPLE);
				Knowledges.Animations.ovalColor(Palette.Minecraft.LIGHT_PURPLE);
			} else {
				switch (entity.getType().getSpawnGroup()) {
					case MONSTER -> {
						Knowledges.Animations.ringColor(Palette.Minecraft.RED);
						Knowledges.Animations.ovalColor(Palette.Minecraft.RED);
					}
					case WATER_CREATURE -> {
						Knowledges.Animations.ringColor(Palette.Minecraft.AQUA);
						Knowledges.Animations.ovalColor(Palette.Minecraft.WHITE);
					}
					case MISC -> {
						Knowledges.Animations.ringColor(Palette.Minecraft.GRAY);
						Knowledges.Animations.ovalColor(Palette.Minecraft.DARK_GRAY);
					}
					case AMBIENT -> {
						Knowledges.Animations.ringColor(Palette.Minecraft.WHITE);
						Knowledges.Animations.ovalColor(Palette.Minecraft.WHITE);
					}
					default -> {
						Knowledges.Animations.ringColor(Palette.Minecraft.GREEN);
						Knowledges.Animations.ovalColor(Palette.Minecraft.WHITE);
					}
				}
			}

			if (!entity.isInvulnerable() && entity instanceof LivingEntity livingEntity) {
				double health = livingEntity.getHealth() / livingEntity.getMaxHealth();

				Knowledges.Animations.ringRadians(Math.PI * 2 * health);
			}
		}
	}

	@Override
	public @NotNull Text name() {
		return Text.translatable("knowledge" + Knowledges.ID + ".entity_info.name");
	}

	@Override
	public @NotNull Text tooltip() {
		return Text.translatable("knowledge" + Knowledges.ID + ".entity_info.tooltip");
	}
}
