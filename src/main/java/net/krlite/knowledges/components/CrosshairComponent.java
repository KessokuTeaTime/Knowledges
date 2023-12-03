package net.krlite.knowledges.components;

import net.krlite.equator.math.algebra.Theory;
import net.krlite.equator.math.geometry.flat.Box;
import net.krlite.equator.render.renderer.Flat;
import net.krlite.equator.visual.color.Palette;
import net.krlite.knowledges.api.Knowledge;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.NotNull;

public class CrosshairComponent implements Knowledge {
	@Override
	public void render(@NotNull DrawContext context, @NotNull MinecraftClient client, @NotNull PlayerEntity player, @NotNull ClientWorld world) {
		Box box = crosshairSafeArea()
				.scaleCenter(InfoComponent.Animations.Ring.focusingBlock())
				.scaleCenter(1 + 0.3 * InfoComponent.Animations.Ring.mouseHolding());

		// Shadow
		box.render(context, flat ->
				flat.new Rectangle()
						.colors(Palette.Minecraft.BLACK)
						.opacityMultiplier(0.08 * InfoComponent.Animations.Ring.ovalOpacity())
						.new Outlined(box.size())
						.style(Flat.Rectangle.Outlined.OutliningStyle.EDGE_FADED)
		);

		// Oval
		box.render(context, flat ->
				flat.new Oval()
						.colorCenter(InfoComponent.Animations.Ring.ovalColor())
						.mode(Flat.Oval.OvalMode.FILL)
		);

		// Ring
		if (Theory.looseGreater(InfoComponent.Animations.Ring.ringRadians(), 0)) {
			box.render(context, flat ->
					flat.new Oval()
							.radians(InfoComponent.Animations.Ring.ringRadians())
							.mode(Flat.Oval.OvalMode.FILL_GRADIANT_OUT)
							.opacityMultiplier(InfoComponent.Animations.Ring.ovalOpacity())

							.colorCenter(InfoComponent.Animations.Ring.ringColor().opacity(0.4))

							.addColor(0, Palette.TRANSPARENT)
							.addColor(
									InfoComponent.Animations.Ring.ringRadians(),
									InfoComponent.Animations.Ring.ringColor()
							)
			);
		}
	}

	@Override
	public @NotNull String id() {
		return "crosshair";
	}

	@Override
	public boolean provideTooltip() {
		return true;
	}
}
