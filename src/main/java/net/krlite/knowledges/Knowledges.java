package net.krlite.knowledges;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.krlite.knowledges.api.Knowledge;
import net.krlite.knowledges.api.KnowledgeContainer;
import net.krlite.knowledges.components.InfoComponent;
import net.krlite.knowledges.config.KnowledgesBanList;
import net.krlite.knowledges.config.KnowledgesConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class Knowledges implements ModInitializer {
	public static final String NAME = "Knowledges", ID = "knowledges";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	public static final KnowledgesConfig CONFIG = new KnowledgesConfig();
	private static final KnowledgesBanList banList = new KnowledgesBanList();
	private static final ArrayList<Knowledge> knowledges = new ArrayList<>();
	private static int knowledgesCount = 0;

	public static int knowledgesCount() {
		return knowledgesCount;
	}

	public static ArrayList<Knowledge> knowledges() {
		return new ArrayList<>(knowledges);
	}

	public static String localizationKey(String category, String... paths) {
		return category + "." + ID + "." + String.join(".", paths);
	}

	public static MutableText localize(String key) {
		return Text.translatable(key);
	}

	public static MutableText localize(String category, String... paths) {
		return localize(localizationKey(category, paths));
	}

	private static void register(Knowledge knowledge) {
		knowledges.add(knowledge);
	}

	public static boolean knowledgeState(Knowledge knowledge) {
		return !banList.isBanned(knowledge.name());
	}

	public static void knowledgeState(Knowledge knowledge, boolean state) {
		banList.setBanned(knowledge.name(), !state);
	}

	@Override
	public void onInitialize() {
		InfoComponent.Animations.registerEvents();

		LOGGER.info("Initializing components for " + NAME + "...");

		FabricLoader.getInstance().getEntrypointContainers("knowledges", KnowledgeContainer.class).forEach(entrypoint -> {
			KnowledgeContainer container = entrypoint.getEntrypoint();
			container.register().forEach(Knowledges::register);

			knowledgesCount += container.register().size();
		});

		LOGGER.info("Successfully registered " + knowledgesCount + " knowledge. You're now full of knowledge! 📚");
	}

	public static void render(
			@NotNull DrawContext context, @NotNull MinecraftClient client,
			@NotNull PlayerEntity player, @NotNull ClientWorld world
	) {
		knowledges.forEach(knowledge -> {
			if (!banList.isBanned(knowledge.name()))
				knowledge.render(context, client, player, world);
		});
	}

	public static double mapToPower(double x, double power, double threshold) {
		return threshold + (1 - threshold) * Math.pow(x, power);
	}
}
