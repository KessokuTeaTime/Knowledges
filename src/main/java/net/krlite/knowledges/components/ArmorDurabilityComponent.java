package net.krlite.knowledges.components;

import net.krlite.equator.math.geometry.flat.Box;
import net.krlite.equator.math.geometry.flat.Vector;
import net.krlite.equator.render.frame.FrameInfo;
import net.krlite.equator.render.renderer.Flat;
import net.krlite.equator.visual.color.AccurateColor;
import net.krlite.equator.visual.color.Palette;
import net.krlite.equator.visual.color.base.ColorStandard;
import net.krlite.knowledges.Knowledge;
import net.krlite.knowledges.Knowledges;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
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
				.shift(-8, -8 - 16 * position);

		if (enabled) {
			AccurateColor color;
			if (itemStack == null || itemStack.isEmpty()) {
				color = Palette.Minecraft.WHITE.opacity(0.1);
			} else {
				double health = (double) itemStack.getDamage() / itemStack.getMaxDamage();
				color = AccurateColor.fromARGB(itemStack.getItemBarColor())
								.opacity(Knowledges.mapToPower(health, 2, 0.15));
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
	public @NotNull Text name() {
		return Knowledges.localize("knowledge", "armor_durability", "name");
	}

	@Override
	public @Nullable Text tooltip() {
		return Knowledges.localize("knowledge", "armor_durability", "tooltip");
	}
}
