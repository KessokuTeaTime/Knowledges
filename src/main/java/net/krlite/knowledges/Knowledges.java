package net.krlite.knowledges;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
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
import net.krlite.knowledges.api.KnowledgeContainer;
import net.krlite.knowledges.config.KnowledgesBanList;
import net.krlite.knowledges.config.KnowledgesConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class Knowledges implements ModInitializer {
	public static final String NAME = "Knowledges", ID = "knowledges";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	public static final KnowledgesConfig CONFIG = new KnowledgesConfig();
	private static final KnowledgesBanList banList = new KnowledgesBanList();
	private static final ArrayList<Knowledge> knowledges = new ArrayList<>();
	private static int knowledgesCount = 0;

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

		private static MutableText lastText = LiteralText.EMPTY.copy(), lastShownText = LiteralText.EMPTY.copy(), text = LiteralText.EMPTY.copy();

		public static MutableText text() {
			if (!lastShownText.equals(text)) lastText = lastShownText;
			int
					currentLength = text.getString().length(),
					truncatedLength = Math.min(textLengthInt(), lastText.getString().length());

			MutableText value = new LiteralText(
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
			return ringColor.value().opacity(0.5 * mapToPower(ringRadians() / (2 * Math.PI), 2, 0.15));
		}

		public static AccurateColor ovalColor() {
			return ovalColor.value().opacity(0.075 * ovalOpacity());
		}

		public static void ringColor(AccurateColor color) {
			ringColor.target(color);
		}

		public static void ovalColor(AccurateColor color) {
			ovalColor.target(color);
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

	public static int knowledgesCount() {
		return knowledgesCount;
	}

	public static ArrayList<Knowledge> knowledges() {
		return new ArrayList<>(knowledges);
	}

	public static Text localize(String category, String... paths) {
		return new TranslatableText(category + "." + ID + "." + String.join(".", paths));
	}

	private static void register(Knowledge knowledge) {
		knowledges.add(knowledge);
	}

	public static boolean knowledgeState(Knowledge knowledge) {
		return !banList.isBanned(knowledge.name());
	}

	public static void knowledgeState(Knowledge knowledge, boolean state) {
		banList.setBanned(knowledge.name(), !state);
	}

	@Override
	public void onInitialize() {
		Animations.registerEvents();

		LOGGER.info("Initializing components for " + NAME + "...");

		FabricLoader.getInstance().getEntrypointContainers("knowledges", KnowledgeContainer.class).forEach(entrypoint -> {
			KnowledgeContainer container = entrypoint.getEntrypoint();
			container.register().forEach(Knowledges::register);

			knowledgesCount += container.register().size();
		});

		LOGGER.info("Successfully registered " + knowledgesCount + " knowledge. You're now full of knowledge! 📚");
	}

	public static void render(
			@NotNull MatrixStack matrixStack, @NotNull MinecraftClient client,
			@NotNull PlayerEntity player, @NotNull ClientWorld world
	) {
		knowledges.forEach(knowledge -> {
			if (!banList.isBanned(knowledge.name()))
				knowledge.render(matrixStack, client, player, world);
		});
	}

	public static double mapToPower(double x, double power, double threshold) {
		return threshold + (1 - threshold) * Math.pow(x, power);
	}
}
