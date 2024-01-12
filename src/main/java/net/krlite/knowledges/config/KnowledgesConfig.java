package net.krlite.knowledges.config;

import net.krlite.knowledges.Knowledges;
import net.krlite.knowledges.data.info.block.blockinformation.NoteBlockInformationData;
import net.krlite.pierced.annotation.Silent;
import net.krlite.pierced.annotation.Table;
import net.krlite.pierced.config.Pierced;

import java.io.File;
import java.lang.reflect.Field;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class KnowledgesConfig extends Pierced {
	private static final @Silent File file = Knowledges.CONFIG_PATH.resolve("config.toml").toFile();

	private static final @Silent KnowledgesConfig self = new KnowledgesConfig();

	KnowledgesConfig() {
		super(KnowledgesConfig.class, file);
	}

	static {
		try {
			String name = Component.Crosshair.class.getField("TEXTS_LEFT").getDeclaringClass().getName();
			System.out.println(name.replace(KnowledgesConfig.class.getName(), "").replace("$", ""));
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	public static void loadStatic() {
		self.load();
	}

	public static void saveStatic() {
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

		public WithDefault(V defaultValue, Class<?> declearingClass, String fieldNameUpperSnakeCase) {
			// Auto bind
			Field target;
			String className = toUpperCamelCase(declearingClass.getName()
					.replace(KnowledgesConfig.class.getName(), "")
					.split("\\$"));
			String targetFieldName = decapitalize(className + toUpperCamelCase(fieldNameUpperSnakeCase.split("_")));

			try {
				target = KnowledgesConfig.class.getDeclaredField(targetFieldName);
			} catch (NoSuchFieldException e) {
				throw new RuntimeException(e);
			}

			this.defaultValue = defaultValue;
			this.supplier = () -> {
				try {
					return (V) target.get(KnowledgesConfig.class);
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			};
			this.consumer = value -> {
				try {
					target.set(KnowledgesConfig.class, value);
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			};
        }

		protected static String toUpperCamelCase(String[] strings) {
			StringBuilder builder = new StringBuilder();
			for (String string : strings) {
				if (!string.isEmpty()) {
					builder
							.append(String.valueOf(string.charAt(0)).toUpperCase())
							.append(string.substring(1).toLowerCase());
				}
			}
			return builder.toString();
		}

		protected static String decapitalize(String string) {
			if (string.isEmpty()) return string;
			return String.valueOf(string.charAt(0)).toLowerCase() + string.substring(1);
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

		public Range(V defaultValue, V minValue, V maxValue, Class<?> declearingClass, String fieldName) {
			super(defaultValue, declearingClass, fieldName);
			this.minValue = minValue;
			this.maxValue = maxValue;
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

	public static class EnumSelector<E extends Enum<E>> extends WithDefault<E> {
		private final Class<E> enumClass;

		public EnumSelector(Class<E> enumClass, E defaultValue, Supplier<E> supplier, Consumer<E> consumer) {
			super(defaultValue, supplier, consumer);
			this.enumClass = enumClass;
		}

		public Class<E> enumClass() {
			return enumClass;
		}
	}

	public static class Global {
		public static final Range<Double> MAIN_SCALAR = new Range<>(
				0.5, 0.0, 2.0,
				Global.class, "MAIN_SCALAR"
		);
		public static final Range<Double> CROSSHAIR_SAFE_AREA_SCALAR = new Range<>(
				0.5, 0.0, 2.0,
				Global.class, "CROSSHAIR_SAFE_AREA_SCALAR"
		);
	}

	public static class Component {
		public static class Crosshair {
			public static final BooleanToggle CURSOR_RING_OUTLINE = new BooleanToggle(
					true,
					() -> KnowledgesConfig.componentCrosshairCusrorRingOutline,
					value -> KnowledgesConfig.componentCrosshairCusrorRingOutline = value
			);
			public static final BooleanToggle TEXTS_RIGHT = new BooleanToggle(
					true,
					() -> KnowledgesConfig.componentCrosshairTextsRight,
					value -> KnowledgesConfig.componentCrosshairTextsRight = value
			);
			public static final BooleanToggle TEXTS_LEFT = new BooleanToggle(
					true,
					() -> KnowledgesConfig.componentCrosshairTextsLeft,
					value -> KnowledgesConfig.componentCrosshairTextsLeft = value
			);
			public static final BooleanToggle SUBTITLES = new BooleanToggle(
					true,
					() -> KnowledgesConfig.componentCrosshairSubtitles,
					value -> KnowledgesConfig.componentCrosshairSubtitles = value
			);
			public static final BooleanToggle SHOW_NUMERIC_HEALTH = new BooleanToggle(
					false,
					() -> KnowledgesConfig.componentCrosshairShowNumericHealth,
					value -> KnowledgesConfig.componentCrosshairShowNumericHealth = value
			);
		}

		public static class InfoBlock {
			public static final BooleanToggle SHOW_POWERED_STATUS = new BooleanToggle(
					true,
					() -> KnowledgesConfig.componentInfoBlockShowPoweredStatus,
					value -> KnowledgesConfig.componentInfoBlockShowPoweredStatus = value
			);
		}

		public static class InfoFluid {
			public static final BooleanToggle IGNORES_WATER = new BooleanToggle(
					false,
					() -> KnowledgesConfig.componentInfoFluidIgnoresWater,
					value -> KnowledgesConfig.componentInfoFluidIgnoresWater = value
			);
			public static final BooleanToggle IGNORES_LAVA = new BooleanToggle(
					false,
					() -> KnowledgesConfig.componentInfoFluidIgnoresLava,
					value -> KnowledgesConfig.componentInfoFluidIgnoresLava = value
			);
			public static final BooleanToggle IGNORES_OTHER_FLUIDS = new BooleanToggle(
					false,
					() -> KnowledgesConfig.componentInfoFluidIgnoresOtherFluids,
					value -> KnowledgesConfig.componentInfoFluidIgnoresOtherFluids = value
			);
		}
	}

	public static class Data {
		public static class NoteBlockInformation {
			public static final EnumSelector<NoteBlockInformationData.NoteModifier> NOTE_MODIFIER = new EnumSelector<>(
					NoteBlockInformationData.NoteModifier.class,
					NoteBlockInformationData.NoteModifier.SHARPS,
					() -> KnowledgesConfig.dataNoteBlockInformationNoteModifier,
					value -> KnowledgesConfig.dataNoteBlockInformationNoteModifier = value
			);
			public static final EnumSelector<NoteBlockInformationData.MusicalAlphabet> MUSICAL_ALPHABET = new EnumSelector<>(
					NoteBlockInformationData.MusicalAlphabet.class,
					NoteBlockInformationData.MusicalAlphabet.ENGLISH,
					() -> KnowledgesConfig.dataNoteBlockInformationMusicalAlphabet,
					value -> KnowledgesConfig.dataNoteBlockInformationMusicalAlphabet = value
			);
		}
	}

	static double globalMainScalar = Global.MAIN_SCALAR.defaultValue();
	static double globalCrosshairSafeAreaScalar = Global.CROSSHAIR_SAFE_AREA_SCALAR.defaultValue();

	@Table("component.crosshair") static boolean componentCrosshairCusrorRingOutline = Component.Crosshair.CURSOR_RING_OUTLINE.defaultValue();
	@Table("component.crosshair") static boolean componentCrosshairTextsRight = Component.Crosshair.TEXTS_RIGHT.defaultValue();
	@Table("component.crosshair") static boolean componentCrosshairTextsLeft = Component.Crosshair.TEXTS_LEFT.defaultValue();
	@Table("component.crosshair") static boolean componentCrosshairSubtitles = Component.Crosshair.SUBTITLES.defaultValue();
	@Table("component.crosshair") static boolean componentCrosshairShowNumericHealth = Component.Crosshair.SHOW_NUMERIC_HEALTH.defaultValue();

	@Table("component.info.block") static boolean componentInfoBlockShowPoweredStatus = Component.InfoBlock.SHOW_POWERED_STATUS.defaultValue();

	@Table("component.info.fluid") static boolean componentInfoFluidIgnoresWater = Component.InfoFluid.IGNORES_WATER.defaultValue();
	@Table("component.info.fluid") static boolean componentInfoFluidIgnoresLava = Component.InfoFluid.IGNORES_LAVA.defaultValue();
	@Table("component.info.fluid") static boolean componentInfoFluidIgnoresOtherFluids = Component.InfoFluid.IGNORES_OTHER_FLUIDS.defaultValue();

	@Table("data.info.block.block_information.note_block") static NoteBlockInformationData.NoteModifier dataNoteBlockInformationNoteModifier = Data.NoteBlockInformation.NOTE_MODIFIER.defaultValue();
	@Table("data.info.block.block_information.note_block") static NoteBlockInformationData.MusicalAlphabet dataNoteBlockInformationMusicalAlphabet = Data.NoteBlockInformation.MUSICAL_ALPHABET.defaultValue();
}
