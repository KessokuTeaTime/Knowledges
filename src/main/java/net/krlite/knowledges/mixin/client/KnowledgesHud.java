package net.krlite.knowledges.mixin.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.krlite.equator.math.algebra.Curves;
import net.krlite.equator.math.geometry.flat.Box;
import net.krlite.equator.math.geometry.flat.Vector;
import net.krlite.equator.render.frame.FrameInfo;
import net.krlite.equator.render.renderer.Flat;
import net.krlite.equator.visual.animation.Animation;
import net.krlite.equator.visual.animation.Interpolation;
import net.krlite.equator.visual.animation.Slice;
import net.krlite.equator.visual.color.AccurateColor;
import net.krlite.equator.visual.color.Palette;
import net.krlite.equator.visual.color.base.ColorStandard;
import net.krlite.equator.visual.text.Paragraph;
import net.krlite.equator.visual.text.Section;
import net.krlite.knowledges.Knowledges;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.random.Random;
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
	private final Box crosshair = new Box(Vector.fromCartesian(32, 32)).center(Vector.ZERO.add(0, -1));

	@Unique
	private boolean wasHavingModel = false;

	@Unique
	private boolean wasNotTargetingBlock = true;

	@Unique
	private final Interpolation textShifting = new Interpolation(0, 0, 72);

	@Unique
	private void renderKnowledge(MatrixStack matrixStack) {
		if (client.player == null || client.world == null) {
			return;
		}

		if (client.options.getPerspective().isFirstPerson() &&
					((client.interactionManager != null && client.interactionManager.getCurrentGameMode() != GameMode.SPECTATOR) || shouldRenderSpectatorCrosshair(client.crosshairTarget)) &&
					!(client.options.debugEnabled && !client.options.hudHidden && !client.player.hasReducedDebugInfo() && !client.options.getReducedDebugInfo().getValue())
		) {
			PlayerEntity player = getCameraPlayer();
			ItemStack mainHand = player.getMainHandStack(), offHand = player.getOffHandStack();
			ItemStack head = player.getEquippedStack(EquipmentSlot.HEAD), chest = player.getEquippedStack(EquipmentSlot.CHEST), legs = player.getEquippedStack(EquipmentSlot.LEGS), feet = player.getEquippedStack(EquipmentSlot.FEET);

			HitResult hitResult = client.crosshairTarget;
			@Nullable BlockState block = hitResult != null && hitResult.getType() == HitResult.Type.BLOCK ? client.world.getBlockState(((BlockHitResult) hitResult).getBlockPos()) : null;
			@Nullable Entity entity = hitResult != null && hitResult.getType() == HitResult.Type.ENTITY && hitResult instanceof EntityHitResult ? ((EntityHitResult) hitResult).getEntity() : null;

			matrixStack.push();
			matrixStack.translate(FrameInfo.scaled().w() / 2, FrameInfo.scaled().h() / 2, 0);

			// Block inspector
			blockInspector: {
				if (block != null && block.getBlock() != null) {
					ItemStack item = block.getBlock().asItem().getDefaultStack();

					Box box = new Box(Vector.fromCartesian(10, 10)).leftCenter(crosshair.rightCenter());

					boolean havingModel = false;

					// Render 3D block model
					renderModel: {
						if (block.getRenderType() == BlockRenderType.INVISIBLE) break renderModel;

						client.getTextureManager().getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).setFilter(true, false);
						RenderSystem.setShaderTexture(0, SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);

						RenderSystem.enableBlend();
						RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
						RenderSystem.setShaderColor(1, 1, 1, 1);

						matrixStack.push();
						matrixStack.translate(box.center().x(), box.center().y(), 0);

						matrixStack.scale(1, -1, 1);
						matrixStack.scale((float) box.w(), (float) box.h(), 1);
						matrixStack.multiply(new Quaternionf().rotateX((float) Math.toRadians(player.getPitch())).rotateY((float) Math.toRadians(player.getYaw())));

						RenderSystem.applyModelViewMatrix();

						matrixStack.translate(-0.5, -0.5, -0.5);

						VertexConsumerProvider.Immediate immediate = client.getBufferBuilders().getEntityVertexConsumers();

						switch (block.getRenderType()) {
							case MODEL -> {
								int color = client.getBlockColors().getColor(block, client.world, ((BlockHitResult) hitResult).getBlockPos(), 0);

								new BlockModelRenderer(client.getBlockColors()).render(
										matrixStack.peek(), immediate.getBuffer(RenderLayers.getBlockLayer(block)), block,
										MinecraftClient.getInstance().getBlockRenderManager().getModel(block),
										color / 255F, (color >> 8) / 255F, (color >> 16) / 255F,
										0xF000F0, OverlayTexture.DEFAULT_UV
								);

								havingModel = true;
							}
							/*
							case ENTITYBLOCK_ANIMATED -> {
								new BuiltinModelItemRenderer(client.getBlockEntityRenderDispatcher(), client.getEntityModelLoader()).render(
										item, ModelTransformationMode.GROUND, matrixStack, immediate, 0xF00000, OverlayTexture.DEFAULT_UV
								);

								havingModel = true;
							}

							 */
						}

						immediate.draw();
						RenderSystem.enableDepthTest();

						matrixStack.pop();
						RenderSystem.applyModelViewMatrix();
					}

					if (wasNotTargetingBlock) {
						textShifting.originValue(havingModel ? 20 : 0);
						textShifting.reset();
						wasNotTargetingBlock = false;
					}

					if (wasHavingModel != havingModel) {
						textShifting.targetValue(havingModel ? 20 : 0);
						wasHavingModel = havingModel;
					}

					double nameWidth;

					// Render block name
					renderBlockName: {
						MutableText blockName = block.getBlock().getName().copy();

						blockName.formatted(item.getRarity().formatting);
						if (item.hasCustomName())
							blockName.formatted(Formatting.ITALIC);

						Section blockNameSection = new Section(0.7).append(blockName);
						nameWidth = blockNameSection.width();

						FrameInfo.scaled().leftCenter(box.leftCenter().add(textShifting.value(), -1.25)).render(matrixStack,
								flat -> flat.new Text(section -> blockNameSection)
												.withoutShadow()
												.color(AccurateColor.WHITE.opacity(0.9))
												.horizontalAlignment(Paragraph.Alignment.LEFT)
												.verticalAlignment(Section.Alignment.CENTER)
						);
					}

					// Render block breaking progress
					renderBrakingProgress: {
						boolean canHarvest = player.canHarvest(block);

						Box indicator = new Box(Vector.fromCartesian(nameWidth, 1));

						indicator.leftCenter(box.leftCenter().add(textShifting.value(), 3.5)).render(matrixStack,
								flat -> flat.new Rectangle()
												.colors((canHarvest ? Palette.Minecraft.WHITE : Palette.Minecraft.RED).opacity(0.2))
						);

						indicator
								.scale(Knowledges.blockBreakingProgress(), 1)
								.leftCenter(box.leftCenter().add(textShifting.value(), 3.5))
								.render(matrixStack,
										flat -> flat.new Rectangle()
														.colors(Palette.Minecraft.YELLOW
																		.mix(Palette.Minecraft.GREEN, Knowledges.blockBreakingProgress(), ColorStandard.MixMode.PIGMENT)
																		.opacity(0.2 + 0.8 * Math.pow(Knowledges.blockBreakingProgress(), 4)))
						);
					}
				} else {
					wasNotTargetingBlock = true;
				}
			}

			// Armor inspector
			armorInspector: {
				boolean
						hasHead = head != null && !head.isEmpty(),
						hasChest = chest != null && !chest.isEmpty(),
						hasLegs = legs != null && !legs.isEmpty(),
						hasFeet = feet != null && !feet.isEmpty();

				boolean hasArmor = hasHead || hasChest || hasLegs || hasFeet;

				if (!hasArmor) break armorInspector;

				Box indicator = new Box(Vector.fromCartesian(10, 1));

				// Helmet
				renderArmorIndicator(matrixStack, 0D / 3, head, hasHead);

				// Chest plate
				renderArmorIndicator(matrixStack, 1D / 3, chest, hasChest);

				// Leggings
				renderArmorIndicator(matrixStack, 2D / 3, legs, hasLegs);

				// Boots
				renderArmorIndicator(matrixStack, 3D / 3, feet, hasFeet);
			}

			matrixStack.pop();
		}
	}

	private double mapDamageProgress(double x, double yMax, double k) {
		return yMax * (1 - Math.exp(-k * x));
	}

	@Unique
	private void renderArmorIndicator(MatrixStack matrixStack, double positionRatio, ItemStack itemStack, boolean enabled) {
		Box indicator = new Box(Vector.fromCartesian(mapDamageProgress(enabled ? itemStack.getMaxDamage() / 5D : 5, 32, 0.02), 1));
		double shift = 3.5;

		if (enabled) {
			indicator.rightCenter(crosshair.leftCenter().add(0, shift * (2 * positionRatio - 1))).render(matrixStack,
					flat -> flat.new Rectangle()
									.colors((itemStack.getItem().isDamageable() ? Palette.Minecraft.WHITE : Palette.Minecraft.LIGHT_PURPLE).opacity(0.2))
			);

			long color = itemStack.getItemBarColor();
			double damageProgress = (double) itemStack.getDamage() / itemStack.getMaxDamage();

			indicator
					.scale(1 - damageProgress, 1)
					.rightCenter(crosshair.leftCenter().add(0, shift * (2 * positionRatio - 1)))
					.render(matrixStack,
							flat -> flat.new Rectangle()
											.colors(AccurateColor.fromARGB(color | 0xFF000000L).opacity(0.2 + 0.8 * Math.pow(damageProgress, 4)))
					);
		} else {
			indicator.rightCenter(crosshair.leftCenter().add(0, shift * (2 * positionRatio - 1))).render(matrixStack,
					flat -> flat.new Rectangle()
									.colors(Palette.Minecraft.GRAY.opacity(0.2))
			);
		}
	}
}
