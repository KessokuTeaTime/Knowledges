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
import org.jetbrains.annotations.NotNull;

public class CrosshairComponent implements Knowledge {
	@Override
	public void render(@NotNull MatrixStack matrixStack, @NotNull MinecraftClient client, @NotNull PlayerEntity player, @NotNull ClientWorld world) {
		Box box = crosshairSafeArea()
						  .scaleCenter(Knowledges.Animations.focusingBlock())
						  .scaleCenter(1 + 0.3 * Knowledges.Animations.mouseHolding());

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
}
