package net.krlite.knowledges.components;

import net.krlite.equator.math.geometry.flat.Box;
import net.krlite.equator.math.geometry.flat.Vector;
import net.krlite.equator.render.frame.FrameInfo;
import net.krlite.equator.render.renderer.Flat;
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
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import org.spongepowered.asm.mixin.Unique;

public class ArmorDurabilityComponent implements Knowledge {
	@Override
	public void render(@NotNull MatrixStack matrixStack, @NotNull MinecraftClient client, @NotNull PlayerEntity player, @NotNull ClientWorld world) {
		ItemStack
				head 	= player.getEquippedStack(EquipmentSlot.HEAD),
				chest 	= player.getEquippedStack(EquipmentSlot.CHEST),
				legs 	= player.getEquippedStack(EquipmentSlot.LEGS),
				feet 	= player.getEquippedStack(EquipmentSlot.FEET);

		boolean
				hasHead 	= head != null 	&& !head.isEmpty(),
				hasChest 	= chest != null && !chest.isEmpty(),
				hasLegs 	= legs != null 	&& !legs.isEmpty(),
				hasFeet 	= feet != null 	&& !feet.isEmpty();

		boolean hasArmor = hasHead || hasChest || hasLegs || hasFeet;

		if (!hasArmor) return;

		// Helmet
		renderArmorIndicator(matrixStack, 0, head, hasHead);

		// Chest plate
		//renderArmorIndicator(matrixStack, 1, chest, hasChest);

		// Leggings
		//renderArmorIndicator(matrixStack, 2, legs, hasLegs);

		// Boots
		//renderArmorIndicator(matrixStack, 3, feet, hasFeet);
	}

	@Unique
	private void renderArmorIndicator(MatrixStack matrixStack, @Range(from = 0, to = 3) int position, @Nullable ItemStack itemStack, boolean enabled) {
		Box box = Box.UNIT.scale(22).center(FrameInfo.scaled()).shift(-50, -50);

		AccurateColor color;
		if (itemStack == null || itemStack.isEmpty()) {
			color = Palette.Minecraft.WHITE;
		} else {
			double health = (double) itemStack.getDamage() / itemStack.getMaxDamage();
			color = AccurateColor.fromARGB(itemStack.getItemBarColor());
							//.opacity(0.5 * Knowledges.mapToPower(health / (2 * Math.PI), 2, 0.15));
		}

		box.render(matrixStack,
				flat -> flat.new Oval()
								.colorCenter(Palette.WHITE)
								.mode(Flat.Oval.OvalMode.FILL)
								.offset(-0.5)
								.radians(1)
		);
	}
}
