package net.krlite.knowledges;

import net.fabricmc.api.ModInitializer;
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
import net.krlite.knowledges.components.ArmorDurabilityComponent;
import net.krlite.knowledges.components.info.BlockInfoComponent;
import net.krlite.knowledges.components.CrosshairComponent;
import net.krlite.knowledges.components.info.EntityInfoComponent;
import net.krlite.knowledges.config.KnowledgesConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class Knowledges implements ModInitializer {
	public static final String NAME = "Knowledges", ID = "knowledges";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	public static final KnowledgesConfig CONFIG = new KnowledgesConfig();
	public static final ArrayList<Knowledge> KNOWLEDGES = new ArrayList<>();

	public static class Constants {
	}

	public static class Animations {
		private static final InterpolatedDouble textLength = new InterpolatedDouble(0, 0.01);

		public static double textLength() {
			return textLength.value();
		}

		public static int textLengthInt() {
			return (int) Math.round(textLength());
		}

		public static void textLength(long length) {
			textLength.target((double) length);
		}

		private static MutableText lastText = Text.empty(), lastShownText = Text.empty(), text = Text.empty();

		public static MutableText text() {
			if (!lastShownText.equals(text)) lastText = lastShownText;
			int
					currentLength = text.getString().length(),
					truncatedLength = Math.min(textLengthInt(), lastText.getString().length());

			MutableText value = Text.literal(
					text.asTruncatedString(textLengthInt())
							+ (currentLength < truncatedLength
									   ? lastText.getString().substring(currentLength, truncatedLength)
									   : "")
			).setStyle(text.getStyle());

			lastShownText = text;
			return value;
		}

		public static void text(MutableText text) {
			Animations.text = text;
		}

		private static final InterpolatedDouble
				ovalOpacity = new InterpolatedDouble(0, 0.013),
				mouseHolding = new InterpolatedDouble(0, 0.02);

		private static final AnimatedDouble focusingBlock = new AnimatedDouble(1.6, 1, 150, Curves.Sinusoidal.EASE);

		static {
			focusingBlock.speedDirection(false);

			Mouse.Callbacks.Click.EVENT.register((button, action, modifiers) -> {
				mouseHolding.target(action.isPress() ? 1D : 0D);
			});
		}

		public static double ovalOpacity() {
			return ovalOpacity.value();
		}

		public static double mouseHolding() {
			return mouseHolding.value();
		}

		public static double focusingBlock() {
			return focusingBlock.value();
		}

		private static final InterpolatedDouble ringRadians = new InterpolatedDouble(0, 0.35);

		public static double ringRadians() {
			return ringRadians.value();
		}

		public static void ringRadians(double radians) {
			radians = Theory.mod(radians, 2 * Math.PI);
			ringRadians.target(radians);
		}

		private static final Interpolation<AccurateColor>
				ringColor = new InterpolatedColor(Palette.TRANSPARENT, 0.009, ColorStandard.MixMode.PIGMENT),
				ovalColor = new InterpolatedColor(Palette.TRANSPARENT, 0.009, ColorStandard.MixMode.PIGMENT);

		public static AccurateColor ringColor() {
			return ringColor.value();
		}

		public static AccurateColor ovalColor() {
			return ovalColor.value();
		}

		public static void ringColor(AccurateColor color) {
			ringColor.target(color.opacity(0.5 * mapToPower(ringRadians() / (2 * Math.PI), 2, 0.15)));
		}

		public static void ovalColor(AccurateColor color) {
			ovalColor.target(color.opacity(0.075 * ovalOpacity()));
		}

		static void registerEvents() {
			ClientTickEvents.END_CLIENT_TICK.register(client -> {
				if (client != null) {
					ovalOpacity.target(Knowledge.Info.hasCrosshairTarget() ? 1D : 0D);

					if (focusingBlock.isPositive() != Knowledge.Info.hasCrosshairTarget()) {
						focusingBlock.speedDirection(Knowledge.Info.hasCrosshairTarget());
						focusingBlock.play();
					}
				}
			});
		}
	}

	@Override
	public void onInitialize() {
		Animations.registerEvents();

		LOGGER.info("Initializing default components for " + NAME + "...");
		KNOWLEDGES.add(new CrosshairComponent());

		KNOWLEDGES.add(new BlockInfoComponent());
		KNOWLEDGES.add(new EntityInfoComponent());

		KNOWLEDGES.add(new ArmorDurabilityComponent());
		LOGGER.info("Finished initializing default components for " + NAME + ".");

		// TODO: Implement more components

		LOGGER.info("You're now full of knowledge! 📚");
	}

	public static void render(
			@NotNull MatrixStack matrixStack, @NotNull MinecraftClient client,
			@NotNull PlayerEntity player, @NotNull ClientWorld world
	) {
		KNOWLEDGES.forEach(knowledge -> knowledge.render(matrixStack, client, player, world));
	}

	public static double mapToPower(double x, double power, double threshold) {
		return threshold + (1 - threshold) * Math.pow(x, power);
	}
}
