package net.krlite.knowledges;

import net.krlite.equator.math.geometry.flat.Box;
import net.krlite.equator.math.geometry.flat.Vector;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Knowledge {
	void render(@NotNull MatrixStack matrixStack, @NotNull MinecraftClient client, @NotNull PlayerEntity player, @NotNull ClientWorld world);

	@NotNull Text name();

	default @Nullable Text tooltip() {
		return null;
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
		public static BlockState crosshairBlock() {
			MinecraftClient client = MinecraftClient.getInstance();
			if (client.world == null || client.player == null) return null;

			HitResult hitResult = crosshairTarget();
			return hitResult != null && hitResult.getType() == HitResult.Type.BLOCK ? client.world.getBlockState(((BlockHitResult) hitResult).getBlockPos()) : null;
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
