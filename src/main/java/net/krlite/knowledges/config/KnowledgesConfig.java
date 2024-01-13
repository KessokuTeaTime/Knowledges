package net.krlite.knowledges.config;

import net.krlite.knowledges.Knowledges;
import net.krlite.knowledges.data.info.block.blockinformation.NoteBlockInformationData;
import net.krlite.pierced.annotation.Silent;
import net.krlite.pierced.annotation.Table;
import net.krlite.pierced.config.Pierced;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class KnowledgesConfig extends Pierced {
	private static final @Silent File file = Knowledges.CONFIG_PATH.resolve("config.toml").toFile();

	private static final @Silent KnowledgesConfig self = new KnowledgesConfig();

	KnowledgesConfig() {
		super(KnowledgesConfig.class, file);
	}

	protected static void forceInit(Class<?> clazz) {
		try {
			Class.forName(clazz.getName(), true, clazz.getClassLoader());
		} catch (ClassNotFoundException e) {
			throw new AssertionError(e);
		}
	}

	protected static void forceInitAllDeclearedClasses(Class<?> clazz) {
		forceInit(clazz);
		Arrays.stream(clazz.getDeclaredClasses()).forEach(KnowledgesConfig::forceInitAllDeclearedClasses);
	}

	public static void loadStatic() {
		self.load();
		forceInitAllDeclearedClasses(KnowledgesConfig.class);
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

		public WithDefault(V defaultValue, Class<?> declearingClass, String fieldName) {
			// Auto bind
			Field target;
			String className = declearingClass.getName()
					.replace(KnowledgesConfig.class.getName(), "")
					.replace("$", "");
			String targetFieldName = decapitalize(className + toUpperCamelCase(fieldName.split("_")));

			try {
				target = KnowledgesConfig.class.getDeclaredField(targetFieldName);
				target.setAccessible(true);
			} catch (NoSuchFieldException e) {
				throw new RuntimeException(e);
			}

			this.defaultValue = defaultValue;
			this.supplier = () -> {
				try {
					@Nullable V value = (V) target.get(KnowledgesConfig.class);
					return value != null ? value : defaultValue();
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

			// Initialization
			try {
				@Nullable V value = (V) target.get(KnowledgesConfig.class);
				if (value == null) target.set(KnowledgesConfig.class, defaultValue);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
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

		public BooleanToggle(Boolean defaultValue, Class<?> declearedClass, String fieldName) {
			super(defaultValue, declearedClass, fieldName);
		}
	}

	public static class EnumSelector<E extends Enum<E>> extends WithDefault<E> {
		private final Class<E> enumClass;

		public EnumSelector(Class<E> enumClass, E defaultValue, Supplier<E> supplier, Consumer<E> consumer) {
			super(defaultValue, supplier, consumer);
			this.enumClass = enumClass;
		}

		public EnumSelector(Class<E> enumClass, E defaultValue, Class<?> declearedClass, String fieldName) {
			super(defaultValue, declearedClass, fieldName);
			this.enumClass = enumClass;
		}

		public Class<E> enumClass() {
			return enumClass;
		}
	}

	public static class Global {
		public static final Range<Double> MAIN_SCALAR = new Range<>(
				1.0, 0.5, 2.0,
				Global.class, "MAIN_SCALAR"
		);
		public static final Range<Double> CROSSHAIR_SAFE_AREA_SCALAR = new Range<>(
				1.0, 0.5, 2.0,
				Global.class, "CROSSHAIR_SAFE_AREA_SCALAR"
		);
	}

	public static class Component {
		public static class Crosshair {
			public static final BooleanToggle CURSOR_RING_OUTLINE = new BooleanToggle(
					true,
					Crosshair.class, "CURSOR_RING_OUTLINE"
			);
			public static final BooleanToggle TEXTS_RIGHT = new BooleanToggle(
					true,
					Crosshair.class, "TEXTS_RIGHT"
			);
			public static final BooleanToggle TEXTS_LEFT = new BooleanToggle(
					true,
					Crosshair.class, "TEXTS_LEFT"
			);
			public static final BooleanToggle SUBTITLES = new BooleanToggle(
					true,
					Crosshair.class, "SUBTITLES"
			);
		}

		public static class InfoBlock {
			public static final BooleanToggle BLOCK_POWERED_STATUS = new BooleanToggle(
					true,
					InfoBlock.class, "BLOCK_POWERED_STATUS"
			);
		}

		public static class InfoEntity {
			public static final BooleanToggle NUMERIC_HEALTH = new BooleanToggle(
					false,
					InfoEntity.class, "NUMERIC_HEALTH"
			);
		}

		public static class InfoFluid {
			public static final BooleanToggle IGNORES_WATER = new BooleanToggle(
					false,
					InfoFluid.class, "IGNORES_WATER"
			);
			public static final BooleanToggle IGNORES_LAVA = new BooleanToggle(
					false,
					InfoFluid.class, "IGNORES_LAVA"
			);
			public static final BooleanToggle IGNORES_OTHER_FLUIDS = new BooleanToggle(
					false,
					InfoFluid.class, "IGNORES_OTHER_FLUIDS"
			);
		}
	}

	public static class Data {
		public static class NoteBlockInformation {
			public static final EnumSelector<NoteBlockInformationData.NoteModifier> NOTE_MODIFIER = new EnumSelector<>(
					NoteBlockInformationData.NoteModifier.class,
					NoteBlockInformationData.NoteModifier.SHARPS,
					NoteBlockInformation.class, "NOTE_MODIFIER"
			);
			public static final EnumSelector<NoteBlockInformationData.MusicalAlphabet> MUSICAL_ALPHABET = new EnumSelector<>(
					NoteBlockInformationData.MusicalAlphabet.class,
					NoteBlockInformationData.MusicalAlphabet.ENGLISH,
					NoteBlockInformation.class, "MUSICAL_ALPHABET"
			);
		}
	}

	static Double globalMainScalar;
	static Double globalCrosshairSafeAreaScalar;

	@Table("component.crosshair") static Boolean componentCrosshairCursorRingOutline;
	@Table("component.crosshair") static Boolean componentCrosshairTextsRight;
	@Table("component.crosshair") static Boolean componentCrosshairTextsLeft;
	@Table("component.crosshair") static Boolean componentCrosshairSubtitles;

	@Table("component.info.block") static Boolean componentInfoBlockBlockPoweredStatus;

	@Table("component.info.entity") static Boolean componentInfoEntityNumericHealth;

	@Table("component.info.fluid") static Boolean componentInfoFluidIgnoresWater;
	@Table("component.info.fluid") static Boolean componentInfoFluidIgnoresLava;
	@Table("component.info.fluid") static Boolean componentInfoFluidIgnoresOtherFluids;

	@Table("data.info.block.block_information.note_block") static NoteBlockInformationData.NoteModifier dataNoteBlockInformationNoteModifier;
	@Table("data.info.block.block_information.note_block") static NoteBlockInformationData.MusicalAlphabet dataNoteBlockInformationMusicalAlphabet;
}
