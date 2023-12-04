package net.krlite.knowledges.config.modmenu;

import me.shedaniel.clothconfig2.api.*;
import me.shedaniel.clothconfig2.impl.builders.AbstractFieldBuilder;
import me.shedaniel.clothconfig2.impl.builders.BooleanToggleBuilder;
import net.krlite.knowledges.Knowledges;
import net.krlite.knowledges.api.Knowledge;
import net.krlite.knowledges.config.KnowledgesConfig;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static net.krlite.knowledges.Knowledges.CONFIG;

public class KnowledgesConfigScreen {
	public static final Function<Boolean, Text> ENABLED_DISABLED_SUPPLIER =
			flag -> flag ? localize("text", "enabled") : localize("text", "disabled");
    private final ConfigBuilder configBuilder = ConfigBuilder.create()
            .setTitle(Knowledges.localize("screen", "config", "title"))
            .transparentBackground()
            .setSavingRunnable(CONFIG::save);
    private final ConfigEntryBuilder entryBuilder = configBuilder.entryBuilder();

    public KnowledgesConfigScreen(Screen parent) {
        configBuilder.setParentScreen(parent);

        initGeneralEntries();
        initComponentEntries();
        initIndependentConfigPages();
    }

    public Screen build() {
        return configBuilder.build();
    }

    public static MutableText localize(String... paths) {
        return Knowledges.localize("config", paths);
    }

    private void initGeneralEntries() {
        ConfigCategory category = configBuilder.getOrCreateCategory(localize("category", "general"));
        ;

        // General
        category.addEntry(
                entryBuilder.startIntSlider(
                                localize("general", "crosshair_safe_area_scalar"),
                                (int) (CONFIG.crosshairSafeAreaScalar() * 1000),
                                (int) (KnowledgesConfig.Default.CROSSHAIR_SAFE_AREA_SCALAR_MIN * 1000),
                                (int) (KnowledgesConfig.Default.CROSSHAIR_SAFE_AREA_SCALAR_MAX * 1000)
                        )
                        .setDefaultValue((int) (1000 * KnowledgesConfig.Default.CROSSHAIR_SAFE_AREA_SCALAR))
                        .setTooltip(localize("general", "crosshair_safe_area_scalar", "tooltip"))
                        .setSaveConsumer(value -> CONFIG.crosshairSafeAreaScalar(value.floatValue() / 1000))
                        .setTextGetter(value -> Text.literal(String.format("%.2f", 0.5 + value / 1000.0 * 0.5)))
                        .build()
        );

        category.addEntry(
                entryBuilder.startIntSlider(
                                localize("general", "main_scalar"),
                                (int) (CONFIG.mainScalar() * 1000),
								(int) (KnowledgesConfig.Default.MAIN_SCALAR_MIN * 1000),
								(int) (KnowledgesConfig.Default.MAIN_SCALAR_MAX * 1000)
                        )
                        .setDefaultValue((int) (1000 * KnowledgesConfig.Default.MAIN_SCALAR))
                        .setTooltip(localize("general", "main_scalar", "tooltip"))
                        .setSaveConsumer(value -> CONFIG.mainScalar(value.floatValue() / 1000))
						.setTextGetter(value -> Text.literal(String.format("%.2f", 0.5 + value / 1000.0 * 0.5)))
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
						.setYesNoTextSupplier(ENABLED_DISABLED_SUPPLIER)
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
						.setYesNoTextSupplier(ENABLED_DISABLED_SUPPLIER)
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
						.setYesNoTextSupplier(ENABLED_DISABLED_SUPPLIER)
                        .build()
        );
    }

    private void initComponentEntries() {
        ConfigCategory category = configBuilder.getOrCreateCategory(localize("category", "components"));

        // Components
        List<Knowledge> defaultComponents = new ArrayList<>(), components = new ArrayList<>();

        Knowledges.knowledgesList().stream()
                .collect(Collectors.groupingBy(Knowledges::inDefaultNamespace))
                .forEach((isDefaultKnowledge, knowledges) -> {
                    if (isDefaultKnowledge) defaultComponents.addAll(knowledges);
                    else components.addAll(knowledges);
                });

        // Default
        category.addEntry(
                entryBuilder.startSubCategory(
                        localize("components", "default"),
                        defaultComponents.stream()
                                .map(this::componentEntry)
                                .map(Supplier::get)
                                .map(builder -> (AbstractConfigListEntry) builder.build())
                                .toList()
                ).setExpanded(true).build()
        );

        // Custom
        if (!components.isEmpty()) {
            category.addEntry(
                    entryBuilder.startSubCategory(
                            localize("components", "custom"),
                            components.stream().map(this::componentEntry)
                                    .map(Supplier::get)
                                    .map(builder -> (AbstractConfigListEntry) builder.build())
                                    .toList()
                    ).setExpanded(true).build()
            );
        }
    }

    private Supplier<BooleanToggleBuilder> componentEntry(Knowledge knowledge, boolean allowsTooltip) {
        return () -> entryBuilder.startBooleanToggle(
                        knowledge.name(),
                        Knowledges.enabled(knowledge)
                )
                .setTooltipSupplier(() -> {
                    if (allowsTooltip && knowledge.providesTooltip())
                        return Optional.of(new Text[]{knowledge.tooltip()});
                    else return Optional.empty();
                })
                .setDefaultValue(true)
                .setSaveConsumer(value -> Knowledges.enabled(knowledge, value))
                .setYesNoTextSupplier(ENABLED_DISABLED_SUPPLIER);
    }

    private Supplier<BooleanToggleBuilder> componentEntry(Knowledge knowledge) {
        return componentEntry(knowledge, true);
    }

    private void initIndependentConfigPages() {
        List<Knowledge> components = Knowledges.knowledgesMap().values().stream()
                .flatMap(List::stream)
                .filter(Knowledge::requestsConfigPage)
                .toList();
        
        components.forEach(knowledge -> {
            ConfigCategory category = configBuilder.getOrCreateCategory(knowledge.localize("config", "category"));

            category.addEntry(componentEntry(knowledge, false).get().build());
            category.addEntry(
                    entryBuilder.startTextDescription(knowledge.tooltip())
                            .setRequirement(Requirement.isTrue(knowledge::providesTooltip))
                            .build()
            );

            knowledge.buildConfigEntries().apply(entryBuilder).forEach(category::addEntry);
        });
    }
}
