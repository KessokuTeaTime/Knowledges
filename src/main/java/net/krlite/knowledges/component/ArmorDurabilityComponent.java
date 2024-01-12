package net.krlite.knowledges.component;

import net.krlite.equator.math.geometry.flat.Box;
import net.krlite.equator.render.frame.FrameInfo;
import net.krlite.equator.visual.color.AccurateColor;
import net.krlite.equator.visual.color.Palette;
import net.krlite.knowledges.core.util.Helper;
import net.krlite.knowledges.api.Knowledge;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
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
	public void render(@NotNull DrawContext context, @NotNull MinecraftClient client, @NotNull PlayerEntity player, @NotNull ClientWorld world) {
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
		renderArmorIndicator(context, 3, head, hasHead);

		// Chest plate
		renderArmorIndicator(context, 2, chest, hasChest);

		// Leggings
		renderArmorIndicator(context, 1, legs, hasLegs);

		// Boots
		renderArmorIndicator(context, 0, feet, hasFeet);
	}

	@Unique
	private void renderArmorIndicator(DrawContext context, @Range(from = 0, to = 3) int position, @Nullable ItemStack itemStack, boolean enabled) {
		Box box = Box.UNIT
				.scale(16)
				.scale(scalar())
				.center(FrameInfo.scaled())
				.shift(-8 * scalar(), (-8 - 16 * position) * scalar());

		if (enabled) {
			AccurateColor color;
			if (itemStack == null || itemStack.isEmpty()) {
				color = Palette.Minecraft.WHITE.opacity(0.1);
			} else {
				double health = (double) itemStack.getDamage() / itemStack.getMaxDamage();
				color = AccurateColor.fromARGB(itemStack.getItemBarColor())
								.opacity(Helper.Math.mapToPower(health, 2, 0.15));
			}

			box.render(context, flat ->
					flat.new Rectangle()
							.colorLeft(Palette.TRANSPARENT)
							.colorRight(color)
			);

			if (itemStack != null) {
				box.scaleCenter(0.6).render(context, flat ->
						flat.new Item(itemStack)
				);
			}
		}
	}

	@Override
	public @NotNull String path() {
		return "armor_durability";
	}

	@Override
	public boolean providesTooltip() {
		return true;
	}
}