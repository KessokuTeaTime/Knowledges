package net.krlite.knowledges.config.modmenu;

import me.shedaniel.clothconfig2.api.*;
import me.shedaniel.clothconfig2.impl.builders.BooleanToggleBuilder;
import net.krlite.knowledges.DefaultComponents;
import net.krlite.knowledges.Knowledges;
import net.krlite.knowledges.api.Knowledge;
import net.krlite.knowledges.config.KnowledgesConfig;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static net.krlite.knowledges.Knowledges.CONFIG;

public class KnowledgesConfigScreen {
	public KnowledgesConfigScreen(Screen parent) {
		configBuilder.setParentScreen(parent);

		initGeneralEntries();
		initComponentEntries();
		initIndependentConfigPages();
	}

	public Screen build() {
		return configBuilder.build();
	}

	private final ConfigBuilder configBuilder = ConfigBuilder.create()
												  .setTitle(Knowledges.localize("screen", "config", "title"))
												  .transparentBackground()
												  .setSavingRunnable(CONFIG::save);

	private final ConfigEntryBuilder entryBuilder = configBuilder.entryBuilder();
	
	private MutableText localize(String... paths) {
		return Knowledges.localize("config", paths);
	}
	
	private void initGeneralEntries() {
		ConfigCategory category = configBuilder.getOrCreateCategory(localize("category", "general"));;

		// General
		category.addEntry(
				entryBuilder.startIntSlider(
						localize("general", "crosshair_safe_area_scalar"),
						(int) (CONFIG.crosshairSafeAreaScalar() * 50),
						0, 100
				)
				.setDefaultValue((int) (50 * KnowledgesConfig.Default.CROSSHAIR_SAFE_AREA_SCALAR))
				.setTooltip(localize("general", "crosshair_safe_area_scalar", "tooltip"))
				.setSaveConsumer(value -> CONFIG.crosshairSafeAreaScalar(value.floatValue() / 50))
				.build()
		);

		category.addEntry(
				entryBuilder.startIntSlider(
						localize("general", "main_scalar"),
						(int) (CONFIG.mainScalar() * 50),
						0, 100
				)
				.setDefaultValue((int) (50 * KnowledgesConfig.Default.MAIN_SCALAR))
				.setTooltip(localize("general", "main_scalar", "tooltip"))
				.setSaveConsumer(value -> CONFIG.mainScalar(value.floatValue() / 50))
				.build()
		);

		category.addEntry(
				entryBuilder.startBooleanToggle(
						localize("general", "info_texts_right_enabled"),
						CONFIG.infoTextsRightEnabled()
				)
				.setDefaultValue(KnowledgesConfig.Default.INFO_TEXTS_RIGHT_ENABLED)
				.setTooltip(localize("general", "info_texts_right_enabled", "tooltip"))
				.setSaveConsumer(CONFIG::infoTextsRightEnabled)
				.build()
		);

		category.addEntry(
				entryBuilder.startBooleanToggle(
						localize("general", "info_texts_left_enabled"),
						CONFIG.infoTextsLeftEnabled()
				)
				.setDefaultValue(KnowledgesConfig.Default.INFO_TEXTS_LEFT_ENABLED)
				.setTooltip(localize("general", "info_texts_left_enabled", "tooltip"))
				.setSaveConsumer(CONFIG::infoTextsLeftEnabled)
				.build()
		);

		category.addEntry(
				entryBuilder.startBooleanToggle(
						localize("general", "info_subtitles_enabled"),
						CONFIG.infoSubtitlesEnabled()
				)
				.setDefaultValue(KnowledgesConfig.Default.INFO_SUBTITLES_ENABLED)
				.setTooltip(localize("general", "info_subtitles_enabled", "tooltip"))
				.setSaveConsumer(CONFIG::infoSubtitlesEnabled)
				.build()
		);
	}
	
	private void initComponentEntries() {
		ConfigCategory category = configBuilder.getOrCreateCategory(localize("category", "components"));

		// Components
		List<Knowledge> defaultComponents = new ArrayList<>(), components = new ArrayList<>();

		Knowledges.knowledges().stream()
				.collect(Collectors.groupingBy(DefaultComponents::contains))
				.forEach((isDefaultComponent, knowledges) -> {
					if (isDefaultComponent) defaultComponents.addAll(knowledges);
					else components.addAll(knowledges);
				});

		// Default
		category.addEntry(
				entryBuilder.startSubCategory(
						localize("components", "default"),
						defaultComponents.stream().map(this::buildComponentEntry).toList()
				).build()
		);
		
		// Custom
		if (!components.isEmpty()) {
			category.addEntry(
					entryBuilder.startSubCategory(
							localize("components", "custom"),
							components.stream().map(this::buildComponentEntry).toList()
					).build()
			);
		}
	}
	
	private AbstractConfigListEntry buildComponentEntry(Knowledge knowledge) {
		BooleanToggleBuilder booleanBuilder =
				entryBuilder.startBooleanToggle(
								knowledge.name(),
								Knowledges.knowledgeState(knowledge)
						)
						.setDefaultValue(true)
						.setSaveConsumer(value -> Knowledges.knowledgeState(knowledge, value));
		
		@Nullable Text tooltip = knowledge.tooltip();

		if (tooltip == null) return booleanBuilder.build();
		else return booleanBuilder.setTooltip(tooltip).build();
	}

	private void initIndependentConfigPages() {
		List<Knowledge> components = Knowledges.knowledges().stream()
				.filter(Knowledge::requestsIndependentConfigPage)
				.toList();

		components.forEach(component -> {
			ConfigCategory category = configBuilder.getOrCreateCategory(component.localize("config", "category"));

			component.buildConfigEntries().apply(entryBuilder).forEach(category::addEntry);
		});
	}
}
