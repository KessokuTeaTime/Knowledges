package net.krlite.knowledges.components.info;

import net.krlite.equator.visual.color.Palette;
import net.krlite.equator.visual.color.base.ColorStandard;
import net.krlite.knowledges.Knowledges;
import net.krlite.knowledges.components.InfoComponent;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockInfoComponent extends InfoComponent {
	@Override
	public void render(@NotNull DrawContext context, @NotNull MinecraftClient client, @NotNull PlayerEntity player, @NotNull ClientWorld world) {
		super.render(context, client, player, world);
		@Nullable BlockState blockState = Info.crosshairBlock();

		if (blockState != null) {
			MutableText blockName = blockState.getBlock().getName();

			Knowledges.Animations.title(blockName);
			Knowledges.Animations.subtitle(Knowledges.getModName(Registries.BLOCK.getId(blockState.getBlock()).getNamespace()));

			Knowledges.Animations.ovalColor(player.canHarvest(blockState) ? Palette.Minecraft.WHITE : Palette.Minecraft.RED);

			Knowledges.Animations.ringRadians(Math.PI * 2 * Knowledges.Animations.blockBreakingProgress());
			Knowledges.Animations.ringColor(Palette.Minecraft.YELLOW.mix(Palette.Minecraft.GREEN, Knowledges.Animations.blockBreakingProgress(), ColorStandard.MixMode.PIGMENT));
		}
	}

	@Override
	public String id() {
		return "block";
	}
}
