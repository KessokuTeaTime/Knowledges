package net.krlite.knowledges.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.krlite.equator.util.Timer;
import net.krlite.knowledges.KnowledgeFlipper;
import net.krlite.knowledges.Knowledges;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class KnowledgesHud {

	@Shadow protected abstract PlayerEntity getCameraPlayer();

	@Shadow protected abstract boolean shouldRenderSpectatorCrosshair(HitResult hitResult);

	@Shadow @Final private MinecraftClient client;

	@Inject(method = "render", at = @At(value = "TAIL"))
	private void injectKnowledge(MatrixStack matrixStack, float tickDelta, CallbackInfo ci) {
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShader(GameRenderer::getPositionTexProgram);
		RenderSystem.enableBlend();
		renderKnowledge();
		RenderSystem.disableBlend();
	}

	private void renderKnowledge() {
		if (this.client.options.getPerspective().isFirstPerson() &&
					(this.client.interactionManager.getCurrentGameMode() != GameMode.SPECTATOR || this.shouldRenderSpectatorCrosshair(this.client.crosshairTarget)) &&
					!(this.client.options.debugEnabled && !this.client.options.hudHidden && !this.client.player.hasReducedDebugInfo() && !this.client.options.getReducedDebugInfo().getValue())
		) {
			if (this.client.options.playerListKey.wasPressed()) {
				KnowledgeFlipper.pushItem(getCameraPlayer().getMainHandStack(), new Timer(500));
			}
			KnowledgeFlipper.renderKnowledge();
		}
	}
}
