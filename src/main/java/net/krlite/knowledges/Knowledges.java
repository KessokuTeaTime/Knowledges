package net.krlite.knowledges;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.krlite.knowledges.api.Knowledge;
import net.krlite.knowledges.api.KnowledgeProvider;
import net.krlite.knowledges.components.InfoComponent;
import net.krlite.knowledges.config.KnowledgesBanList;
import net.krlite.knowledges.config.KnowledgesConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class Knowledges implements ModInitializer {
	public static final String NAME = "Knowledges", ID = "knowledges";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	public static final KnowledgesConfig CONFIG = new KnowledgesConfig();
	private static final KnowledgesBanList banList = new KnowledgesBanList();
	private static final HashMap<String, List<Knowledge>> knowledges = new HashMap<>();

	public static Map<String, List<Knowledge>> knowledgesMap() {
		return Map.copyOf(knowledges);
	}

	public static List<Knowledge> knowledgesList() {
		return knowledgesMap().values().stream()
				.flatMap(List::stream)
				.toList();
	}

	public static Optional<Knowledge> getKnowledgeById(String namespace, String... paths) {
		return Optional.ofNullable(knowledgesMap().get(namespace))
				.flatMap(list -> list.stream()
						.filter(knowledge -> knowledge.path().equals(String.join(".", paths)))
						.findAny());
	}

	public static Optional<String> getNamespace(Knowledge knowledge) {
		return knowledgesMap().entrySet().stream()
				.filter(entry -> entry.getValue().contains(knowledge))
				.findAny()
				.map(Map.Entry::getKey);
	}

	public static Optional<Identifier> getIdentifier(Knowledge knowledge) {
		return getNamespace(knowledge)
				.map(namespace -> new Identifier(namespace, knowledge.path()));
	}

	public static boolean isKnowledgeIn(Knowledge knowledge, String namespace) {
		return getNamespace(knowledge).equals(Optional.of(namespace));
	}

	public static boolean isDefaultKnowledge(Knowledge knowledge) {
		return isKnowledgeIn(knowledge, ID);
	}

	public static String localizationKey(String category, String... paths) {
		return category + "." + ID + "." + String.join(".", paths);
	}

	public static String localizationKey(Knowledge knowledge, String... paths) {
		String namespace = getNamespace(knowledge).orElse(ID);
		return "knowledge." + namespace + "." + String.join(".", paths);
	}

	public static MutableText localize(String category, String... paths) {
		return Text.translatable(localizationKey(category, paths));
	}

	public static MutableText localize(Knowledge knowledge, String... paths) {
		return Text.translatable(localizationKey(knowledge, paths));
	}

	private static void register(String namespace, Knowledge knowledge) {
		knowledgesMap().getOrDefault(namespace, new ArrayList<>()).add(knowledge);
	}

	public static boolean knowledgeState(Knowledge knowledge) {
		return !banList.isBanned(knowledge);
	}

	public static void knowledgeState(Knowledge knowledge, boolean state) {
		banList.setBanned(knowledge, !state);
	}

	@Override
	public void onInitialize() {
		InfoComponent.Animations.registerEvents();

		FabricLoader.getInstance().getEntrypointContainers(ID, KnowledgeProvider.class).forEach(entrypoint -> {
			KnowledgeProvider provider = entrypoint.getEntrypoint();
			ModContainer modContainer = entrypoint.getProvider();

			modContainer.getContainingMod().ifPresent(mod -> {
				String namespace = mod.getMetadata().getId(), name = mod.getMetadata().getName();

				LOGGER.info("Initializing components for " + name + "...");

				provider.provide().stream()
						.map(clazz -> {
							try {
								return clazz.getDeclaredConstructor().newInstance();
							} catch (Throwable throwable) {
								throw new RuntimeException(
										"Failed to register knowledge for " + clazz.getName() + ": constructor not found", throwable
								);
							}
						})
						.forEach(knowledge -> register(namespace, knowledge));
			});
		});

		LOGGER.info("Successfully registered " + knowledgesMap().size() + " knowledge. They make you wiser! 📚");
	}

	public static void render(
			@NotNull DrawContext context, @NotNull MinecraftClient client,
			@NotNull PlayerEntity player, @NotNull ClientWorld world
	) {
		knowledgesList().forEach(knowledge -> {
			if (!banList.isBanned(knowledge)) knowledge.render(context, client, player, world);
		});
	}

	public static double mapToPower(double x, double power, double threshold) {
		return threshold + (1 - threshold) * Math.pow(x, power);
	}
}
