package net.krlite.knowledges;

import net.krlite.equator.math.geometry.flat.Box;
import net.krlite.equator.math.geometry.flat.Vector;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public interface Knowledge {
	void render(@NotNull DrawContext context, @NotNull MinecraftClient client, @NotNull PlayerEntity player, @NotNull ClientWorld world);

	@NotNull String id();

	boolean provideTooltip();

	default @NotNull Text name() {
		return localize("name");
	};

	default @Nullable Text tooltip() {
		return provideTooltip() ? localize("tooltip") : null;
	}

	default String localizationKey(String... paths) {
		List<String> fullPaths = new ArrayList<>(List.of(id()));
		fullPaths.addAll(List.of(paths));

		return Knowledges.localizationKey("knowledge", fullPaths.toArray(String[]::new));
	}

	default MutableText localize(String... paths) {
		return Knowledges.localize(localizationKey(paths));
	}

	default double scalar() {
		return 0.5 + 0.5 * Knowledges.CONFIG.scalar();
	}

	default Box crosshairSafeArea() {
		double size = 16 + 8 * Knowledges.CONFIG.crosshairSafeAreaSizeScalar();
		return Box.UNIT.scale(size)
					   .scale(scalar())
					   .center(Vector.ZERO)
					   .shift(0, -1);
	}

	class Info {
		public static boolean hasCrosshairTarget() {
			MinecraftClient client = MinecraftClient.getInstance();
			if (client.world == null || client.player == null) return false;
			return crosshairBlock() != null || crosshairEntity() != null;
		}

		@Nullable
		public static HitResult crosshairTarget() {
			MinecraftClient client = MinecraftClient.getInstance();
			if (client.world == null || client.player == null) return null;
			return client.crosshairTarget;
		}

		@Nullable
		public static Vec3d crosshairPos() {
			HitResult hitResult = crosshairTarget();
			return hitResult != null ? hitResult.getPos() : null;
		}

		@Nullable
		public static BlockPos crosshairBlockPos() {
			MinecraftClient client = MinecraftClient.getInstance();
			if (client.world == null || client.player == null) return null;

			HitResult hitResult = crosshairTarget();
			return hitResult != null && hitResult.getType() == HitResult.Type.BLOCK ? ((BlockHitResult) hitResult).getBlockPos() : null;
		}

		@Nullable
		public static BlockState crosshairBlock() {
			MinecraftClient client = MinecraftClient.getInstance();
			if (client.world == null || client.player == null) return null;

			HitResult hitResult = crosshairTarget();
			return hitResult != null && hitResult.getType() == HitResult.Type.BLOCK ? client.world.getBlockState(crosshairBlockPos()) : null;
		}

		@Nullable
		public static Entity crosshairEntity() {
			MinecraftClient client = MinecraftClient.getInstance();
			if (client.world == null || client.player == null) return null;

			HitResult hitResult = crosshairTarget();
			return hitResult != null && hitResult.getType() == HitResult.Type.ENTITY && hitResult instanceof EntityHitResult ? ((EntityHitResult) hitResult).getEntity() : null;
		}
	}
}
