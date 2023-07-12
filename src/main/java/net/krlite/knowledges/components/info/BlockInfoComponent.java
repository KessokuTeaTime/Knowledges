package net.krlite.knowledges.components.info;

import net.krlite.equator.visual.color.Palette;
import net.krlite.equator.visual.color.base.ColorStandard;
import net.krlite.knowledges.Knowledges;
import net.krlite.knowledges.components.InfoComponent;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.MutableText;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockInfoComponent extends InfoComponent {
	@Override
	public void render(@NotNull MatrixStack matrixStack, @NotNull MinecraftClient client, @NotNull PlayerEntity player, @NotNull ClientWorld world) {
		super.render(matrixStack, client, player, world);
		@Nullable BlockState blockState = Info.crosshairBlock();

		if (blockState != null) {
			MutableText blockName = blockState.getBlock().getName();
			Knowledges.Animations.textLength(blockName.getString().length());

			/*
			@Nullable ItemStack itemStack = blockState.getBlock().asItem().getDefaultStack();

			if (itemStack != null) {
				blockName.formatted(itemStack.getRarity().formatting);
				if (itemStack.hasCustomName()) blockName.formatted(Formatting.ITALIC);
			}

			 */

			Knowledges.Animations.text(blockName);

			Knowledges.Animations.ovalColor(player.canHarvest(blockState) ? Palette.Minecraft.WHITE : Palette.Minecraft.RED);

			Knowledges.Animations.ringRadians(Math.PI * 2 * Knowledges.CONFIG.blockBreakingProgress());
			Knowledges.Animations.ringColor(Palette.Minecraft.YELLOW.mix(Palette.Minecraft.GREEN, Knowledges.CONFIG.blockBreakingProgress(), ColorStandard.MixMode.PIGMENT));
		}
	}
}
