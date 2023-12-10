package net.krlite.knowledges;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.krlite.knowledges.api.Data;
import net.krlite.knowledges.api.Knowledge;
import net.krlite.knowledges.api.entrypoints.ComponentProvider;
import net.krlite.knowledges.api.entrypoints.DataProvider;
import net.krlite.knowledges.components.InfoComponent;
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

import java.nio.file.Path;

public class Knowledges implements ModInitializer {
	public static final String NAME = "Knowledges", ID = "knowledges";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);
	public static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve(Knowledges.ID);

	public static final KnowledgesConfig CONFIG = new KnowledgesConfig();
	public static final ComponentsManager COMPONENTS = new ComponentsManager();
	public static final DataManager DATA = new DataManager();

	public static String localizationKey(String category, String... paths) {
		return category + "." + ID + "." + String.join(".", paths);
	}

	public static String localizationKey(Knowledge knowledge, String... paths) {
		String namespace = COMPONENTS.namespace(knowledge).orElse(ID);
		return "knowledge." + namespace + "." + String.join(".", paths);
	}

	public static String localizationKey(Data<?, ?> data, String... paths) {
		String namespace = DATA.namespace(data).orElse(ID);
		return "knowledge_data." + namespace + "." + String.join(".", paths);
	}

	public static MutableText localize(String category, String... paths) {
		return Text.translatable(localizationKey(category, paths));
	}

	public static MutableText localize(Knowledge knowledge, String... paths) {
		return Text.translatable(localizationKey(knowledge, paths));
	}

	public static MutableText localize(Data<?, ?> data, String... paths) {
		return Text.translatable(localizationKey(data, paths));
	}

	@Override
	public void onInitialize() {
		InfoComponent.Animations.registerEvents();

		// Components
		FabricLoader.getInstance().getEntrypointContainers(ID, ComponentProvider.class).forEach(entrypoint -> {
			ComponentProvider provider = entrypoint.getEntrypoint();
			var classes = provider.provide();
			if (classes.isEmpty()) return;

			ModContainer mod = entrypoint.getProvider();
			String namespace = mod.getMetadata().getId(), name = mod.getMetadata().getName();

			LOGGER.info(String.format(
					"Registering %d %s for %s.",
					classes.size(),
					classes.size() <= 1 ? "knowledge" : "knowledges",
					name
			));

			classes.stream()
					.map(clazz -> {
						try {
							return clazz.getDeclaredConstructor().newInstance();
						} catch (Throwable throwable) {
							throw new RuntimeException(String.format(
									"Failed to register knowledge for %s: constructor not found",
									clazz.getName()
							), throwable);
						}
					})
					.forEach(knowledge -> COMPONENTS.register(namespace, knowledge));
		});

		// Data
		FabricLoader.getInstance().getEntrypointContainers(ID + "_data", DataProvider.class).forEach(entrypoint -> {
			DataProvider provider = entrypoint.getEntrypoint();
			var classes = provider.provide();
			if (classes.isEmpty()) return;

			ModContainer mod = entrypoint.getProvider();
			String namespace = mod.getMetadata().getId(), name = mod.getMetadata().getName();

			LOGGER.info(String.format(
					"Registering %d %s for %s.",
					classes.size(),
					"data",
					name
			));

			classes.stream()
					.map(clazz -> {
						try {
							return clazz.getDeclaredConstructor().newInstance();
						} catch (Throwable throwable) {
							throw new RuntimeException(String.format(
									"Failed to register knowledge data for %s: constructor not found",
									clazz.getName()
							), throwable);
						}
					})
					.forEach(data -> DATA.register(namespace, data));
		});

		if (!COMPONENTS.asMap().isEmpty()) {
			LOGGER.info(String.format(
					"Successfully registered %d %s for %d %s and %d %s for %d %s. They make you wiser! 📚",

					COMPONENTS.asList().size(),
					COMPONENTS.asList().size() <= 1 ? "knowledge" : "knowledges",
					COMPONENTS.asMap().keySet().size(),
					COMPONENTS.asMap().keySet().size() <= 1 ? "mod" : "mods",

					DATA.asList().size(),
					"data",
					DATA.asMap().keySet().size(),
					DATA.asMap().keySet().size() <= 1 ? "mod" : "mods"
			));
		}
	}

	public static void render(
			@NotNull DrawContext context, @NotNull MinecraftClient client,
			@NotNull PlayerEntity player, @NotNull ClientWorld world
	) {
		COMPONENTS.asList().forEach(knowledge -> {
			if (COMPONENTS.isEnabled(knowledge)) knowledge.render(context, client, player, world);
		});
	}
}
