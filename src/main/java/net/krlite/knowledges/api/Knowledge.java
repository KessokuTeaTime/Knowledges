package net.krlite.knowledges.api;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.krlite.equator.math.geometry.flat.Box;
import net.krlite.equator.math.geometry.flat.Vector;
import net.krlite.knowledges.Knowledges;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.Instrument;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public interface Knowledge {
	void render(@NotNull DrawContext context, @NotNull MinecraftClient client, @NotNull PlayerEntity player, @NotNull ClientWorld world);

	@NotNull String id();

	boolean provideTooltip();

	default @NotNull Text name() {
		return localize("name");
	};

	default @Nullable Text tooltip() {
		return provideTooltip() ? localize("tooltip") : null;
	}

	default String localizationKey(String... paths) {
		List<String> fullPaths = new ArrayList<>(List.of(id()));
		fullPaths.addAll(List.of(paths));

		return Knowledges.localizationKey("knowledge", fullPaths.toArray(String[]::new));
	}

	default MutableText localize(String... paths) {
		return Knowledges.localize(localizationKey(paths));
	}

	default double scalar() {
		return 0.5 + 0.5 * Knowledges.CONFIG.scalar();
	}

	default Box crosshairSafeArea() {
		double size = 16 + 8 * Knowledges.CONFIG.crosshairSafeAreaSizeScalar();
		return Box.UNIT.scale(size)
					   .scale(scalar())
					   .center(Vector.ZERO)
					   .shift(0, -1);
	}

	class Info {
		public static boolean hasCrosshairTarget() {
			MinecraftClient client = MinecraftClient.getInstance();
			if (client.world == null || client.player == null) return false;

			return crosshairBlockState().isPresent() || crosshairEntity().isPresent();
		}

		public static Optional<HitResult> crosshairTarget() {
			return Optional.ofNullable(hasCrosshairTarget() ? MinecraftClient.getInstance().crosshairTarget : null);
		}

		public static Optional<Vec3d> crosshairPos() {
			return crosshairTarget()
					.map(HitResult::getPos);
		}

		public static Optional<BlockPos> crosshairBlockPos() {
			return crosshairTarget()
					.filter(hitResult -> hitResult.getType() == HitResult.Type.BLOCK)
					.map(hitResult -> (BlockHitResult) hitResult)
					.map(BlockHitResult::getBlockPos);
		}

		public static Optional<BlockState> crosshairBlockState() {
			MinecraftClient client = MinecraftClient.getInstance();
			return crosshairBlockPos()
					.map(blockPos -> client.world != null ? client.world.getBlockState(blockPos) : null);
		}

		public static Optional<BlockEntity> crosshairBlockEntity() {
			MinecraftClient client = MinecraftClient.getInstance();
			return crosshairBlockPos()
					.map(blockPos -> client.world != null ? client.world.getBlockEntity(blockPos) : null);
		}

		public static Optional<Entity> crosshairEntity() {
			return crosshairTarget()
					.filter(hitResult -> hitResult.getType() == HitResult.Type.ENTITY)
					.map(hitResult -> (EntityHitResult) hitResult)
					.map(EntityHitResult::getEntity);
		}

		public static Optional<FluidState> crosshairFluidState() {
			MinecraftClient client = MinecraftClient.getInstance();
			return crosshairBlockPos()
					.map(blockPos -> client.world != null ? client.world.getFluidState(blockPos) : null);
		}
	}

	class Util {
		public static MutableText getModName(String namespace) {
			return Text.literal(FabricLoader.getInstance().getModContainer(namespace)
					.map(ModContainer::getMetadata)
					.map(ModMetadata::getName)
					.orElse(""));
		}

		public static String getNamespace(ItemStack itemStack) {
			String namespace = Registries.ITEM.getId(itemStack.getItem()).getNamespace();

			// Enchanted Book
			if (itemStack.getItem() instanceof EnchantedBookItem) {
				NbtList nbtList = EnchantedBookItem.getEnchantmentNbt(itemStack);
				String enchantmentNamespace = null;

				for (int i = 0; i < nbtList.size(); i++) {
					Identifier id = Identifier.tryParse(nbtList.getCompound(i).getString("id"));
					if (id != null) {
						if (enchantmentNamespace == null) {
							enchantmentNamespace = id.getNamespace();
						} else if (!enchantmentNamespace.equals(id.getNamespace())) {
							enchantmentNamespace = null;
							break;
						}
					}
				}

				if (enchantmentNamespace != null) {
					namespace = enchantmentNamespace;
				}
			}

			// Potion and Tipped Arrow
			if (itemStack.getItem() instanceof PotionItem || itemStack.getItem() instanceof TippedArrowItem) {
				NbtCompound nbtCompound = itemStack.getNbt();
				Potion potion = nbtCompound == null ? Potions.EMPTY : Potion.byId(nbtCompound.getString("Potion"));
				namespace = Registries.POTION.getId(potion).getNamespace();
			}

			// Painting
			if (itemStack.isOf(Items.PAINTING)) {
				NbtCompound nbtCompound = itemStack.getNbt();
				String paintingNamespace = null;

				if (nbtCompound != null && nbtCompound.contains("EntityTag", 10)) {
					NbtCompound entityTagCompound = nbtCompound.getCompound("EntityTag");

					paintingNamespace = PaintingEntity.readVariantFromNbt(entityTagCompound)
							.flatMap(RegistryEntry::getKey)
							.map(RegistryKey::getRegistry)
							.map(Identifier::getNamespace)
							.orElse(null);
				}

				if (paintingNamespace != null) {
					namespace = paintingNamespace;
				}
			}

			return namespace;
		}

		public static MutableText getInstrumentName(Instrument instrument) {
			return Knowledges.localize("instrument", instrument.asString());
		}
	}
}