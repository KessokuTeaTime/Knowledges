package net.krlite.knowledges.components;

import net.krlite.equator.math.geometry.flat.Box;
import net.krlite.equator.render.frame.FrameInfo;
import net.krlite.equator.visual.color.Palette;
import net.krlite.equator.visual.color.base.ColorStandard;
import net.krlite.equator.visual.text.Paragraph;
import net.krlite.equator.visual.text.Section;
import net.krlite.knowledges.Knowledge;
import net.krlite.knowledges.Knowledges;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.NotNull;

public abstract class InfoComponent implements Knowledge {
	public Box info() {
		return FrameInfo.scaled()
					   .leftCenter(crosshairSafeArea().rightCenter())
					   .shift(5 + 3 * scalar(), 2 * scalar());
	}

	@Override
	public void render(@NotNull MatrixStack matrixStack, @NotNull MinecraftClient client, @NotNull PlayerEntity player, @NotNull ClientWorld world) {
		if (!Info.hasCrosshairTarget()) {
			Knowledges.Animations.textLength(0);
			Knowledges.Animations.ovalColor(Palette.Minecraft.WHITE);
		}

		info().render(matrixStack,
				flat -> flat.new Text(section -> section.fontSize(0.9 * scalar()).append(Knowledges.Animations.text()))
								.verticalAlignment(Section.Alignment.CENTER)
								.horizontalAlignment(Paragraph.Alignment.LEFT)
								.color(
										Palette.Minecraft.WHITE
												.mix(Knowledges.Animations.ovalColor(), 0.8, ColorStandard.MixMode.PIGMENT)
												.mix(Knowledges.Animations.ringColor(), Knowledges.Animations.ringRadians() / (Math.PI * 2), ColorStandard.MixMode.PIGMENT)
												.opacity(0.3)
								)
		);
	}
}
