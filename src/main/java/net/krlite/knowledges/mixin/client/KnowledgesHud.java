package net.krlite.knowledges.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.krlite.equator.render.frame.FrameInfo;
import net.krlite.knowledges.api.Knowledge;
import net.krlite.knowledges.Knowledges;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class KnowledgesHud {
	@Inject(method = "render", at = @At(value = "TAIL"))
	private void injectKnowledge(DrawContext context, float tickDelta, CallbackInfo ci) {
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShader(GameRenderer::getPositionTexProgram);
		RenderSystem.enableBlend();

		context.getMatrices().push();
		context.getMatrices().translate(FrameInfo.scaled().w() / 2, FrameInfo.scaled().h() / 2, 0);

		render: {
			MinecraftClient client = MinecraftClient.getInstance();
			if (client.world == null || client.player == null) break render;

			boolean isFirstPerson = client.options.getPerspective().isFirstPerson();
			boolean isSpectator = client.interactionManager != null && client.interactionManager.getCurrentGameMode() == GameMode.SPECTATOR;
			boolean shouldRenderSpectatorCrosshair = Knowledge.Info.crosshairTarget()
					.map(hitResult -> ((InGameHudInvoker) client.inGameHud).invokeShouldRenderSpectatorCrosshair(hitResult))
					.orElse(false);
			boolean isInDebugHud = client.options.debugEnabled
					&& !client.options.hudHidden
					&& !client.player.hasReducedDebugInfo()
					&& !client.options.getReducedDebugInfo().getValue();

			if (isFirstPerson
						&& (!isSpectator || shouldRenderSpectatorCrosshair)
						&& (Knowledges.CONFIG.global.visibleInDebugHud || !isInDebugHud)
			) {
				Knowledges.render(context, client, client.player, client.world);
			}
		}

		context.getMatrices().pop();

		RenderSystem.disableBlend();
	}
}
