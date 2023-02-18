package net.krlite.knowledges.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.krlite.knowledges.KnowledgeFlipper;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;
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
			PlayerEntity player = getCameraPlayer();
			ItemStack mainHand = player.getMainHandStack(), offHand = player.getOffHandStack();
			ItemStack head = player.getEquippedStack(EquipmentSlot.HEAD), chest = player.getEquippedStack(EquipmentSlot.CHEST), LEGS = player.getEquippedStack(EquipmentSlot.LEGS), feet = player.getEquippedStack(EquipmentSlot.FEET);

			KnowledgeFlipper.pushTemporary(mainHand);

			if (head.isEmpty()) KnowledgeFlipper.clearPermanent();
			else KnowledgeFlipper.setPermanent(head);

			HitResult hitResult = client.crosshairTarget;
			@Nullable BlockState block = hitResult != null && hitResult.getType() == HitResult.Type.BLOCK ? client.world.getBlockState(((BlockHitResult) hitResult).getBlockPos()) : null;
			@Nullable Entity entity = hitResult != null && hitResult.getType() == HitResult.Type.ENTITY ? ((EntityHitResult) hitResult).getEntity() : null;

			KnowledgeFlipper.renderKnowledge();
		}
	}
}
