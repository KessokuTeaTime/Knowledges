package net.krlite.knowledges;

import net.fabricmc.api.ModInitializer;
import net.krlite.equator.visual.animation.Interpolation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Knowledges implements ModInitializer {
	public static final String NAME = "Knowledges", ID = "knowledges";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	private static final Interpolation blockBreakingProgress = new Interpolation(0, 0, 45);

	@Override
	public void onInitialize() {
	}

	public static double blockBreakingProgress() {
		return blockBreakingProgress.value();
	}

	public static void blockBreakingProgress(double progress) {
		blockBreakingProgress.targetValue(progress);
	}
}
