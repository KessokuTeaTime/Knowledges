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
		public static final double GLOBAL_MAIN_SCALAR = 1, GLOBAL_MAIN_SCALAR_MIN = 0, GLOBAL_MAIN_SCALAR_MAX = 3;
		public static final double GLOBAL_CROSSHAIR_SAFE_AREA_SCALAR = 1, GLOBAL_CROSSHAIR_SAFE_AREA_SCALAR_MIN = 0, GLOBAL_CROSSHAIR_SAFE_AREA_SCALAR_MAX = 3;

		public static final boolean CROSSHAIR_CURSOR_RING_OUTLINE = true;
		public static final boolean CROSSHAIR_TEXTS_RIGHT = true;
		public static final boolean CROSSHAIR_TEXTS_LEFT = true;
		public static final boolean CROSSHAIR_SUBTITLES = true;

		public static final boolean INFO_BLOCK_SHOW_POWERED_STATUS = true;

		public static final boolean INFO_FLUID_IGNORES_WATER = false;
		public static final boolean INFO_FLUID_IGNORES_LAVA = false;
		public static final boolean INFO_FLUID_IGNORES_OTHER_FLUIDS = false;
	}

	private double globalMainScalar = Default.GLOBAL_MAIN_SCALAR;

	public double globalMainScalar() {
		return globalMainScalar;
	}

	public void globalMainScalar(double scalar) {
		this.globalMainScalar = Theory.clamp(scalar, Default.GLOBAL_MAIN_SCALAR_MIN, Default.GLOBAL_MAIN_SCALAR_MAX);
	}

	private double globalCrosshairSafeAreaScalar = Default.GLOBAL_CROSSHAIR_SAFE_AREA_SCALAR;

	public double globalCrosshairSafeAreaScalar() {
		return globalCrosshairSafeAreaScalar;
	}

	public void globalCrosshairSafeAreaScalar(double scalar) {
		globalCrosshairSafeAreaScalar = Theory.clamp(scalar, Default.GLOBAL_CROSSHAIR_SAFE_AREA_SCALAR_MIN, Default.GLOBAL_CROSSHAIR_SAFE_AREA_SCALAR_MAX);
	}

	@Table("crosshair")
	private boolean crosshairCusrorRingOutline = Default.CROSSHAIR_CURSOR_RING_OUTLINE;

	public boolean crosshairCursorRingOutline() {
		return crosshairCusrorRingOutline;
	}

	public void crosshairCursorRingOutline(boolean flag) {
		crosshairCusrorRingOutline = flag;
	}

	@Table("crosshair")
	private boolean crosshairTextsRightEnabled = Default.CROSSHAIR_TEXTS_RIGHT;

	public boolean crosshairTextsRightEnabled() {
		return crosshairTextsRightEnabled;
	}

	public void crosshairTextsRightEnabled(boolean flag) {
		crosshairTextsRightEnabled = flag;
	}

	@Table("crosshair")
	private boolean crosshairTextsLeftEnabled = Default.CROSSHAIR_TEXTS_LEFT;

	public boolean crosshairTextsLeftEnabled() {
		return crosshairTextsLeftEnabled;
	}

	public void crosshairTextsLeftEnabled(boolean flag) {
		crosshairTextsLeftEnabled = flag;
	}

	@Table("crosshair")
	private boolean crosshairSubtitlesEnabled = Default.CROSSHAIR_SUBTITLES;

	public boolean crosshairSubtitlesEnabled() {
		return crosshairSubtitlesEnabled;
	}

	public void crosshairSubtitlesEnabled(boolean flag) {
		crosshairSubtitlesEnabled = flag;
	}

	@Table("info.block")
	private boolean infoBlockShowPoweredStatus = Default.INFO_BLOCK_SHOW_POWERED_STATUS;

	public boolean infoBlockShowPoweredStatus() {
		return infoBlockShowPoweredStatus;
	}

	public void infoBlockShowPoweredStatus(boolean flag) {
		infoBlockShowPoweredStatus = flag;
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
