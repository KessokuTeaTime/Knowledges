package net.krlite.knowledges.config.modmenu;

import me.shedaniel.clothconfig2.api.*;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import me.shedaniel.clothconfig2.impl.builders.AbstractFieldBuilder;
import me.shedaniel.clothconfig2.impl.builders.BooleanToggleBuilder;
import net.krlite.knowledges.api.Data;
import net.krlite.knowledges.config.modmenu.impl.KnowledgesConfigBuilder;
import net.krlite.knowledges.core.util.Helper;
import net.krlite.knowledges.Knowledges;
import net.krlite.knowledges.api.Knowledge;
import net.krlite.knowledges.config.KnowledgesConfig;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static net.krlite.knowledges.Knowledges.CONFIG;

public class KnowledgesConfigScreen {
    public static final HashMap<BooleanListEntry, Knowledge<?>> SWITCH_KNOWLEDGE_MAP = new HashMap<>();

    public static final HashMap<Knowledge<?>, List<BooleanListEntry>> KNOWLEDGE_SWITCHES_MAP = new HashMap<>();

	public static final Function<Boolean, Text> ENABLED_DISABLED_SUPPLIER =
			flag -> flag ? localize("text", "enabled") : localize("text", "disabled");

    private final KnowledgesConfigBuilder configBuilder = (KnowledgesConfigBuilder) new KnowledgesConfigBuilder()
            .setTitle(Knowledges.localize("screen", "config", "title"))
            .transparentBackground()
            .setShouldTabsSmoothScroll(true)
            .setShouldListSmoothScroll(true)
            .setSavingRunnable(CONFIG::save);
    private final ConfigEntryBuilder entryBuilder = configBuilder.entryBuilder();

    public KnowledgesConfigScreen(Screen parent) {
        SWITCH_KNOWLEDGE_MAP.clear();
        KNOWLEDGE_SWITCHES_MAP.clear();

        configBuilder.setParentScreen(parent);

        initGeneralEntries();
        initComponentEntries();
        initDataEntries();

        initIndependentConfigPages();
    }

    public Screen build() {
        return configBuilder.build();
    }

    public static String localizationKey(String... paths) {
        return Knowledges.localizationKey("config", paths);
    }

    public static MutableText localize(String... paths) {
        return Text.translatable(localizationKey(paths));
    }

    private void initGeneralEntries() {
        configBuilder.getOrCreateCategorySeparator(localize("separator", "global"));

        ConfigCategory category = configBuilder.getOrCreateCategory(localize("category", "general"));

        // General
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
    }

    private BooleanToggleBuilder componentEntry(Knowledge<?> knowledge, boolean allowsTooltip) {
        return entryBuilder.startBooleanToggle(
                        knowledge.name(),
                        Knowledges.COMPONENTS.isEnabled(knowledge)
                )
                .setDefaultValue(true)
                .setTooltipSupplier(() -> {
                    if (allowsTooltip && knowledge.providesTooltip())
                        return Optional.of(new Text[]{ knowledge.tooltip() });
                    else return Optional.empty();
                })
                .setSaveConsumer(value -> Knowledges.COMPONENTS.setEnabled(knowledge, value))
                .setYesNoTextSupplier(ENABLED_DISABLED_SUPPLIER);
    }

    private BooleanToggleBuilder dataEntry(Data<?, ?> data) {
        return entryBuilder.startBooleanToggle(
                        data.name(),
                        Knowledges.DATA.isEnabled(data)
                )
                .setDefaultValue(true)
                .setTooltipSupplier(() -> data.knowledge()
                        .map(knowledge -> new Text[]{
                                data.tooltip(),
                                Text.translatable(
                                        localizationKey("data", "footnote"),
                                        Helper.Text.withFormatting(knowledge.name(), Formatting.GRAY),
                                        Helper.Text.withFormatting(data.listener().target().name(), Formatting.GRAY)
                                )
                        }))
                .setSaveConsumer(value -> Knowledges.DATA.setEnabled(data, value))
                .setYesNoTextSupplier(ENABLED_DISABLED_SUPPLIER);
    }

    private void initComponentEntries() {
        ConfigCategory category = configBuilder.getOrCreateCategory(localize("category", "components"));

        if (!Knowledges.COMPONENTS.asMap().isEmpty()) {
            Knowledges.COMPONENTS.asMap().forEach((namespace, components) -> {
                MutableText name = Knowledge.Util.getModName(namespace);
                boolean isInDefaultNamespace = namespace.equals(Knowledges.ID);
                if (isInDefaultNamespace) name.append(localize("components", "default"));

                category.addEntry(entryBuilder.startSubCategory(
                        name,
                        components.stream()
                                .map(knowledge -> {
                                    var built = componentEntry(knowledge, true).build();
                                    SWITCH_KNOWLEDGE_MAP.put(built, knowledge);
                                    Helper.Map.fastMerge(KNOWLEDGE_SWITCHES_MAP, knowledge, built);

                                    return (AbstractConfigListEntry) built;
                                })
                                .toList()
                ).setExpanded(isInDefaultNamespace).build());
            });
        }
    }

    private void initDataEntries() {
        ConfigCategory category = configBuilder.getOrCreateCategory(localize("category", "data"));

        if (!Knowledges.DATA.asMap().isEmpty()) {
            Knowledges.DATA.asMap().forEach((namespace, components) -> {
                MutableText name = Knowledge.Util.getModName(namespace);
                boolean isInDefaultNamespace = namespace.equals(Knowledges.ID);
                if (isInDefaultNamespace) name.append(localize("data", "default"));

                category.addEntry(entryBuilder.startSubCategory(
                        name,
                        components.stream()
                                .map(this::dataEntry)
                                .map(builder -> (AbstractConfigListEntry) builder.build())
                                .toList()
                ).setExpanded(isInDefaultNamespace).build());
            });
        }
    }

    private void initIndependentConfigPages() {
        List<Knowledge> components = Knowledges.COMPONENTS.asMap().values().stream()
                .flatMap(List::stream)
                .filter(Knowledge::requestsConfigPage)
                .toList();

        if (!components.isEmpty()) {
            configBuilder.getOrCreateCategorySeparator(localize("separator", "components"));
        }
        
        components.forEach(knowledge -> {
            ConfigCategory category = configBuilder.getOrCreateCategory(knowledge.localize("config", "category"));

            var built = componentEntry(knowledge, false).build();
            SWITCH_KNOWLEDGE_MAP.put(built, knowledge);
            Helper.Map.fastMerge(KNOWLEDGE_SWITCHES_MAP, knowledge, built);

            category.addEntry(built);
            category.addEntry(
                    entryBuilder.startTextDescription(knowledge.tooltip())
                            .setRequirement(Requirement.isTrue(knowledge::providesTooltip))
                            .build()
            );

            knowledge.buildConfigEntries().apply(entryBuilder).stream()
                    .map(AbstractFieldBuilder::build)
                    .forEach(category::addEntry);
        });
    }
}
