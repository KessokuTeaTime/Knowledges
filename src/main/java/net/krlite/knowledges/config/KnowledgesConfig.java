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

	@Table("global")
	private double crosshairSafeAreaSizeScalar = 1;

	public double crosshairSafeAreaSizeScalar() {
		return crosshairSafeAreaSizeScalar;
	}

	public void crosshairSafeAreaSizeScalar(double scalar) {
		crosshairSafeAreaSizeScalar = Theory.clamp(scalar, 0, 2);
		save();
	}

	@Table("global")
	private double scalar = 1;

	public double scalar() {
		return scalar;
	}

	public void scalar(double scalar) {
		this.scalar = Theory.clamp(scalar, 0, 2);
		save();
	}

	@Table("info")
	private boolean infoTextsRightEnabled = true;

	public boolean infoTextsRightEnabled() {
		return infoTextsRightEnabled;
	}

	public void infoTextsRightEnabled(boolean flag) {
		infoTextsRightEnabled = flag;
	}

	@Table("info")
	private boolean infoTextsLeftEnabled = true;

	public boolean infoTextsLeftEnabled() {
		return infoTextsLeftEnabled;
	}

	public void infoTextsLeftEnabled(boolean flag) {
		infoTextsLeftEnabled = flag;
	}

	@Table("info")
	private boolean infoSubtitlesEnabled = true;

	public boolean infoSubtitlesEnabled() {
		return infoSubtitlesEnabled;
	}

	public void infoSubtitlesEnabled(boolean flag) {
		infoSubtitlesEnabled = flag;
	}
}
