package net.krlite.knowledges.impl.component;

import net.krlite.equator.math.geometry.flat.Box;
import net.krlite.equator.render.frame.FrameInfo;
import net.krlite.equator.visual.color.AccurateColor;
import net.krlite.equator.visual.color.Palette;
import net.krlite.knowledges.Util;
import net.krlite.knowledges.api.component.Knowledge;
import net.krlite.knowledges.api.proxy.LayoutProxy;
import net.krlite.knowledges.api.proxy.RenderProxy;
import net.krlite.knowledges.api.representable.Representable;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import org.spongepowered.asm.mixin.Unique;

public class ArmorDurabilityComponent implements Knowledge {
	@Override
	public void render(RenderProxy renderProxy, @NotNull Representable<?> representable) {
		ItemStack
				head 	= representable.player().getEquippedStack(EquipmentSlot.HEAD),
				chest 	= representable.player().getEquippedStack(EquipmentSlot.CHEST),
				legs 	= representable.player().getEquippedStack(EquipmentSlot.LEGS),
				feet 	= representable.player().getEquippedStack(EquipmentSlot.FEET);

		boolean
				hasHead 	= head != null 	&& !head.isEmpty(),
				hasChest 	= chest != null && !chest.isEmpty(),
				hasLegs 	= legs != null 	&& !legs.isEmpty(),
				hasFeet 	= feet != null 	&& !feet.isEmpty();

		boolean hasArmor = hasHead || hasChest || hasLegs || hasFeet;

		if (!hasArmor) return;

		// Helmet
		renderArmorIndicator(renderProxy, 3, head, hasHead);

		// Chest plate
		renderArmorIndicator(renderProxy, 2, chest, hasChest);

		// Leggings
		renderArmorIndicator(renderProxy, 1, legs, hasLegs);

		// Boots
		renderArmorIndicator(renderProxy, 0, feet, hasFeet);
	}

	@Unique
	private void renderArmorIndicator(RenderProxy renderProxy, @Range(from = 0, to = 3) int position, @Nullable ItemStack itemStack, boolean enabled) {
		Box box = Box.UNIT
				.scale(16)
				.scale(LayoutProxy.scalar())
				.center(FrameInfo.scaled())
				.shift(-8 * LayoutProxy.scalar(), (-8 - 16 * position) * LayoutProxy.scalar());

		if (enabled) {
			AccurateColor color;
			if (itemStack == null || itemStack.isEmpty()) {
				color = Palette.Minecraft.WHITE.opacity(0.1);
			} else {
				double health = (double) itemStack.getDamage() / itemStack.getMaxDamage();
				color = AccurateColor.fromARGB(itemStack.getItemBarColor())
								.opacity(Util.Math.mapToPower(health, 2, 0.15));
			}

			renderProxy.draw(box, flat ->
					flat.new Rectangle()
							.colorLeft(Palette.TRANSPARENT)
							.colorRight(color)
			);

			if (itemStack != null) {
				renderProxy.draw(box.scaleCenter(0.6), flat ->
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
