package net.krlite.knowledges.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.krlite.knowledges.Knowledges;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class KnowledgesHud {
	@Inject(method = "render", at = @At(value = "TAIL"))
	private void injectKnowledge(MatrixStack matrixStack, float tickDelta, CallbackInfo ci) {
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShader(GameRenderer::getPositionTexProgram);
		RenderSystem.enableBlend();
		Knowledges.renderKnowledges(matrixStack);
		RenderSystem.disableBlend();
	}
}
