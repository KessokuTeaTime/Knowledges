package net.krlite.knowledges;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.krlite.knowledges.util.FlaggedTimer;
import net.krlite.knowledges.util.Ranger;
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
	public static final long ANIMATION_TIME = 175, DURATION_TIME = 1000;
	@NotNull private static ItemStack permanentItem = ItemStack.EMPTY;
	private static final Stack<Pair<ItemStack, Ranger>> TEMPORARY_ITEMS = new Stack<>();
	@Nullable private static Ranger temporaryAnimation = null;
	@NotNull private static final Ranger PERMANENT_ANIMATION = new Ranger(DURATION_TIME + ANIMATION_TIME, ANIMATION_TIME);
	@NotNull private static final FlaggedTimer TERMINAL_ANIMATION = new FlaggedTimer(ANIMATION_TIME);
	@NotNull private static ItemStack lastPushed = ItemStack.EMPTY;

	public static void renderKnowledge() {
		while (!TEMPORARY_ITEMS.empty() && TEMPORARY_ITEMS.peek().getRight().isFinished()) TEMPORARY_ITEMS.pop();
		if (!TEMPORARY_ITEMS.empty()) {
			temporaryAnimation = TEMPORARY_ITEMS.peek().getRight();
			flipUpTemporary(TEMPORARY_ITEMS.peek().getLeft());

			if (TEMPORARY_ITEMS.size() > 1)
				TEMPORARY_ITEMS.stream().skip(TEMPORARY_ITEMS.size() - 2).limit(1).findFirst()
						.filter(p -> isValidRanger(p.getRight())).ifPresent(secondary -> flipDownTemporary(secondary.getLeft()));
		} else temporaryAnimation = null;

		if (!permanentItem.isEmpty() && (PERMANENT_ANIMATION.isPresent() || !TERMINAL_ANIMATION.isFlagged() || TERMINAL_ANIMATION.isPresent()))
			flipPermanent(permanentItem);
	}

	public static void pushTemporary(@NotNull ItemStack itemStack, long lasting) {
		if (!lastPushed.isItemEqual(itemStack)) {
			if (!itemStack.isEmpty() && (!permanentItem.isItemEqual(itemStack) || (PERMANENT_ANIMATION.isFinished() && TERMINAL_ANIMATION.isFinished())))
				TEMPORARY_ITEMS.push(new Pair<>(itemStack, new Ranger(lasting + ANIMATION_TIME, ANIMATION_TIME)));
			lastPushed = itemStack;
		}
	}

	public static void pushTemporary(@NotNull ItemStack itemStack) {
		pushTemporary(itemStack, DURATION_TIME);
	}

	public static void setPermanent(@NotNull ItemStack itemStack, boolean force) {
		if ((!itemStack.isEmpty() && !permanentItem.isItemEqual(itemStack)) || force) {
			permanentItem = itemStack;
			PERMANENT_ANIMATION.reset();
			TERMINAL_ANIMATION.unFlag();
		}
	}

	public static void setPermanent(@NotNull ItemStack itemStack) {
		setPermanent(itemStack, false);
	}

	public static void clearPermanent() {
		if (!permanentItem.isEmpty()) TERMINAL_ANIMATION.flag();
	}

	private static void flipUpTemporary(@NotNull ItemStack itemStack) {
		if (!itemStack.isEmpty() && temporaryAnimation != null && temporaryAnimation.isPresent())
			renderItem(itemStack, 1 - (float) temporaryAnimation.queueAsPercentage());
	}

	private static void flipDownTemporary(@NotNull ItemStack itemStack) {
		if (!itemStack.isEmpty() && temporaryAnimation != null && temporaryAnimation.isPresent())
			renderItem(itemStack, (float) -temporaryAnimation.queueAsPercentage());
	}

	private static void flipPermanent(@NotNull ItemStack itemStack) {
		if (PERMANENT_ANIMATION.isPresent() || !TERMINAL_ANIMATION.isFlagged() || TERMINAL_ANIMATION.isPresent()) {
			double permanentSqueezing =
					PERMANENT_ANIMATION.isAscending() ?
							Math.max(1 - PERMANENT_ANIMATION.queueAsPercentage(), TERMINAL_ANIMATION.queueAsPercentage()) :
							Math.min(1 - PERMANENT_ANIMATION.queueAsPercentage(), TERMINAL_ANIMATION.queueAsPercentage());
			permanentSqueezing = TERMINAL_ANIMATION.isFinished() ? 1 : permanentSqueezing - ((TEMPORARY_ITEMS.size() > 1 && isValidRanger(TEMPORARY_ITEMS.peek().getRight()) ? 1 : temporaryAnimation != null ? temporaryAnimation.queueAsPercentage() : 0));
			renderItem(itemStack, (float) permanentSqueezing);
		}
	}

	private static boolean isValidRanger(Ranger ranger) {
		return ranger.getLasting() - ranger.queueRaw() >= ANIMATION_TIME;
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

	private static void renderItem(@NotNull ItemStack itemStack, float squeezing) {
		renderItem(itemStack, new Vec3d(Knowledges.getX(), Knowledges.getY(), 0), 0.75F, squeezing);
	}
}
