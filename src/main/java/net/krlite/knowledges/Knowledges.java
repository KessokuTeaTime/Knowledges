package net.krlite.knowledges;

import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Knowledges implements ModInitializer {
	public static final String NAME = "Knowledges", ID = "knowledges";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	@Override
	public void onInitialize() {
	}

	public static double getX() {
		return MinecraftClient.getInstance().getWindow().getScaledWidth() / 2.0;
	}

	public static double getY() {
		return MinecraftClient.getInstance().getWindow().getScaledHeight() / 2.0 + 18;
	}
}
