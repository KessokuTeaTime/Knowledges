package net.krlite.knowledges;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.krlite.knowledges.components.ArmorDurabilityComponent;
import net.krlite.knowledges.components.BlockInfoComponent;
import net.krlite.knowledges.config.KnowledgesConfig;
import net.minecraft.client.util.math.MatrixStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class Knowledges implements ModInitializer {
	public static final String NAME = "Knowledges", ID = "knowledges";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	public static final KnowledgesConfig CONFIG = new KnowledgesConfig();
	public static final ArrayList<Knowledge> KNOWLEDGES = new ArrayList<>();

	public static class Constants {
		public static double MAX_TEXT_SHIFT = 20, MIN_TEXT_SHIFT = 0;

		public static double MAX_TEXT_SHRINK = 1, MIN_TEXT_SHRINK = 0;
	}

	public static class Animations {
		static void registerEvents() {
			ClientTickEvents.END_CLIENT_TICK.register(client -> {
				if (client.player != null) {

				}
			});
		}
	}

	@Override
	public void onInitialize() {
		Animations.registerEvents();

		LOGGER.info("Initializing default components for " + NAME + "...");
		KNOWLEDGES.add(new BlockInfoComponent());
		KNOWLEDGES.add(new ArmorDurabilityComponent());
		LOGGER.info("Finished initializing default components for " + NAME + ".");

		// TODO: Implement more components

		LOGGER.info("You're now full of knowledge! 📚");
	}

	public static void renderKnowledges(MatrixStack matrixStack) {
		KNOWLEDGES.forEach(knowledge -> knowledge.render(matrixStack));
	}

	public static double mapToPower(double x, double power, double threshold) {
		return threshold + (1 - threshold) * Math.pow(x, power);
	}
}
