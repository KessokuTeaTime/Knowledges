package net.krlite.knowledges.components;

import net.krlite.equator.math.algebra.Theory;
import net.krlite.equator.math.geometry.flat.Box;
import net.krlite.equator.render.renderer.Flat;
import net.krlite.equator.visual.color.Palette;
import net.krlite.knowledges.Knowledge;
import net.krlite.knowledges.Knowledges;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CrosshairComponent implements Knowledge {
	@Override
	public void render(@NotNull MatrixStack matrixStack, @NotNull MinecraftClient client, @NotNull PlayerEntity player, @NotNull ClientWorld world) {
		Box box = crosshairSafeArea()
						  .scaleCenter(Knowledges.Animations.focusingBlock())
						  .scaleCenter(1 + 0.3 * Knowledges.Animations.mouseHolding());

		// Shadow
		box.render(matrixStack,
				flat -> flat.new Rectangle()
								.colors(Palette.Minecraft.BLACK)
								.opacityMultiplier(0.075 * Knowledges.Animations.ovalOpacity())
								.new Outlined(box.size())
								.style(Flat.Rectangle.Outlined.OutliningStyle.EDGE_FADED)
		);

		// Oval
		box.render(matrixStack,
				flat -> flat.new Oval()
								.colorCenter(Knowledges.Animations.ovalColor())
								.mode(Flat.Oval.OvalMode.FILL)
		);

		// Ring
		if (Theory.looseGreater(Knowledges.Animations.ringRadians(), 0)) {
			box.render(matrixStack,
					flat -> flat.new Oval()
									.radians(Knowledges.Animations.ringRadians())
									.mode(Flat.Oval.OvalMode.FILL_GRADIANT_OUT)
									.opacityMultiplier(Knowledges.Animations.ovalOpacity())

									.colorCenter(Knowledges.Animations.ringColor().opacity(0.3))

									.addColor(0, Palette.TRANSPARENT)
									.addColor(
											Knowledges.Animations.ringRadians(),
											Knowledges.Animations.ringColor()
									)
			);
		}
	}

	@Override
	public @NotNull Text name() {
		return Knowledges.localize("knowledge", "crosshair", "name");
	}

	@Override
	public @Nullable Text tooltip() {
		return Knowledges.localize("knowledge", "crosshair", "tooltip");
	}
}
