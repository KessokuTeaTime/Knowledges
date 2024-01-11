package net.krlite.knowledges.api;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.krlite.equator.math.geometry.flat.Box;
import net.krlite.equator.math.geometry.flat.Vector;
import net.krlite.knowledges.Knowledges;
import net.krlite.knowledges.config.KnowledgesConfig;
import net.krlite.knowledges.core.config.WithIndependentConfigPage;
import net.krlite.knowledges.core.localization.LocalizableWithName;
import net.krlite.knowledges.core.path.WithPath;
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
import net.minecraft.fluid.Fluids;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface Knowledge extends WithPath, LocalizableWithName, WithIndependentConfigPage {
	void render(@NotNull DrawContext context, @NotNull MinecraftClient client, @NotNull PlayerEntity player, @NotNull ClientWorld world);

	@Override
	default String localizationKey(String... paths) {
		List<String> fullPaths = new ArrayList<>(List.of(path()));
		fullPaths.addAll(List.of(paths));

		return Knowledges.COMPONENTS.localizationKey(this, fullPaths.toArray(String[]::new));
	}

	default double scalar() {
		return 0.5 + 0.5 * KnowledgesConfig.Global.MAIN_SCALAR.get();
	}

	default Box crosshairSafeArea() {
		double size = 16 + 8 * KnowledgesConfig.Global.CROSSHAIR_SAFE_AREA_SCALAR.get();
		return Box.UNIT.scale(size)
					   .scale(scalar())
					   .center(Vector.ZERO)
					   .shift(0, -1);
	}

	class Info {
		public static boolean hasCrosshairTarget() {
			MinecraftClient client = MinecraftClient.getInstance();
			if (client.world == null || client.player == null) return false;

			boolean blockPos = Knowledges.COMPONENTS.isEnabled(Knowledges.COMPONENTS.byId(Knowledges.ID, "info", "block").orElseThrow()) && crosshairBlockPos().isPresent();
			boolean entity = Knowledges.COMPONENTS.isEnabled(Knowledges.COMPONENTS.byId(Knowledges.ID, "info", "entity").orElseThrow()) && crosshairEntity().isPresent();
			boolean fluidState = Knowledges.COMPONENTS.isEnabled(Knowledges.COMPONENTS.byId(Knowledges.ID, "info", "fluid").orElseThrow()) && crosshairFluidState().isPresent();

			return blockPos || entity || fluidState;
		}

		public static Optional<HitResult> crosshairTarget() {
			return Optional.ofNullable(MinecraftClient.getInstance().crosshairTarget);
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

			return crosshairTarget()
					.filter(hitResult -> hitResult.getType() == HitResult.Type.MISS)
					.flatMap(hitResult -> crosshairPos()
							.flatMap(pos -> Optional.ofNullable(client.world)
									.map(world -> world.getFluidState(new BlockPos((int) pos.getX(), (int) pos.getY(), (int) pos.getZ())))
							)
					)
					.filter(fluidState -> !fluidState.isEmpty())
					.filter(fluidState -> !(KnowledgesConfig.InfoFluid.IGNORES_WATER.get() && (
							fluidState.getFluid() == Fluids.WATER || fluidState.getFluid() == Fluids.FLOWING_WATER
					)))
					.filter(fluidState -> !(KnowledgesConfig.InfoFluid.IGNORES_LAVA.get() && (
							fluidState.getFluid() == Fluids.LAVA || fluidState.getFluid() == Fluids.FLOWING_LAVA
					)))
					.filter(fluidState -> !(KnowledgesConfig.InfoFluid.IGNORES_OTHER_FLUIDS.get() && (
							fluidState.getFluid() != Fluids.WATER
									&& fluidState.getFluid() != Fluids.LAVA
									&& fluidState.getFluid() != Fluids.FLOWING_WATER
									&& fluidState.getFluid() != Fluids.FLOWING_LAVA
					)));
		}
	}

	class Util {
		public static MutableText modName(String namespace) {
			return Text.literal(FabricLoader.getInstance().getModContainer(namespace)
					.map(ModContainer::getMetadata)
					.map(ModMetadata::getName)
					.orElse(""));
		}

		public static String namespace(ItemStack itemStack) {
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

		public static MutableText instrumentName(Instrument instrument) {
			return Knowledges.localize("instrument", instrument.asString());
		}

		public static MutableText dateAndTime() {
			if (MinecraftClient.getInstance().world == null) return Text.empty();

			long time = MinecraftClient.getInstance().world.getTimeOfDay();
			long day = time / 24000;
			double percentage = (time % 24000) / 24000.0;

			int hour = (int) (24 * percentage), minute = (int) (60 * ((percentage * 24) % 1));

			return Text.translatable(
					Knowledges.localizationKey("util", "date_and_time"),
					String.valueOf(day), String.format("%02d", hour), String.format("%02d", minute)
			);
		}
	}
}
