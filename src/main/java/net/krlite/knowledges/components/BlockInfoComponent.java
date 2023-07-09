package net.krlite.knowledges.components;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.krlite.equator.math.geometry.flat.Box;
import net.krlite.equator.math.geometry.flat.Vector;
import net.krlite.equator.render.frame.FrameInfo;
import net.krlite.equator.render.renderer.Flat;
import net.krlite.equator.visual.animation.Interpolation;
import net.krlite.equator.visual.color.AccurateColor;
import net.krlite.equator.visual.color.Palette;
import net.krlite.equator.visual.color.base.ColorStandard;
import net.krlite.equator.visual.text.Paragraph;
import net.krlite.equator.visual.text.Section;
import net.krlite.knowledges.Knowledge;
import net.krlite.knowledges.Knowledges;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import java.util.Objects;

public class BlockInfoComponent implements Knowledge {
	private boolean wasHavingModel = false, wasNotTargetingBlock = true;

	private final Interpolation
			textShifting = new Interpolation(0, 0, 72),
			textShrinking = new Interpolation(0, 0, 24);

	@Override
	public void render(MatrixStack matrixStack, @NotNull MinecraftClient client, @NotNull PlayerEntity player, @NotNull ClientWorld world) {
		@Nullable BlockState blockState = crosshairBlock();

		Box box = new Box(Vector.UNIT.scale(14 * scalar()))
						  .leftCenter(crosshairSafeArea().rightCenter())
						  .shift(Knowledges.Animations.sprintShift(), 0)
						  .shift(2 * scalar(), 0);

		if (blockState != null && blockState.getBlock() != null) {
			boolean havingModel = false;

			// Render 3D block model
			renderBlockModel: {
				if (blockState.getRenderType() == BlockRenderType.INVISIBLE) break renderBlockModel;

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

				switch (blockState.getRenderType()) {
					case MODEL -> {
						int color = client.getBlockColors().getColor(blockState, client.world, ((BlockHitResult) Objects.requireNonNull(crosshairTarget())).getBlockPos(), 0);

						new BlockModelRenderer(client.getBlockColors()).render(
								matrixStack.peek(), immediate.getBuffer(RenderLayers.getBlockLayer(blockState)), blockState,
								MinecraftClient.getInstance().getBlockRenderManager().getModel(blockState),
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
				textShifting.originValue(havingModel ? Knowledges.Constants.MAX_TEXT_SHIFT : Knowledges.Constants.MIN_TEXT_SHIFT);
				textShifting.reset();
				wasNotTargetingBlock = false;
			}

			if (wasHavingModel != havingModel) {
				textShifting.targetValue(havingModel ? Knowledges.Constants.MAX_TEXT_SHIFT : Knowledges.Constants.MIN_TEXT_SHIFT);
				wasHavingModel = havingModel;
			}
		} else {
			wasNotTargetingBlock = true;
		}

		double opacity = Knowledges.mapToPower(textShrinking.value(), 1D / 2, 0.1);

		matrixStack.push();
		matrixStack.translate(box.rightCenter().x() - 12, box.rightCenter().y(), 0);
		matrixStack.translate(textShifting.value(), 0, 0);
		matrixStack.scale((float) opacity, 1, 1);

		double nameWidth = 0;
		@Nullable ItemStack itemStack = blockState == null ? null : blockState.getBlock().asItem().getDefaultStack();

		// Render block name
		renderBlockName: {
			@Nullable MutableText blockName;
			if (itemStack != null) {
				textShrinking.targetValue(Knowledges.Constants.MAX_TEXT_SHRINK);
				blockName = blockState.getBlock().getName().copy();

				blockName.formatted(itemStack.getRarity().formatting);
				if (itemStack.hasCustomName())
					blockName.formatted(Formatting.ITALIC);

				Knowledges.CONFIG.lastShownBlockName(blockName);
			} else {
				textShrinking.targetValue(Knowledges.Constants.MIN_TEXT_SHRINK);
				blockName = Knowledges.CONFIG.lastShownBlockName();
			}

			if (blockName != null) {
				Section blockNameSection = new Section(0.7 * scalar()).append(blockName);
				nameWidth = blockNameSection.width() * textShrinking.value();

				FrameInfo.scaled()
						.leftCenter(Vector.ZERO.add(0, -1.5 * scalar()))
						.render(matrixStack,
								flat -> flat.new Text(section -> blockNameSection)
												.withoutShadow()
												.color(AccurateColor.WHITE.opacity(0.8 * opacity))
												.horizontalAlignment(Paragraph.Alignment.LEFT)
												.verticalAlignment(Section.Alignment.CENTER)
						);
			}
		}

		// Render block breaking progress
		renderBrakingProgress: {
			if (blockState == null || nameWidth == 0) break renderBrakingProgress;

			boolean canHarvest = player.canHarvest(blockState);

			Box indicator = new Box(Vector.fromCartesian(nameWidth, 1))
									.leftCenter(Vector.ZERO.add(0, 3.5 * scalar()));

			indicator
					.render(matrixStack,
							flat -> flat.new Rectangle()
											.colors((canHarvest ? Palette.Minecraft.WHITE : Palette.Minecraft.RED).opacity(0.2 * opacity))
					);

			indicator
					.translateRight(Knowledges.CONFIG.blockBreakingProgress() - 1)
					.render(matrixStack,
							flat -> flat.new Rectangle()
											.colors(Palette.Minecraft.YELLOW
															.mix(Palette.Minecraft.GREEN, Knowledges.CONFIG.blockBreakingProgress(), ColorStandard.MixMode.PIGMENT)
															.opacity(Knowledges.mapToPower(Knowledges.CONFIG.blockBreakingProgress(), 4, 0.2) * opacity))
					);
		}

		matrixStack.pop();
	}
}
