package net.krlite.knowledges.config.modmenu;

import me.shedaniel.clothconfig2.api.*;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import me.shedaniel.clothconfig2.impl.builders.AbstractFieldBuilder;
import me.shedaniel.clothconfig2.impl.builders.BooleanToggleBuilder;
import net.krlite.knowledges.api.Data;
import net.krlite.knowledges.config.modmenu.impl.KnowledgesConfigBuilder;
import net.krlite.knowledges.core.config.WithIndependentConfigPage;
import net.krlite.knowledges.core.util.Helper;
import net.krlite.knowledges.Knowledges;
import net.krlite.knowledges.api.Knowledge;
import net.krlite.knowledges.config.KnowledgesConfig;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.*;
import java.util.function.Function;

import static net.krlite.knowledges.Knowledges.CONFIG;

@SuppressWarnings("UnstableApiUsage")
public class KnowledgesConfigScreen {
    public enum BooleanSupplier implements Function<Boolean, Text> {
        ENABLED_DISABLED(flag -> flag ? localize("text", "enabled") : localize("text", "disabled")),
        DISPLAYED_HIDDEN(flag -> flag ? localize("text", "displayed") : localize("text", "hidden"));

        private final Function<Boolean, Text> supplier;

        BooleanSupplier(Function<Boolean, Text> supplier) {
            this.supplier = supplier;
        }

        @Override
        public Text apply(Boolean flag) {
            return supplier.apply(flag);
        }
    }

    public enum BooleanListEntrySyncHelper {
        COMPONENTS(), DATA();

        private final HashMap<BooleanListEntry, Object> ENTRY_INDEXED;
        private final HashMap<Object, List<BooleanListEntry>> OBJECT_INDEXED;

        BooleanListEntrySyncHelper() {
            ENTRY_INDEXED = new HashMap<>();
            OBJECT_INDEXED = new HashMap<>();
        }

        public static void clearAll() {
            Arrays.stream(values()).forEach(BooleanListEntrySyncHelper::clear);
        }

        public void clear() {
            ENTRY_INDEXED.clear();
            OBJECT_INDEXED.clear();
        }

        public Optional<Object> object(Object entry) {
            return Optional.ofNullable(ENTRY_INDEXED.getOrDefault(entry, null));
        }

        public List<BooleanListEntry> entries(Object object) {
            return OBJECT_INDEXED.getOrDefault(object, new ArrayList<>());
        }

        public void register(Object object, BooleanListEntry entry) {
            ENTRY_INDEXED.put(entry, object);
            Helper.Map.fastMerge(OBJECT_INDEXED, object, entry);
        }
    }

    private final KnowledgesConfigBuilder configBuilder = (KnowledgesConfigBuilder) new KnowledgesConfigBuilder()
            .setTitle(Knowledges.localize("screen", "config", "title"))
            .transparentBackground()
            .setShouldTabsSmoothScroll(true)
            .setShouldListSmoothScroll(true)
            .setSavingRunnable(CONFIG::save);
    private final ConfigEntryBuilder entryBuilder = configBuilder.entryBuilder();

    public KnowledgesConfigScreen(Screen parent) {
        BooleanListEntrySyncHelper.clearAll();

        configBuilder.setParentScreen(parent);

        initGeneralEntries();
        initComponentEntries();
        initDataEntries();

        initComponentIndependentConfigPages();
        initDataIndependentConfigPages();
    }

    public static String localizationKey(String... paths) {
        return Knowledges.localizationKey("config", paths);
    }

    public static MutableText localize(String... paths) {
        return Text.translatable(localizationKey(paths));
    }

    public Screen build() {
        return configBuilder.build();
    }

    private void initGeneralEntries() {
        configBuilder.getOrCreateCategorySeparator(localize("separator", "global"));

        ConfigCategory category = configBuilder.getOrCreateCategory(localize("category", "general"));

        // General
        category.addEntry(
                entryBuilder.startIntSlider(
                                localize("general", "main_scalar"),
                                (int) (CONFIG.globalMainScalar() * 1000),
                                (int) (KnowledgesConfig.Default.GLOBAL_MAIN_SCALAR_MIN * 1000),
                                (int) (KnowledgesConfig.Default.GLOBAL_MAIN_SCALAR_MAX * 1000)
                        )
                        .setDefaultValue((int) (1000 * KnowledgesConfig.Default.GLOBAL_MAIN_SCALAR))
                        .setTooltip(localize("general", "main_scalar", "tooltip"))
                        .setSaveConsumer(value -> CONFIG.globalMainScalar(value.floatValue() / 1000))
                        .setTextGetter(value -> Text.literal(String.format("%.2f", 0.5 + value / 1000.0 * 0.5)))
                        .build()
        );

        category.addEntry(
                entryBuilder.startIntSlider(
                                localize("general", "crosshair_safe_area_scalar"),
                                (int) (CONFIG.globalCrosshairSafeAreaScalar() * 1000),
                                (int) (KnowledgesConfig.Default.GLOBAL_CROSSHAIR_SAFE_AREA_SCALAR_MIN * 1000),
                                (int) (KnowledgesConfig.Default.GLOBAL_CROSSHAIR_SAFE_AREA_SCALAR_MAX * 1000)
                        )
                        .setDefaultValue((int) (1000 * KnowledgesConfig.Default.GLOBAL_CROSSHAIR_SAFE_AREA_SCALAR))
                        .setTooltip(localize("general", "crosshair_safe_area_scalar", "tooltip"))
                        .setSaveConsumer(value -> CONFIG.globalCrosshairSafeAreaScalar(value.floatValue() / 1000))
                        .setTextGetter(value -> Text.literal(String.format("%.2f", 0.5 + value / 1000.0 * 0.5)))
                        .build()
        );
    }

    private BooleanToggleBuilder componentEntry(Knowledge knowledge, boolean allowsTooltip) {
        return entryBuilder.startBooleanToggle(
                        knowledge.name(),
                        Knowledges.COMPONENTS.isEnabled(knowledge)
                )
                .setDefaultValue(true)
                .setTooltipSupplier(() -> {
                    if (allowsTooltip && knowledge.providesTooltip())
                        return Optional.of(new Text[]{knowledge.tooltip()});
                    else return Optional.empty();
                })
                .setSaveConsumer(value -> Knowledges.COMPONENTS.setEnabled(knowledge, value))
                .setYesNoTextSupplier(BooleanSupplier.ENABLED_DISABLED);
    }

    private BooleanToggleBuilder dataEntry(Data<?> data) {
        return entryBuilder.startBooleanToggle(
                        data.name(),
                        Knowledges.DATA.isEnabled(data)
                )
                .setDefaultValue(true)
                .setTooltipSupplier(() -> data.dataInvoker().targetKnowledge()
                        .map(knowledge -> new Text[]{
                                data.providesTooltip() ? data.tooltip() : Text.empty(),
                                Text.translatable(
                                        localizationKey("data", "footnote"),
                                        Helper.Text.withFormatting(knowledge.name(), Formatting.GRAY),
                                        Helper.Text.withFormatting(data.dataInvoker().name(), Formatting.GRAY)
                                )
                        }))
                .setSaveConsumer(value -> Knowledges.DATA.setEnabled(data, value))
                .setYesNoTextSupplier(BooleanSupplier.ENABLED_DISABLED);
    }

    private void initComponentEntries() {
        ConfigCategory category = configBuilder.getOrCreateCategory(localize("category", "components"));

        if (!Knowledges.COMPONENTS.asMap().isEmpty()) {
            Knowledges.COMPONENTS.asMap().forEach((namespace, components) -> {
                MutableText name = Knowledge.Util.modName(namespace);
                boolean isInDefaultNamespace = namespace.equals(Knowledges.ID);
                if (isInDefaultNamespace) name.append(localize("components", "suffix", "default"));

                category.addEntry(entryBuilder.startSubCategory(
                        name,
                        components.stream()
                                .map(c -> {
                                    var built = componentEntry(c, true).build();
                                    BooleanListEntrySyncHelper.COMPONENTS.register(c, built);

                                    return (AbstractConfigListEntry) built;
                                })
                                .toList()
                ).setExpanded(isInDefaultNamespace).build());
            });
        }
    }

    private void initDataEntries() {
        ConfigCategory category = configBuilder.getOrCreateCategory(localize("category", "data"));

        if (!Knowledges.DATA.asNamespaceKnowledgeClassifiedMap().isEmpty()) {
            Knowledges.DATA.asNamespaceKnowledgeClassifiedMap().forEach((namespace, map) -> {
                MutableText name = Knowledge.Util.modName(namespace);
                boolean isInDefaultNamespace = namespace.equals(Knowledges.ID);
                if (isInDefaultNamespace) name.append(localize("data", "suffix", "default"));

                ArrayList<AbstractConfigListEntry> entries = new ArrayList<>();

                map.forEach((knowledge, data) -> {
                    entries.add(
                            entryBuilder.startTextDescription(Text.translatable(
                                            localizationKey("data", "classifier"),
                                            Helper.Text.withFormatting(knowledge.name(), Formatting.GRAY)
                                    ))
                                    .setTooltipSupplier(() -> !knowledge.providesTooltip() ? Optional.empty() : Optional.of(
                                            new Text[]{knowledge.tooltip()}
                                    ))
                                    .build()
                    );
                    entries.addAll(
                            data.stream()
                                    .map(d -> {
                                        var built = dataEntry(d).build();
                                        BooleanListEntrySyncHelper.DATA.register(knowledge, built);

                                        return (AbstractConfigListEntry) built;
                                    })
                                    .toList()
                    );
                });

                category.addEntry(entryBuilder.startSubCategory(name, entries)
                        .setExpanded(isInDefaultNamespace).build());
            });
        }
    }

    private void initComponentIndependentConfigPages() {
        List<Knowledge> components = Knowledges.COMPONENTS.asMap().values().stream()
                .flatMap(List::stream)
                .filter(WithIndependentConfigPage::requestsConfigPage)
                .toList();

        if (!components.isEmpty()) {
            configBuilder.getOrCreateCategorySeparator(localize("separator", "components"));
        }

        components.forEach(c -> {
            ConfigCategory category = configBuilder.getOrCreateCategory(c.name());

            var built = componentEntry(c, false).build();
            BooleanListEntrySyncHelper.COMPONENTS.register(c, built);

            category.addEntry(built);
            category.addEntry(
                    entryBuilder.startTextDescription(((MutableText) c.tooltip()).append("\n"))
                            .setRequirement(Requirement.isTrue(c::providesTooltip))
                            .build()
            );

            c.buildConfigEntries().apply(entryBuilder).stream()
                    .map(AbstractFieldBuilder::build)
                    .forEach(category::addEntry);
        });
    }

    private void initDataIndependentConfigPages() {
        List<Data<?>> data = Knowledges.DATA.asMap().values().stream()
                .flatMap(List::stream)
                .filter(WithIndependentConfigPage::requestsConfigPage)
                .toList();

        if (!data.isEmpty()) {
            configBuilder.getOrCreateCategorySeparator(localize("separator", "data"));
        }

        data.forEach(d -> {
            ConfigCategory category = configBuilder.getOrCreateCategory(d.name());

            var built = dataEntry(d).build();
            BooleanListEntrySyncHelper.DATA.register(d, built);

            category.addEntry(built);
            category.addEntry(
                    entryBuilder.startTextDescription(((MutableText) d.tooltip()).append("\n"))
                            .setRequirement(Requirement.isTrue(d::providesTooltip))
                            .build()
            );

            d.buildConfigEntries().apply(entryBuilder).stream()
                    .map(AbstractFieldBuilder::build)
                    .forEach(category::addEntry);
        });
    }
}
