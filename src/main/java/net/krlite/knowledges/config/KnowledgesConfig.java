package net.krlite.knowledges.config;

import net.fabricmc.loader.api.FabricLoader;
import net.krlite.equator.math.algebra.Theory;
import net.krlite.knowledges.Knowledges;
import net.krlite.pierced.annotation.Silent;
import net.krlite.pierced.annotation.Table;
import net.krlite.pierced.config.Pierced;

import java.io.File;

public class KnowledgesConfig extends Pierced {
	private static final @Silent File file = FabricLoader.getInstance().getConfigDir().resolve(Knowledges.ID + ".toml").toFile();

	public KnowledgesConfig() {
		super(KnowledgesConfig.class, file);
		load();
	}

	public static class Default {
		public static final double CROSSHAIR_SAFE_AREA_SCALAR = 1, CROSSHAIR_SAFE_AREA_SCALAR_MIN = 0, CROSSHAIR_SAFE_AREA_SCALAR_MAX = 3;
		public static final double MAIN_SCALAR = 1, MAIN_SCALAR_MIN = 0, MAIN_SCALAR_MAX = 3;
		public static boolean INFO_TEXTS_RIGHT_ENABLED = true;
		public static boolean INFO_TEXTS_LEFT_ENABLED = true;
		public static boolean INFO_SUBTITLES_ENABLED = true;
		public static boolean INFO_FLUID_IGNORES_WATER = false;
		public static boolean INFO_FLUID_IGNORES_LAVA = false;
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

	@Table("global")
	private double mainScalar = Default.MAIN_SCALAR;

	public double mainScalar() {
		return mainScalar;
	}

	public void mainScalar(double scalar) {
		this.mainScalar = Theory.clamp(scalar, Default.MAIN_SCALAR_MIN, Default.MAIN_SCALAR_MAX);
		save();
	}

	@Table("info")
	private boolean infoTextsRightEnabled = Default.INFO_TEXTS_RIGHT_ENABLED;

	public boolean infoTextsRightEnabled() {
		return infoTextsRightEnabled;
	}

	public void infoTextsRightEnabled(boolean flag) {
		infoTextsRightEnabled = flag;
	}

	@Table("info")
	private boolean infoTextsLeftEnabled = Default.INFO_TEXTS_LEFT_ENABLED;

	public boolean infoTextsLeftEnabled() {
		return infoTextsLeftEnabled;
	}

	public void infoTextsLeftEnabled(boolean flag) {
		infoTextsLeftEnabled = flag;
	}

	@Table("info")
	private boolean infoSubtitlesEnabled = Default.INFO_SUBTITLES_ENABLED;

	public boolean infoSubtitlesEnabled() {
		return infoSubtitlesEnabled;
	}

	public void infoSubtitlesEnabled(boolean flag) {
		infoSubtitlesEnabled = flag;
	}

	@Table("info")
	private boolean infoFluidIgnoresWater = Default.INFO_FLUID_IGNORES_WATER;

	public boolean infoFluidIgnoresWater() {
		return infoFluidIgnoresWater;
	}

	public void infoFluidIgnoresWater(boolean flag) {
		infoFluidIgnoresWater = flag;
	}

	@Table("info")
	private boolean infoFluidIgnoresLava = Default.INFO_FLUID_IGNORES_LAVA;

	public boolean infoFluidIgnoresLava() {
		return infoFluidIgnoresLava;
	}

	public void infoFluidIgnoresLava(boolean flag) {
		infoFluidIgnoresLava = flag;
	}
}
