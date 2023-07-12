package net.krlite.knowledges;

import net.krlite.equator.math.geometry.flat.Box;
import net.krlite.equator.math.geometry.flat.Vector;
import net.krlite.equator.render.frame.FrameInfo;
import net.krlite.knowledges.mixin.client.InGameHudInvoker;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Knowledge {
	void render(MatrixStack matrixStack, @NotNull MinecraftClient client, @NotNull PlayerEntity player, @NotNull ClientWorld world);

	default void render(MatrixStack matrixStack) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.world == null || client.player == null) return;

		if (client.options.getPerspective().isFirstPerson()
					&& ((client.interactionManager != null && client.interactionManager.getCurrentGameMode() != GameMode.SPECTATOR)
								|| ((InGameHudInvoker) client.inGameHud).invokeShouldRenderSpectatorCrosshair(crosshairTarget()))
					&& !(client.options.debugEnabled && !client.options.hudHidden && !client.player.hasReducedDebugInfo() && !client.options.getReducedDebugInfo().getValue())
		) {
			matrixStack.push();
			matrixStack.translate(FrameInfo.scaled().w() / 2, FrameInfo.scaled().h() / 2, 0);
			render(matrixStack, client, client.player, client.world);
			matrixStack.pop();
		}
	}

	default double scalar() {
		return Knowledges.CONFIG.scalar();
	}

	default Box crosshairSafeArea() {
		double size = 24 * Knowledges.CONFIG.crosshairSafeAreaSizeScalar();
		return new Box(Vector.UNIT.scale(size))
					   .center(Vector.ZERO)
					   .shift(0, -1);
	}

	default boolean hasCrosshairTarget() {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.world == null || client.player == null) return false;
		return crosshairBlock() != null || crosshairEntity() != null;
	}

	@Nullable
	default HitResult crosshairTarget() {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.world == null || client.player == null) return null;
		return client.crosshairTarget;
	}

	@Nullable
	default BlockState crosshairBlock() {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.world == null || client.player == null) return null;

		HitResult hitResult = crosshairTarget();
		return hitResult != null && hitResult.getType() == HitResult.Type.BLOCK ? client.world.getBlockState(((BlockHitResult) hitResult).getBlockPos()) : null;
	}

	@Nullable
	default Entity crosshairEntity() {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.world == null || client.player == null) return null;

		HitResult hitResult = crosshairTarget();
		return hitResult != null && hitResult.getType() == HitResult.Type.ENTITY && hitResult instanceof EntityHitResult ? ((EntityHitResult) hitResult).getEntity() : null;
	}
}
