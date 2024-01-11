package net.krlite.knowledges.config;

import net.krlite.equator.math.algebra.Theory;
import net.krlite.knowledges.Knowledges;
import net.krlite.pierced.annotation.Silent;
import net.krlite.pierced.annotation.Table;
import net.krlite.pierced.config.Pierced;

import java.io.File;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class KnowledgesConfig extends Pierced {
	private static final @Silent File file = Knowledges.CONFIG_PATH.resolve("config.toml").toFile();

	private static final @Silent KnowledgesConfig self = new KnowledgesConfig();

	KnowledgesConfig() {
		super(KnowledgesConfig.class, file);
		load();
	}

	public static void loadSelf() {
		self.load();
	}

	public static void saveSelf() {
		self.save();
	}

	public static class WithDefault<V> {
		private final V defaultValue;
		private final Supplier<V> supplier;
		private final Consumer<V> consumer;

		public WithDefault(V defaultValue, Supplier<V> supplier, Consumer<V> consumer) {
			this.defaultValue = defaultValue;
			this.supplier = supplier;
			this.consumer = consumer;
		}

		public V defaultValue() {
			return defaultValue;
		}

		public V get() {
			return supplier.get();
		}

		public void set(V value) {
			consumer.accept(value);
		}
	}

	public static class Range<V extends Comparable<V>> extends WithDefault<V> {
		private final V minValue, maxValue;

		public Range(V defaultValue, V minValue, V maxValue, Supplier<V> supplier, Consumer<V> consumer) {
			super(defaultValue, supplier, consumer);
			if (minValue.compareTo(maxValue) > 0) {
				this.minValue = maxValue;
				this.maxValue = minValue;
			} else {
				this.minValue = minValue;
				this.maxValue = maxValue;
			}
		}

		public V min() {
			return minValue;
		}

		public V max() {
			return maxValue;
		}

		@Override
		public V get() {
			V value = super.get();
			if (value.compareTo(min()) < 0) return min();
			if (value.compareTo(max()) > 0) return max();
			return value;
		}

		@Override
		public void set(V value) {
			if (value.compareTo(min()) < 0) super.set(min());
			if (value.compareTo(max()) > 0) super.set(max());
			super.set(value);
		}
	}

	public static class BooleanToggle extends WithDefault<Boolean> {
		public BooleanToggle(Boolean defaultValue, Supplier<Boolean> supplier, Consumer<Boolean> consumer) {
			super(defaultValue, supplier, consumer);
		}
	}

	public static class Global {
		public static final Range<Double> MAIN_SCALAR = new Range<>(
				1.0, 0.0, 3.0,
				() -> KnowledgesConfig.globalMainScalar,
				value -> KnowledgesConfig.globalMainScalar = value
		);
		public static final Range<Double> CROSSHAIR_SAFE_AREA_SCALAR = new Range<>(
				1.0, 0.0, 3.0,
				() -> KnowledgesConfig.globalCrosshairSafeAreaScalar,
				value -> KnowledgesConfig.globalCrosshairSafeAreaScalar = value
		);
	}

	public static class Crosshair {
		public static final BooleanToggle CURSOR_RING_OUTLINE = new BooleanToggle(
				true,
				() -> KnowledgesConfig.crosshairCusrorRingOutline,
				value -> KnowledgesConfig.crosshairCusrorRingOutline = value
		);
		public static final BooleanToggle TEXTS_RIGHT = new BooleanToggle(
				true,
				() -> KnowledgesConfig.crosshairTextsRight,
				value -> KnowledgesConfig.crosshairTextsRight = value
		);
		public static final BooleanToggle TEXTS_LEFT = new BooleanToggle(
				true,
				() -> KnowledgesConfig.crosshairTextsLeft,
				value -> KnowledgesConfig.crosshairTextsLeft = value
		);
		public static final BooleanToggle SUBTITLES = new BooleanToggle(
				true,
				() -> KnowledgesConfig.crosshairSubtitles,
				value -> KnowledgesConfig.crosshairSubtitles = value
		);
	}

	public static class InfoBlock {
		public static final BooleanToggle SHOW_POWERED_STATUS = new BooleanToggle(
				true,
				() -> KnowledgesConfig.infoBlockShowPoweredStatus,
				value -> KnowledgesConfig.infoBlockShowPoweredStatus = value
		);
	}

	public static class InfoFluid {
		public static final BooleanToggle IGNORES_WATER = new BooleanToggle(
				false,
				() -> KnowledgesConfig.infoFluidIgnoresWater,
				value -> KnowledgesConfig.infoFluidIgnoresWater = value
		);
		public static final BooleanToggle IGNORES_LAVA = new BooleanToggle(
				false,
				() -> KnowledgesConfig.infoFluidIgnoresLava,
				value -> KnowledgesConfig.infoFluidIgnoresLava = value
		);
		public static final BooleanToggle IGNORES_OTHER_FLUIDS = new BooleanToggle(
				false,
				() -> KnowledgesConfig.infoFluidIgnoresOtherFluids,
				value -> KnowledgesConfig.infoFluidIgnoresOtherFluids = value
		);
	}

	static double globalMainScalar = Global.MAIN_SCALAR.defaultValue();
	static double globalCrosshairSafeAreaScalar = Global.CROSSHAIR_SAFE_AREA_SCALAR.defaultValue();

	@Table("crosshair") static boolean crosshairCusrorRingOutline = Crosshair.CURSOR_RING_OUTLINE.defaultValue();
	@Table("crosshair") static boolean crosshairTextsRight = Crosshair.TEXTS_RIGHT.defaultValue();
	@Table("crosshair") static boolean crosshairTextsLeft = Crosshair.TEXTS_LEFT.defaultValue();
	@Table("crosshair") static boolean crosshairSubtitles = Crosshair.SUBTITLES.defaultValue();

	@Table("info.block") static boolean infoBlockShowPoweredStatus = InfoBlock.SHOW_POWERED_STATUS.defaultValue();

	@Table("info.fluid") static boolean infoFluidIgnoresWater = InfoFluid.IGNORES_WATER.defaultValue();
	@Table("info.fluid") static boolean infoFluidIgnoresLava = InfoFluid.IGNORES_LAVA.defaultValue();
	@Table("info.fluid") static boolean infoFluidIgnoresOtherFluids = InfoFluid.IGNORES_OTHER_FLUIDS.defaultValue();
}
