package net.krlite.knowledges.config;

import net.fabricmc.loader.api.FabricLoader;
import net.krlite.equator.math.algebra.Theory;
import net.krlite.equator.visual.animation.interpolated.InterpolatedDouble;
import net.krlite.knowledges.Knowledges;
import net.krlite.pierced.annotation.Silent;
import net.krlite.pierced.config.Pierced;
import net.minecraft.text.MutableText;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class KnowledgesConfig extends Pierced {
	private static final @Silent File file = FabricLoader.getInstance().getConfigDir().resolve(Knowledges.ID + ".toml").toFile();

	public KnowledgesConfig() {
		super(KnowledgesConfig.class, file);
		load();
	}

	// SILENT
	// Block Breaking Progress

	private @Silent final InterpolatedDouble blockBreakingProgress = new InterpolatedDouble(0, 0.021);

	public double blockBreakingProgress() {
		return blockBreakingProgress.value();
	}

	public void blockBreakingProgress(double progress) {
		blockBreakingProgress.target(Theory.clamp(progress, 0, 1));
	}

	// SILENT
	// Last Shown Block Name

	private @Silent MutableText lastShownBlockName = null;

	@Nullable
	public MutableText lastShownBlockName() {
		return lastShownBlockName;
	}

	public void lastShownBlockName(MutableText text) {
		lastShownBlockName = text;
	}

	// Crosshair Safe Area Size Scalar

	private double crosshairSafeAreaSizeScalar = 1;

	public double crosshairSafeAreaSizeScalar() {
		return crosshairSafeAreaSizeScalar;
	}

	public void crosshairSafeAreaSizeScalar(double scalar) {
		crosshairSafeAreaSizeScalar = Theory.clamp(scalar, 0, 1);
		save();
	}

	// Scalar

	private double scalar = 1;

	public double scalar() {
		return scalar;
	}

	public void scalar(double scalar) {
		this.scalar = Theory.clamp(scalar, 0, 1);
		save();
	}
}
