package net.krlite.knowledges.api.component;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.krlite.knowledges.KnowledgesClient;
import net.krlite.knowledges.KnowledgesCommon;
import net.krlite.knowledges.api.core.config.WithIndependentConfigPage;
import net.krlite.knowledges.api.core.localization.Localizable;
import net.krlite.knowledges.api.core.path.WithPath;
import net.krlite.knowledges.api.representable.Representable;
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

public interface Knowledge extends WithPath, Localizable.WithName, WithIndependentConfigPage {
	default void render(@NotNull Representable<?> representable) {};

	@Deprecated
	void render(@NotNull DrawContext context, @NotNull MinecraftClient client, @NotNull PlayerEntity player, @NotNull ClientWorld world);

	@Override
	default String localizationKey(String... paths) {
		List<String> fullPaths = new ArrayList<>(List.of(path()));
		fullPaths.addAll(List.of(paths));

		return KnowledgesClient.COMPONENTS.localizationKey(this, fullPaths.toArray(String[]::new));
	}

	@Deprecated
	class Info {
		@Deprecated
		public static boolean hasCrosshairTarget() {
			MinecraftClient client = MinecraftClient.getInstance();
			if (client.world == null || client.player == null) return false;

			boolean blockPos = KnowledgesClient.COMPONENTS.isEnabled(KnowledgesClient.COMPONENTS.byId(KnowledgesCommon.ID, "info", "block").orElseThrow()) && crosshairBlockPos().isPresent();
			boolean entity = KnowledgesClient.COMPONENTS.isEnabled(KnowledgesClient.COMPONENTS.byId(KnowledgesCommon.ID, "info", "entity").orElseThrow()) && crosshairEntity().isPresent();
			boolean fluidState = KnowledgesClient.COMPONENTS.isEnabled(KnowledgesClient.COMPONENTS.byId(KnowledgesCommon.ID, "info", "fluid").orElseThrow()) && crosshairFluidState().isPresent();

			return blockPos || entity || fluidState;
		}

		@Deprecated
		public static Optional<HitResult> crosshairTarget() {
			return Optional.ofNullable(MinecraftClient.getInstance().crosshairTarget);
		}

		@Deprecated
		public static Optional<Vec3d> crosshairPos() {
			return crosshairTarget().map(HitResult::getPos);
		}

		@Deprecated
		public static Optional<BlockPos> crosshairBlockPos() {
			return crosshairTarget()
					.filter(hitResult -> hitResult.getType() == HitResult.Type.BLOCK)
					.map(hitResult -> (BlockHitResult) hitResult)
					.map(BlockHitResult::getBlockPos);
		}

		@Deprecated
		public static Optional<BlockState> crosshairBlockState() {
			MinecraftClient client = MinecraftClient.getInstance();
			return crosshairBlockPos()
					.map(blockPos -> client.world != null ? client.world.getBlockState(blockPos) : null);
		}

		@Deprecated
		public static Optional<BlockEntity> crosshairBlockEntity() {
			MinecraftClient client = MinecraftClient.getInstance();
			return crosshairBlockPos()
					.map(blockPos -> client.world != null ? client.world.getBlockEntity(blockPos) : null);
		}

		@Deprecated
		public static Optional<Entity> crosshairEntity() {
			return crosshairTarget()
					.filter(hitResult -> hitResult.getType() == HitResult.Type.ENTITY)
					.map(hitResult -> (EntityHitResult) hitResult)
					.map(EntityHitResult::getEntity);
		}

		@Deprecated
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
					.filter(fluidState -> !(KnowledgesClient.CONFIG.components.infoFluid.ignoresWater && (
							fluidState.getFluid() == Fluids.WATER || fluidState.getFluid() == Fluids.FLOWING_WATER
					)))
					.filter(fluidState -> !(KnowledgesClient.CONFIG.components.infoFluid.ignoresLava && (
							fluidState.getFluid() == Fluids.LAVA || fluidState.getFluid() == Fluids.FLOWING_LAVA
					)))
					.filter(fluidState -> !(KnowledgesClient.CONFIG.components.infoFluid.ignoresOtherFluids && (
							fluidState.getFluid() != Fluids.WATER
									&& fluidState.getFluid() != Fluids.LAVA
									&& fluidState.getFluid() != Fluids.FLOWING_WATER
									&& fluidState.getFluid() != Fluids.FLOWING_LAVA
					)));
		}
	}

	@Deprecated
	class Util {
		@Deprecated
		public static MutableText modName(String namespace) {
			return Text.literal(FabricLoader.getInstance().getModContainer(namespace)
					.map(ModContainer::getMetadata)
					.map(ModMetadata::getName)
					.orElse(""));
		}

		@Deprecated
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

		@Deprecated
		public static MutableText instrumentName(Instrument instrument) {
			return KnowledgesClient.localize("instrument", instrument.asString());
		}

		@Deprecated
		public static MutableText dateAndTime() {
			if (MinecraftClient.getInstance().world == null) return Text.empty();

			long time = MinecraftClient.getInstance().world.getTimeOfDay() + 24000 / 4; // Offset to morning
			long day = time / 24000;
			double percentage = (time % 24000) / 24000.0;

			int hour = (int) (24 * percentage), minute = (int) (60 * ((percentage * 24) % 1));

			return Text.translatable(
					KnowledgesClient.localizationKey("util", "date_and_time"),
					String.valueOf(day), String.format("%02d", hour), String.format("%02d", minute)
			);
		}
	}
}
