package net.krlite.knowledges.components;

import net.krlite.equator.math.geometry.flat.Box;
import net.krlite.equator.render.frame.FrameInfo;
import net.krlite.equator.visual.color.AccurateColor;
import net.krlite.equator.visual.color.Palette;
import net.krlite.equator.visual.color.base.ColorStandard;
import net.krlite.equator.visual.text.Paragraph;
import net.krlite.equator.visual.text.Section;
import net.krlite.knowledges.Knowledge;
import net.krlite.knowledges.Knowledges;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class InfoComponent implements Knowledge {
	@Override
	public void render(@NotNull DrawContext context, @NotNull MinecraftClient client, @NotNull PlayerEntity player, @NotNull ClientWorld world) {
		if (!Info.hasCrosshairTarget()) {
			Knowledges.Animations.title(Text.empty());
			Knowledges.Animations.subtitle(Text.empty());

			Knowledges.Animations.ovalColor(Palette.Minecraft.WHITE);
		}

		// Title
		if (Knowledges.CONFIG.infoTitleEnabled()) {
			renderText(
					context,
					FrameInfo.scaled()
							.leftCenter(crosshairSafeArea().rightCenter())
							.shift(5 + 3 * scalar(), 2 * scalar()),
					Knowledges.Animations.title(),
					Paragraph.Alignment.LEFT,
					Palette.Minecraft.WHITE
							.mix(Knowledges.Animations.ovalColor(), 0.8, ColorStandard.MixMode.PIGMENT)
							.mix(Knowledges.Animations.ringColor(), Knowledges.Animations.ringRadians() / (Math.PI * 2), ColorStandard.MixMode.PIGMENT)
							.opacity(0.3)
			);
		}

		// Subtitle
		if (Knowledges.CONFIG.infoSubtitleEnabled()) {
			renderText(
					context,
					FrameInfo.scaled()
							.rightCenter(crosshairSafeArea().leftCenter())
							.shift(-5 - 3 * scalar(), 2 * scalar()),
					Knowledges.Animations.subtitle(),
					Paragraph.Alignment.RIGHT,
					Palette.Minecraft.WHITE.opacity(0.11)
			);
		}
	}

	private void renderText(DrawContext context, Box box, Text text, Paragraph.Alignment alignment, AccurateColor color) {
		box.render(context, flat ->
				flat.new Text(section -> section.fontSize(0.9 * scalar()).append(text))
						.verticalAlignment(Section.Alignment.CENTER)
						.horizontalAlignment(alignment)
						.color(color)
		);
	}

	public abstract String id();

	@Override
	public @NotNull Text name() {
		return Knowledges.localize("knowledge", "info", id(), "name");
	}

	@Override
	public @Nullable Text tooltip() {
		return Knowledges.localize("knowledge", "info", id(), "tooltip");
	}
}
