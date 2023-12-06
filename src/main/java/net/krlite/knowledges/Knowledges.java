package net.krlite.knowledges;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.krlite.knowledges.api.Knowledge;
import net.krlite.knowledges.api.KnowledgeProvider;
import net.krlite.knowledges.base.InterpolatedText;
import net.krlite.knowledges.components.InfoComponent;
import net.krlite.knowledges.config.KnowledgesBanned;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Knowledges implements ModInitializer {
	public static final String NAME = "Knowledges", ID = "knowledges";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	public static final KnowledgesConfig CONFIG = new KnowledgesConfig();

	private static final KnowledgesBanned banned = new KnowledgesBanned();
	private static final HashMap<String, List<Knowledge>> knowledges = new HashMap<>();

	public static Map<String, List<Knowledge>> knowledgesMap() {
		return Map.copyOf(knowledges);
	}

	public static List<Knowledge> knowledgesList() {
		return knowledgesMap().values().stream()
				.flatMap(List::stream)
				.toList();
	}

	public static Optional<Knowledge> byId(String namespace, String... paths) {
		return Optional.ofNullable(knowledgesMap().get(namespace))
				.flatMap(list -> list.stream()
						.filter(knowledge -> knowledge.path().equals(String.join(".", paths)))
						.findAny());
	}

	public static Optional<String> namespace(Knowledge knowledge) {
		return knowledgesMap().entrySet().stream()
				.filter(entry -> entry.getValue().contains(knowledge))
				.findAny()
				.map(Map.Entry::getKey);
	}

	public static Optional<Identifier> identifier(Knowledge knowledge) {
		return namespace(knowledge)
				.map(namespace -> new Identifier(namespace, knowledge.path()));
	}

	public static boolean inNamespace(Knowledge knowledge, String namespace) {
		return namespace(knowledge).equals(Optional.of(namespace));
	}

	public static boolean inDefaultNamespace(Knowledge knowledge) {
		return inNamespace(knowledge, ID);
	}

	public static boolean enabled(Knowledge knowledge) {
		return !banned.get(knowledge);
	}

	public static void enabled(Knowledge knowledge, boolean enabled) {
		banned.set(knowledge, !enabled);
	}

	public static String localizationKey(String category, String... paths) {
		return category + "." + ID + "." + String.join(".", paths);
	}

	public static String localizationKey(Knowledge knowledge, String... paths) {
		String namespace = namespace(knowledge).orElse(ID);
		return "knowledge." + namespace + "." + String.join(".", paths);
	}

	public static MutableText localize(String category, String... paths) {
		return Text.translatable(localizationKey(category, paths));
	}

	public static MutableText localize(Knowledge knowledge, String... paths) {
		return Text.translatable(localizationKey(knowledge, paths));
	}

	public static <K, V> List<V> fastMerge(HashMap<K, List<V>> hashMap, K key, V defaultValue) {
		return hashMap.merge(
				key,
				new ArrayList<>(List.of(defaultValue)),
				(l1, l2) -> Stream.concat(l1.stream(), l2.stream()).toList()
		);
	}

	private static void register(String namespace, Knowledge knowledge) {
		fastMerge(knowledges, namespace, knowledge);
	}

	@Override
	public void onInitialize() {
		InfoComponent.Animations.registerEvents();

		FabricLoader.getInstance().getEntrypointContainers(ID, KnowledgeProvider.class).forEach(entrypoint -> {
			KnowledgeProvider provider = entrypoint.getEntrypoint();
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
					.forEach(knowledge -> register(namespace, knowledge));

			LOGGER.info(String.format(
					"Registered %d %s for %s.",
					classes.size(),
					classes.size() <= 1 ? "knowledge" : "knowledges",
					name
			));
		});

		if (!knowledgesMap().isEmpty()) {
			LOGGER.info(String.format(
					"Successfully registered %d %s for %d %s. %s you wiser! 📚",
					knowledgesList().size(),
					knowledgesList().size() <= 1 ? "knowledge" : "knowledges",
					knowledgesMap().keySet().size(),
					knowledgesMap().keySet().size() <= 1 ? "mod" : "mods",
					knowledgesList().size() <= 1 ? "It makes" : "They make"
			));
		}
	}

	public static void render(
			@NotNull DrawContext context, @NotNull MinecraftClient client,
			@NotNull PlayerEntity player, @NotNull ClientWorld world
	) {
		knowledgesList().forEach(knowledge -> {
			if (!banned.get(knowledge)) knowledge.render(context, client, player, world);
		});
	}

	public static double mapToPower(double x, double power, double threshold) {
		return threshold + (1 - threshold) * Math.pow(x, power);
	}
}
