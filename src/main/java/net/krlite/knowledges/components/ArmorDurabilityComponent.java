package net.krlite.knowledges.components;

import net.krlite.equator.math.geometry.flat.Box;
import net.krlite.equator.math.geometry.flat.Vector;
import net.krlite.equator.visual.color.AccurateColor;
import net.krlite.equator.visual.color.Palette;
import net.krlite.knowledges.Knowledge;
import net.krlite.knowledges.Knowledges;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Unique;

public class ArmorDurabilityComponent implements Knowledge {
	@Override
	public void render(MatrixStack matrixStack, @NotNull MinecraftClient client, @NotNull PlayerEntity player, @NotNull ClientWorld world) {
		ItemStack
				head 	= player.getEquippedStack(EquipmentSlot.HEAD),
				chest 	= player.getEquippedStack(EquipmentSlot.CHEST),
				legs 	= player.getEquippedStack(EquipmentSlot.LEGS),
				feet 	= player.getEquippedStack(EquipmentSlot.FEET);

		boolean
				hasHead 	= head != null && !head.isEmpty(),
				hasChest 	= chest != null && !chest.isEmpty(),
				hasLegs 	= legs != null && !legs.isEmpty(),
				hasFeet 	= feet != null && !feet.isEmpty();

		boolean hasArmor = hasHead || hasChest || hasLegs || hasFeet;

		if (!hasArmor) return;

		// Helmet
		renderArmorIndicator(matrixStack, 0D / 3, head, hasHead);

		// Chest plate
		renderArmorIndicator(matrixStack, 1D / 3, chest, hasChest);

		// Leggings
		renderArmorIndicator(matrixStack, 2D / 3, legs, hasLegs);

		// Boots
		renderArmorIndicator(matrixStack, 3D / 3, feet, hasFeet);
	}

	@Unique
	private void renderArmorIndicator(MatrixStack matrixStack, double positionRatio, ItemStack itemStack, boolean enabled) {
	}
}
