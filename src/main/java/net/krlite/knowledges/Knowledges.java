package net.krlite.knowledges;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.krlite.equator.math.algebra.Theory;
import net.krlite.equator.math.geometry.flat.Vector;
import net.krlite.equator.math.geometry.volume.Pos;
import net.krlite.equator.render.frame.FrameInfo;
import net.krlite.equator.visual.animation.Interpolation;
import net.krlite.knowledges.components.ArmorDurabilityComponent;
import net.krlite.knowledges.components.BlockInfoComponent;
import net.krlite.knowledges.config.KnowledgesConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class Knowledges implements ModInitializer {
	public static final String NAME = "Knowledges", ID = "knowledges";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	public static final KnowledgesConfig CONFIG = new KnowledgesConfig();
	public static final ArrayList<Knowledge> KNOWLEDGES = new ArrayList<>();

	public static class Constants {
		public static double MAX_TEXT_SHIFT = 20, MIN_TEXT_SHIFT = 0;

		public static double MAX_TEXT_SHRINK = 1, MIN_TEXT_SHRINK = 0;
	}

	public static class Animations {
		private static final Interpolation sprinting = new Interpolation(0, 0, 130);

		public static double sprintShift() {
			return FrameInfo.scaled().w() / 2 * 0.3 * sprinting.value();
		}

		public static double sprintShrink() {
			return 1 - 0.27 * sprinting.value();
		}

		private static boolean initialized = false;

		private static final Interpolation
				yaw = new Interpolation(0, 0, 200),
				pitch = new Interpolation(0, 0, 200);

		private static Vector rotation(PlayerEntity player) {
			return Vector.fromCartesian(player.getYaw(), player.getPitch());
		}

		public static Vector rotationDifference() {
			PlayerEntity player = MinecraftClient.getInstance().player;
			if (player == null) return Vector.ZERO;

			Vector diff = Vector.fromCartesian(
					rotation(player).x() - yaw.value(),
					rotation(player).y() - pitch.value()
			);

			return diff.magnitude(3 * mapToExponential(diff.magnitude(), 1, 0.1));
		}

		private static final Interpolation
				x = new Interpolation(0, 0, 200),
				y = new Interpolation(0, 0, 125),
				z = new Interpolation(0, 0, 200);

		private static Pos position(PlayerEntity player) {
			return new Pos(player.getEyePos().getX(), player.getEyePos().getY(), player.getEyePos().getZ());
		}

		public static Pos positionDifference() {
			PlayerEntity player = MinecraftClient.getInstance().player;
			if (player == null) return Pos.ZERO;

			Pos diff = position(player).subtract(
					x.value(),
					y.value(),
					z.value()
			);

			return projectOntoXYPlane(diff, rotation(player))
						   .magnitude(5 * mapToExponential(diff.magnitude(), 1, 1));
		}

		public static Vector positionDifferenceOnXYPlane() {
			Vector diff = Vector.fromCartesian(-positionDifference().x(), -positionDifference().y());
			return diff.magnitude(3 * mapToExponential(diff.magnitude(), 1, 1));
		}

		public static double positionDifferenceOnYAxis() {
			double diff = -positionDifference().z();
			return 0.5 * mapToExponential(diff, 1, 0.15);
		}

		static void registerEvents() {
			ClientTickEvents.END_CLIENT_TICK.register(client -> {
				if (client.player != null) {
					sprinting.targetValue(client.player.isSprinting() ? 1 : 0);

					if (!initialized) {
						// Rotation
						yaw.originValue(rotation(client.player).x());
						yaw.reset();

						pitch.originValue(rotation(client.player).y());
						pitch.reset();

						// Position
						x.originValue(client.player.getEyePos().getX());
						x.reset();

						y.originValue(client.player.getEyePos().getY());
						y.reset();

						z.originValue(client.player.getEyePos().getZ());
						z.reset();

						initialized = true;
					}

					yaw.targetValue(rotation(client.player).x());
					pitch.targetValue(rotation(client.player).y());

					x.targetValue(client.player.getEyePos().getX());
					y.targetValue(client.player.getEyePos().getY());
					z.targetValue(client.player.getEyePos().getZ());
				}
			});
		}
	}

	@Override
	public void onInitialize() {
		Animations.registerEvents();

		LOGGER.info("Initializing default components for " + NAME + "...");
		KNOWLEDGES.add(new BlockInfoComponent());
		KNOWLEDGES.add(new ArmorDurabilityComponent());
		LOGGER.info("Finished initializing default components for " + NAME + ".");

		// TODO: Implement more components

		LOGGER.info("You're now full of knowledge! 📚");
	}

	public static void renderKnowledges(MatrixStack matrixStack) {
		KNOWLEDGES.forEach(knowledge -> knowledge.render(matrixStack));
	}

	public static double mapToExponential(double x, double yMax, double k) {
		if (Theory.looseEquals(x, 0)) return 0;
		return x > 0 ? (yMax * (1 - Math.exp(-k * x))) : (-yMax * (1 - Math.exp(k * x)));
	}

	public static double mapToPower(double x, double power, double threshold) {
		return threshold + (1 - threshold) * Math.pow(x, power);
	}

	public static Pos projectOntoXYPlane(Pos pos, Vector rotation /* x: yaw, y: pitch */ ) {
		return pos
					   .rotate(new Quaterniond(new Quaternionf().rotationY(
							   (float) Math.toRadians(rotation.x()))))
					   .rotate(new Quaterniond(new Quaternionf().rotationX(
							   (float) Math.toRadians(-rotation.y()))));
	}
}
