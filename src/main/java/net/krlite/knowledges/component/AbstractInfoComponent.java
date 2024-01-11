package net.krlite.knowledges.component;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.krlite.equator.input.Mouse;
import net.krlite.equator.math.algebra.Curves;
import net.krlite.equator.math.algebra.Theory;
import net.krlite.equator.math.geometry.flat.Box;
import net.krlite.equator.render.frame.FrameInfo;
import net.krlite.equator.visual.animation.animated.AnimatedDouble;
import net.krlite.equator.visual.animation.base.Interpolation;
import net.krlite.equator.visual.animation.interpolated.InterpolatedColor;
import net.krlite.equator.visual.animation.interpolated.InterpolatedDouble;
import net.krlite.equator.visual.color.AccurateColor;
import net.krlite.equator.visual.color.Palette;
import net.krlite.equator.visual.color.base.ColorStandard;
import net.krlite.equator.visual.text.Paragraph;
import net.krlite.equator.visual.text.Section;
import net.krlite.knowledges.core.path.WithPartialPath;
import net.krlite.knowledges.core.util.Helper;
import net.krlite.knowledges.core.animation.InterpolatedText;
import net.krlite.knowledges.api.Knowledge;
import net.krlite.knowledges.Knowledges;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class AbstractInfoComponent implements Knowledge, WithPartialPath {
	@Override
	public @NotNull String currentPath() {
		return "info";
	}

	public static class Animations {
		public static class Texts {
			private static final InterpolatedText
					TITLE_RIGHT = new InterpolatedText(InterpolatedText.Alignment.LEFT),
					TITLE_LEFT = new InterpolatedText(InterpolatedText.Alignment.RIGHT);

			public static MutableText titleRight() {
				return TITLE_RIGHT.text();
			}

			public static void titleRight(Text text) {
				TITLE_RIGHT.text(text);
			}

			public static MutableText titleLeft() {
				return TITLE_LEFT.text();
			}

			public static void titleLeft(Text text) {
				TITLE_LEFT.text(text);
			}



			private static final InterpolatedText
					SUBTITLE_RIGHT_ABOVE = new InterpolatedText(InterpolatedText.Alignment.LEFT),
					SUBTITLE_LEFT_ABOVE = new InterpolatedText(InterpolatedText.Alignment.RIGHT);

			public static MutableText subtitleRightAbove() {
				return SUBTITLE_RIGHT_ABOVE.text();
			}

			public static void subtitleRightAbove(Text text) {
				SUBTITLE_RIGHT_ABOVE.text(text);
			}

			public static MutableText subtitleLeftAbove() {
				return SUBTITLE_LEFT_ABOVE.text();
			}

			public static void subtitleLeftAbove(Text text) {
				SUBTITLE_LEFT_ABOVE.text(text);
			}



			private static final InterpolatedText
					SUBTITLE_RIGHT_BELOW = new InterpolatedText(InterpolatedText.Alignment.LEFT),
					SUBTITLE_LEFT_BELOW = new InterpolatedText(InterpolatedText.Alignment.RIGHT);

			public static MutableText subtitleRightBelow() {
				return SUBTITLE_RIGHT_BELOW.text();
			}

			public static void subtitleRightBelow(Text text) {
				SUBTITLE_RIGHT_BELOW.text(text);
			}

			public static MutableText subtitleLeftBelow() {
				return SUBTITLE_LEFT_BELOW.text();
			}

			public static void subtitleLeftBelow(Text text) {
				SUBTITLE_LEFT_BELOW.text(text);
			}
		}

		public static class Ring {
			private static final InterpolatedDouble BLOCK_BREAKING_PROGRESS = new InterpolatedDouble(0, 0.004);

			public static double blockBreakingProgress() {
				return BLOCK_BREAKING_PROGRESS.value();
			}

			public static void blockBreakingProgress(double progress, boolean reset) {
				progress = Theory.clamp(progress, 0, 1);
				BLOCK_BREAKING_PROGRESS.target(progress);
				if (reset) BLOCK_BREAKING_PROGRESS.reset(progress);
			}

			public static void blockBreakingProgress(double progress) {
				blockBreakingProgress(progress, false);
			}



			private static final InterpolatedDouble RING_RADIANS = new InterpolatedDouble(0, 0.035);

			private static final Interpolation<AccurateColor>
					RING_COLOR = new InterpolatedColor(Palette.TRANSPARENT, 0.009, ColorStandard.MixMode.PIGMENT),
					OVAL_COLOR = new InterpolatedColor(Palette.TRANSPARENT, 0.009, ColorStandard.MixMode.PIGMENT);

			public static double ringRadians() {
				return RING_RADIANS.value();
			}

			public static void ringRadians(double radians, boolean reset) {
				radians = Theory.mod(radians, 2 * Math.PI);
				RING_RADIANS.target(radians);
				if (reset) RING_RADIANS.reset(radians);
			}

			public static void ringRadians(double radians) {
				ringRadians(radians, false);
			}

			public static AccurateColor ringColor() {
				return RING_COLOR.value().opacity(0.5 * Helper.Math.mapToPower(ringRadians() / (2 * Math.PI), 2, 0.15));
			}

			public static void ringColor(AccurateColor color) {
				RING_COLOR.target(color);
			}

			public static AccurateColor ovalColor() {
				return OVAL_COLOR.value().opacity(0.075 * ovalOpacity());
			}

			public static void ovalColor(AccurateColor color) {
				OVAL_COLOR.target(color);
			}



			private static final InterpolatedDouble OVAL_OPACITY = new InterpolatedDouble(0, 0.013);
			private static final InterpolatedDouble MOUSE_HOLDING = new InterpolatedDouble(0, 0.02);
			private static final AnimatedDouble FOCUSING_BLOCK = new AnimatedDouble(1.6, 1, 150, Curves.Sinusoidal.EASE);


			public static double ovalOpacity() {
				return OVAL_OPACITY.value();
			}

			public static double mouseHolding() {
				return MOUSE_HOLDING.value();
			}

			public static double focusingBlock() {
				return FOCUSING_BLOCK.value();
			}
		}



		static {
			Ring.FOCUSING_BLOCK.speedDirection(false);

			Mouse.Callbacks.Click.EVENT.register((button, action, modifiers) -> {
				Ring.MOUSE_HOLDING.target(action.isPress() ? 1D : 0D);
			});
		}

		public static void registerEvents() {
			ClientTickEvents.END_CLIENT_TICK.register(client -> {
				if (client != null) {
					Ring.OVAL_OPACITY.target(Info.hasCrosshairTarget() ? 1D : 0D);

					if (Ring.FOCUSING_BLOCK.isPositive() != Info.hasCrosshairTarget()) {
						Ring.FOCUSING_BLOCK.speedDirection(Info.hasCrosshairTarget());
						Ring.FOCUSING_BLOCK.play();
					}
				}
			});
		}
	}
}
