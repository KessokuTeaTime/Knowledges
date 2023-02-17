package net.krlite.knowledges;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.krlite.equator.util.Pusher;
import net.krlite.equator.util.Timer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Stack;

public class KnowledgeFlipper {
	@Nullable
	private static ItemStack permanentItem = null;
	private static final Stack<Pair<ItemStack, Timer>> TEMPORARY_ITEMS = new Stack<>();
	private static final Timer FLIP_TIMER = new Timer(175);
	private static final Pusher POP_MODE = new Pusher();
	private static boolean popping = false;

	public static void renderKnowledge() {
		while (!TEMPORARY_ITEMS.empty() && TEMPORARY_ITEMS.peek().getRight().isFinished()) TEMPORARY_ITEMS.pop();
		FLIP_TIMER.run(() -> POP_MODE.pull(() -> popping = false));
		if (!TEMPORARY_ITEMS.empty()) {
			Knowledges.LOGGER.warn(TEMPORARY_ITEMS.peek().getLeft().getItem().getName().getString() + ", " + FLIP_TIMER.queueAsPercentage() + ", " + popping);
			flipUp(TEMPORARY_ITEMS.peek().getLeft(), popping);
		}
	}

	public static void pushItem(ItemStack itemStack, Timer timer) {
		TEMPORARY_ITEMS.push(new Pair<>(itemStack, timer));
		FLIP_TIMER.reset();
	}

	private static void popItem() {
		TEMPORARY_ITEMS.push(new Pair<>(TEMPORARY_ITEMS.pop().getLeft(), FLIP_TIMER));
		FLIP_TIMER.reset();
	}

	private static void flipUp(@Nullable ItemStack itemStack, boolean reversed) {
		if (itemStack != null)
			renderItem(itemStack, new Vec3d(Knowledges.getX(), Knowledges.getY(), 0), 0.75F, reversed ? (float) FLIP_TIMER.queueAsPercentage() : 1 - (float) FLIP_TIMER.queueAsPercentage());
	}

	public static void flipDown(@Nullable ItemStack itemStack, boolean reversed) {
		if (itemStack != null)
			renderItem(itemStack, new Vec3d(Knowledges.getX(), Knowledges.getY(), 0), 0.75F, reversed ? (float) FLIP_TIMER.queueAsPercentage() - 1 : (float) -FLIP_TIMER.queueAsPercentage());
	}

	@SuppressWarnings("deprecation")
	private static void prepareModel() {
		MinecraftClient.getInstance().getTextureManager().getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).setFilter(false, false);
		RenderSystem.setShaderTexture(0, SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);

		RenderSystem.enableBlend();
		RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
		RenderSystem.setShaderColor(1, 1, 1, 1);
	}

	private static void applyModelView(@NotNull MatrixStack matrixStack, float scaling, float squeezing) {
		matrixStack.scale(1, -1, 1);
		matrixStack.scale(16 * scaling, 16 * scaling * (1 - Math.abs(squeezing)), 16 * scaling);

		RenderSystem.applyModelViewMatrix();
	}

	private static void renderItem(@NotNull ItemStack itemStack, @NotNull Vec3d pos, float scaling, float squeezing) {
		BakedModel bakedModel = MinecraftClient.getInstance().getItemRenderer().getModel(itemStack, null, null, 0);
		prepareModel();
		MatrixStack matrixStack = RenderSystem.getModelViewStack();

		matrixStack.push();
		pos = pos.add(-8 * scaling, 0, 0);
		matrixStack.translate(pos.x, pos.y, 100 + pos.z);
		matrixStack.translate(8 * scaling, 8 * scaling * (1 - squeezing), 0);
		applyModelView(matrixStack, scaling, squeezing);

		MatrixStack itemMatrixStack = new MatrixStack();
		VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();

		if (!bakedModel.isSideLit())
			DiffuseLighting.disableGuiDepthLighting();

		MinecraftClient.getInstance().getItemRenderer().renderItem(itemStack, ModelTransformation.Mode.GUI,
				false, itemMatrixStack, immediate, 0xF000F0, OverlayTexture.DEFAULT_UV, bakedModel);
		immediate.draw();
		RenderSystem.enableDepthTest();

		if (!bakedModel.isSideLit())
			DiffuseLighting.enableGuiDepthLighting();

		matrixStack.pop();
		RenderSystem.applyModelViewMatrix();
	}
}
