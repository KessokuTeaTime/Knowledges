package net.krlite.knowledges.config;

import net.krlite.equator.math.algebra.Theory;
import net.krlite.knowledges.Knowledges;
import net.krlite.pierced.annotation.Silent;
import net.krlite.pierced.annotation.Table;
import net.krlite.pierced.config.Pierced;

import java.io.File;

public class KnowledgesConfig extends Pierced {
	private static final @Silent File file = Knowledges.CONFIG_PATH.resolve("config.toml").toFile();

	public KnowledgesConfig() {
		super(KnowledgesConfig.class, file);
		load();
	}

	public static class Default {
		public static final double MAIN_SCALAR = 1, MAIN_SCALAR_MIN = 0, MAIN_SCALAR_MAX = 3;
		public static final double CROSSHAIR_SAFE_AREA_SCALAR = 1, CROSSHAIR_SAFE_AREA_SCALAR_MIN = 0, CROSSHAIR_SAFE_AREA_SCALAR_MAX = 3;
		public static final boolean CROSSHAIR_TEXTS_RIGHT_ENABLED = true;
		public static final boolean CROSSHAIR_TEXTS_LEFT_ENABLED = true;
		public static final boolean CROSSHAIR_SUBTITLES_ENABLED = true;

		public static final boolean INFO_FLUID_IGNORES_WATER = false;
		public static final boolean INFO_FLUID_IGNORES_LAVA = false;
		public static final boolean INFO_FLUID_IGNORES_OTHER_FLUIDS = false;
	}

	@Table("global")
	private double mainScalar = Default.MAIN_SCALAR;

	public double mainScalar() {
		return mainScalar;
	}

	public void mainScalar(double scalar) {
		this.mainScalar = Theory.clamp(scalar, Default.MAIN_SCALAR_MIN, Default.MAIN_SCALAR_MAX);
		save();
	}

	@Table("global")
	private double crosshairSafeAreaScalar = Default.CROSSHAIR_SAFE_AREA_SCALAR;

	public double crosshairSafeAreaScalar() {
		return crosshairSafeAreaScalar;
	}

	public void crosshairSafeAreaScalar(double scalar) {
		crosshairSafeAreaScalar = Theory.clamp(scalar, Default.CROSSHAIR_SAFE_AREA_SCALAR_MIN, Default.CROSSHAIR_SAFE_AREA_SCALAR_MAX);
		save();
	}

	@Table("crosshair")
	private boolean crosshairTextsRightEnabled = Default.CROSSHAIR_TEXTS_RIGHT_ENABLED;

	public boolean crosshairTextsRightEnabled() {
		return crosshairTextsRightEnabled;
	}

	public void crosshairTextsRightEnabled(boolean flag) {
		crosshairTextsRightEnabled = flag;
	}

	@Table("crosshair")
	private boolean crosshairTextsLeftEnabled = Default.CROSSHAIR_TEXTS_LEFT_ENABLED;

	public boolean crosshairTextsLeftEnabled() {
		return crosshairTextsLeftEnabled;
	}

	public void crosshairTextsLeftEnabled(boolean flag) {
		crosshairTextsLeftEnabled = flag;
	}

	@Table("crosshair")
	private boolean crosshairSubtitlesEnabled = Default.CROSSHAIR_SUBTITLES_ENABLED;

	public boolean crosshairSubtitlesEnabled() {
		return crosshairSubtitlesEnabled;
	}

	public void crosshairSubtitlesEnabled(boolean flag) {
		crosshairSubtitlesEnabled = flag;
	}

	@Table("info.fluid")
	private boolean infoFluidIgnoresWater = Default.INFO_FLUID_IGNORES_WATER;

	public boolean infoFluidIgnoresWater() {
		return infoFluidIgnoresWater;
	}

	public void infoFluidIgnoresWater(boolean flag) {
		infoFluidIgnoresWater = flag;
	}

	@Table("info.fluid")
	private boolean infoFluidIgnoresLava = Default.INFO_FLUID_IGNORES_LAVA;

	public boolean infoFluidIgnoresLava() {
		return infoFluidIgnoresLava;
	}

	public void infoFluidIgnoresLava(boolean flag) {
		infoFluidIgnoresLava = flag;
	}

	@Table("info.fluid")
	private boolean infoFluidIgnoresOtherFluids = Default.INFO_FLUID_IGNORES_OTHER_FLUIDS;

	public boolean infoFluidIgnoresOtherFluids() {
		return infoFluidIgnoresOtherFluids;
	}

	public void infoFluidIgnoresOtherFluids(boolean flag) {
		infoFluidIgnoresOtherFluids = flag;
	}
}
