package net.krlite.knowledges.components;

import net.krlite.equator.visual.animation.interpolated.InterpolatedDouble;
import net.krlite.knowledges.Knowledge;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockInfoComponent implements Knowledge {
	private final InterpolatedDouble
			textShifting = new InterpolatedDouble(0, 0.013),
			textShrinking = new InterpolatedDouble(0, 0.041);

	@Override
	public void render(MatrixStack matrixStack, @NotNull MinecraftClient client, @NotNull PlayerEntity player, @NotNull ClientWorld world) {
		@Nullable BlockState blockState = crosshairBlock();
	}
}
