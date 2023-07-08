package net.krlite.knowledges.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.krlite.equator.math.geometry.flat.Box;
import net.krlite.equator.math.geometry.flat.Vector;
import net.krlite.equator.render.frame.FrameInfo;
import net.krlite.equator.render.renderer.Flat;
import net.krlite.equator.visual.color.AccurateColor;
import net.krlite.equator.visual.color.Palette;
import net.krlite.equator.visual.text.Paragraph;
import net.krlite.equator.visual.text.Section;
import net.krlite.knowledges.Knowledges;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
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
		renderKnowledge(matrixStack);
		RenderSystem.disableBlend();
	}

	@Unique
	private void renderKnowledge(MatrixStack matrixStack) {
		if (this.client.player == null || this.client.world == null) {
			return;
		}

		if (this.client.options.getPerspective().isFirstPerson() &&
					((this.client.interactionManager != null && this.client.interactionManager.getCurrentGameMode() != GameMode.SPECTATOR) || this.shouldRenderSpectatorCrosshair(this.client.crosshairTarget)) &&
					!(this.client.options.debugEnabled && !this.client.options.hudHidden && !this.client.player.hasReducedDebugInfo() && !this.client.options.getReducedDebugInfo().getValue())
		) {
			PlayerEntity player = getCameraPlayer();
			ItemStack mainHand = player.getMainHandStack(), offHand = player.getOffHandStack();
			ItemStack head = player.getEquippedStack(EquipmentSlot.HEAD), chest = player.getEquippedStack(EquipmentSlot.CHEST), legs = player.getEquippedStack(EquipmentSlot.LEGS), feet = player.getEquippedStack(EquipmentSlot.FEET);

			HitResult hitResult = client.crosshairTarget;
			@Nullable BlockState block = hitResult != null && hitResult.getType() == HitResult.Type.BLOCK ? client.world.getBlockState(((BlockHitResult) hitResult).getBlockPos()) : null;
			@Nullable Entity entity = hitResult != null && hitResult.getType() == HitResult.Type.ENTITY && hitResult instanceof EntityHitResult ? ((EntityHitResult) hitResult).getEntity() : null;

			{
				if (block != null && block.getBlock() != null) {
					new Box(Vector.fromCartesian(8, 8))
							.bottomCenter(FrameInfo.scaled().center())
							.render(matrixStack, 100,
									flat -> flat.new Model(
											block.getBlock().getDefaultState(),
											new Quaternionf().rotateX((float) Math.toRadians(player.getPitch())).rotateY((float) Math.toRadians(player.getYaw()))
									)
					);
				}
			}

			{
				matrixStack.push();
				matrixStack.translate(FrameInfo.scaled().w() / 2, FrameInfo.scaled().h() / 2, 500);
				matrixStack.scale(0.7F, 0.7F, 0.7F);

				matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-6));
				matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(19));
				matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-7));

				Box box = FrameInfo.scaled().leftCenter(Vector.ZERO);

				box.render(matrixStack,
						flat -> flat.new Rectangle().colors(AccurateColor.MAGENTA)
										.new Outlined(Vector.UNIT_SQUARE).outliningMode(Flat.Rectangle.Outlined.OutliningMode.SCISSORED)
				);

				if (block != null && block.getBlock() != null) {
					box.shift(24, -8).render(matrixStack,
							flat -> flat.new Text(builder -> builder.append(block.getBlock().getName()).fontSize(1.2))
											.withoutShadow()
											.color(AccurateColor.WHITE.opacity(0.5))
											.horizontalAlignment(Paragraph.Alignment.LEFT)
											.verticalAlignment(Section.Alignment.CENTER)
					);
				}

				matrixStack.pop();
			}
		}
	}
}
