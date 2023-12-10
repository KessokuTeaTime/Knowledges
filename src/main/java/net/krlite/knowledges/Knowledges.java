package net.krlite.knowledges;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.krlite.knowledges.api.Knowledge;
import net.krlite.knowledges.api.ComponentsProvider;
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
	public static final ComponentsManager MANAGER = new ComponentsManager();

	public static String localizationKey(String category, String... paths) {
		return category + "." + ID + "." + String.join(".", paths);
	}

	public static String localizationKey(Knowledge knowledge, String... paths) {
		String namespace = MANAGER.namespace(knowledge).orElse(ID);
		return "knowledge." + namespace + "." + String.join(".", paths);
	}

	public static MutableText localize(String category, String... paths) {
		return Text.translatable(localizationKey(category, paths));
	}

	public static MutableText localize(Knowledge knowledge, String... paths) {
		return Text.translatable(localizationKey(knowledge, paths));
	}

	@Override
	public void onInitialize() {
		InfoComponent.Animations.registerEvents();

		FabricLoader.getInstance().getEntrypointContainers(ID, ComponentsProvider.class).forEach(entrypoint -> {
			ComponentsProvider provider = entrypoint.getEntrypoint();
			var classes = provider.provide();
			if (classes.isEmpty()) return;

			ModContainer mod = entrypoint.getProvider();
			String namespace = mod.getMetadata().getId(), name = mod.getMetadata().getName();

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
					.forEach(knowledge -> MANAGER.register(namespace, knowledge));

			LOGGER.info(String.format(
					"Registered %d %s for %s.",
					classes.size(),
					classes.size() <= 1 ? "knowledge" : "knowledges",
					name
			));
		});

		if (!MANAGER.asMap().isEmpty()) {
			LOGGER.info(String.format(
					"Successfully registered %d %s for %d %s. %s you wiser! 📚",
					MANAGER.asList().size(),
					MANAGER.asList().size() <= 1 ? "knowledge" : "knowledges",
					MANAGER.asMap().keySet().size(),
					MANAGER.asMap().keySet().size() <= 1 ? "mod" : "mods",
					MANAGER.asList().size() <= 1 ? "It makes" : "They make"
			));
		}
	}

	public static void render(
			@NotNull DrawContext context, @NotNull MinecraftClient client,
			@NotNull PlayerEntity player, @NotNull ClientWorld world
	) {
		MANAGER.asList().forEach(knowledge -> {
			if (MANAGER.isEnabled(knowledge)) knowledge.render(context, client, player, world);
		});
	}
}
