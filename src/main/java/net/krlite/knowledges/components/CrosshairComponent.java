package net.krlite.knowledges.components;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.krlite.equator.math.geometry.flat.Box;
import net.krlite.equator.render.renderer.Flat;
import net.krlite.equator.visual.animation.interpolated.InterpolatedDouble;
import net.krlite.equator.visual.color.Palette;
import net.krlite.knowledges.Knowledge;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.NotNull;

public class CrosshairComponent implements Knowledge {
	private static final InterpolatedDouble opacity = new InterpolatedDouble(0, 0.013);

	@Override
	public void render(MatrixStack matrixStack, @NotNull MinecraftClient client, @NotNull PlayerEntity player, @NotNull ClientWorld world) {
		opacity.target(hasCrosshairTarget() ? 1D : 0D);

		RenderSystem.blendFunc(GlStateManager.SrcFactor.ONE_MINUS_DST_COLOR, GlStateManager.DstFactor.ZERO);

		crosshairSafeArea().render(matrixStack,
				flat -> flat.new Oval()
								.colorCenter(Palette.TRANSPARENT)

								.addColor(0, Palette.WHITE)

								.ovalMode(Flat.Oval.OvalMode.GRADIANT_OUT)
								.innerRadiusFactor(0.9)
		);

		RenderSystem.defaultBlendFunc();
	}
}
