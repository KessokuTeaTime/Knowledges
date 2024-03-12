package net.krlite.knowledges.impl.component.base;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.krlite.equator.input.Mouse;
import net.krlite.equator.math.algebra.Curves;
import net.krlite.equator.math.algebra.Theory;
import net.krlite.equator.visual.animation.animated.AnimatedDouble;
import net.krlite.equator.visual.animation.base.Interpolation;
import net.krlite.equator.visual.animation.interpolated.InterpolatedColor;
import net.krlite.equator.visual.animation.interpolated.InterpolatedDouble;
import net.krlite.equator.visual.color.AccurateColor;
import net.krlite.equator.visual.color.Palette;
import net.krlite.equator.visual.color.base.ColorStandard;
import net.krlite.knowledges.api.core.path.WithPartialPath;
import net.krlite.knowledges.Util;
import net.krlite.knowledges.animation.InterpolatedText;
import net.krlite.knowledges.api.component.Knowledge;
import net.krlite.knowledges.api.proxy.ModProxy;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.NotNull;

public abstract class InfoComponent implements Knowledge, WithPartialPath {
	@Override
	public @NotNull String currentPath() {
		return "info";
	}

	public static class Animation {
		public static class Text {
			private static final InterpolatedText
					titleRight = new InterpolatedText(InterpolatedText.Alignment.LEFT),
					titleLeft = new InterpolatedText(InterpolatedText.Alignment.RIGHT);

			public static MutableText titleRight() {
				return titleRight.text();
			}

			public static void titleRight(net.minecraft.text.Text text) {
				titleRight.text(text);
			}

			public static MutableText titleLeft() {
				return titleLeft.text();
			}

			public static void titleLeft(net.minecraft.text.Text text) {
				titleLeft.text(text);
			}



			private static final InterpolatedText
					subtitleRightAbove = new InterpolatedText(InterpolatedText.Alignment.LEFT),
					subtitleLeftAbove = new InterpolatedText(InterpolatedText.Alignment.RIGHT);

			public static MutableText subtitleRightAbove() {
				return subtitleRightAbove.text();
			}

			public static void subtitleRightAbove(net.minecraft.text.Text text) {
				subtitleRightAbove.text(text);
			}

			public static MutableText subtitleLeftAbove() {
				return subtitleLeftAbove.text();
			}

			public static void subtitleLeftAbove(net.minecraft.text.Text text) {
				subtitleLeftAbove.text(text);
			}



			private static final InterpolatedText
					subtitleRightBelow = new InterpolatedText(InterpolatedText.Alignment.LEFT),
					subtitleLeftBelow = new InterpolatedText(InterpolatedText.Alignment.RIGHT);

			public static MutableText subtitleRightBelow() {
				return subtitleRightBelow.text();
			}

			public static void subtitleRightBelow(net.minecraft.text.Text text) {
				subtitleRightBelow.text(text);
			}

			public static MutableText subtitleLeftBelow() {
				return subtitleLeftBelow.text();
			}

			public static void subtitleLeftBelow(net.minecraft.text.Text text) {
				subtitleLeftBelow.text(text);
			}



			private static final InterpolatedText numericHealth = new InterpolatedText(InterpolatedText.Alignment.LEFT);

			public static MutableText numericHealth() {
				return numericHealth.text();
			}

			public static void numericHealth(float health) {
				numericHealth.text(net.minecraft.text.Text.literal(String.format("%.1f", health)));
			}

			public static void clearNumericHealth() {
				numericHealth.text(net.minecraft.text.Text.empty());
			}
		}

		public static class Ring {
			private static final InterpolatedDouble blockBreakingProgress = new InterpolatedDouble(0, 0.004);

			public static double blockBreakingProgress() {
				return blockBreakingProgress.value();
			}

			public static void blockBreakingProgress(double progress, boolean reset) {
				progress = Theory.clamp(progress, 0, 1);
				blockBreakingProgress.target(progress);
				if (reset) blockBreakingProgress.reset(progress);
			}

			public static void blockBreakingProgress(double progress) {
				blockBreakingProgress(progress, false);
			}



			private static final InterpolatedDouble ringArc = new InterpolatedDouble(0, 0.035);

			private static final Interpolation<AccurateColor>
					ringColor = new InterpolatedColor(Palette.TRANSPARENT, 0.009, ColorStandard.MixMode.PIGMENT),
					ovalColor = new InterpolatedColor(Palette.TRANSPARENT, 0.009, ColorStandard.MixMode.PIGMENT);

			public static double ringArc() {
				return ringArc.value();
			}

			public static void ringArc(double radians, boolean reset) {
				radians = Theory.mod(radians, 2 * Math.PI);
				ringArc.target(radians);
				if (reset) ringArc.reset(radians);
			}

			public static void ringArc(double radians) {
				ringArc(radians, false);
			}

			public static AccurateColor ringColor() {
				return ringColor.value().opacity(0.5 * Util.Math.mapToPower(ringArc() / (2 * Math.PI), 2, 0.15));
			}

			public static void ringColor(AccurateColor color) {
				ringColor.target(color);
			}

			public static AccurateColor ovalColor() {
				return ovalColor.value().opacity(0.075 * ovalOpacity());
			}

			public static void ovalColor(AccurateColor color) {
				ovalColor.target(color);
			}



			private static final InterpolatedDouble ovalOpacity = new InterpolatedDouble(0, 0.013);
			private static final InterpolatedDouble mouseHolding = new InterpolatedDouble(0, 0.02);
			private static final AnimatedDouble focusingBlock = new AnimatedDouble(1.6, 1, 150, Curves.Sinusoidal.EASE);


			public static double ovalOpacity() {
				return ovalOpacity.value();
			}

			public static double mouseHolding() {
				return mouseHolding.value();
			}

			public static double focusingBlock() {
				return focusingBlock.value();
			}
		}

		public static class Contextual {
			private static float rawBlockBreakingProgress = 0;
			private static boolean cancelledBlockBreaking = false;

			public static float rawBlockBreakingProgress() {
				return rawBlockBreakingProgress;
			}

			public static void rawBlockBreakingProgress(float progress) {
				rawBlockBreakingProgress = progress;
			}

			public static boolean cancelledBlockBreaking() {
				return cancelledBlockBreaking;
			}

			public static void cancelledBlockBreaking(boolean flag) {
				cancelledBlockBreaking = flag;
			}



			private static boolean entityWasNotDamaged = true;

			public static boolean entityWasNotDamaged() {
				return entityWasNotDamaged;
			}

			public static void entityWasNotDamaged(boolean flag) {
				entityWasNotDamaged = flag;
			}
		}



		static {
			Ring.focusingBlock.speedDirection(false);

			Mouse.Callbacks.Click.EVENT.register((button, action, modifiers) -> {
				Ring.mouseHolding.target(action.isPress() ? 1D : 0D);
			});
		}

		public static void registerEvents() {
			ClientTickEvents.END_CLIENT_TICK.register(client -> {
				if (client != null) {
					HitResult hitResult = MinecraftClient.getInstance().crosshairTarget;
					boolean hitResultNotAir = ModProxy.hitResultNotAir(hitResult);

					Ring.ovalOpacity.target(hitResultNotAir ? 1D : 0D);

					if (Ring.focusingBlock.isPositive() != hitResultNotAir) {
						Ring.focusingBlock.speedDirection(hitResultNotAir);
						Ring.focusingBlock.play();
					}
				}
			});
		}
	}
}
