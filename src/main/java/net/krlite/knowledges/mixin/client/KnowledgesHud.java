package net.krlite.knowledges.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.krlite.equator.render.frame.FrameInfo;
import net.krlite.knowledges.Knowledge;
import net.krlite.knowledges.Knowledges;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class KnowledgesHud {
	@Inject(method = "render", at = @At(value = "TAIL"))
	private void injectKnowledge(MatrixStack matrixStack, float tickDelta, CallbackInfo ci) {
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.enableBlend();

		matrixStack.push();
		matrixStack.translate(FrameInfo.scaled().w() / 2, FrameInfo.scaled().h() / 2, 0);

		render: {
			MinecraftClient client = MinecraftClient.getInstance();
			if (client.world == null || client.player == null) break render;

			if (client.options.getPerspective().isFirstPerson()
						&& ((client.interactionManager != null && client.interactionManager.getCurrentGameMode() != GameMode.SPECTATOR)
									|| ((InGameHudInvoker) client.inGameHud).invokeShouldRenderSpectatorCrosshair(Knowledge.Info.crosshairTarget()))
						&& !(client.options.debugEnabled && !client.options.hudHidden && !client.player.hasReducedDebugInfo() && !client.options.reducedDebugInfo)
			) Knowledges.render(matrixStack, client, client.player, client.world);
		}

		matrixStack.pop();

		RenderSystem.disableBlend();
	}
}
