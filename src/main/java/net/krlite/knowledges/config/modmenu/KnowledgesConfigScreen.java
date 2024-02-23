package net.krlite.knowledges.config.modmenu;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.api.Requirement;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import me.shedaniel.clothconfig2.impl.builders.AbstractFieldBuilder;
import me.shedaniel.clothconfig2.impl.builders.BooleanToggleBuilder;
import net.krlite.knowledges.Knowledges;
import net.krlite.knowledges.api.Data;
import net.krlite.knowledges.api.Knowledge;
import net.krlite.knowledges.config.modmenu.impl.KnowledgesConfigBuilder;
import net.krlite.knowledges.core.config.WithIndependentConfigPage;
import net.krlite.knowledges.core.localization.LocalizableWithName;
import net.krlite.knowledges.core.path.WithPath;
import net.krlite.knowledges.core.util.Helper;
import net.krlite.knowledges.manager.AbstractManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.*;
import java.util.function.Function;

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
            .setSavingRunnable(Knowledges.CONFIG_HOLDER::save);
    private final ConfigEntryBuilder entryBuilder = configBuilder.entryBuilder();

    public KnowledgesConfigScreen(Screen parent) {
        configBuilder.setParentScreen(parent);
        BooleanListEntrySyncHelper.clearAll();

        initGeneralEntries();
        initComponentEntries();
        initDataEntries();

        initIndependentConfigPages(Knowledges.COMPONENTS, component -> componentEntry(component, false), BooleanListEntrySyncHelper.COMPONENTS, "components");
        initIndependentConfigPages(Knowledges.DATA, data -> dataEntry(data, false), BooleanListEntrySyncHelper.DATA, "data");
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
                                Knowledges.CONFIG.global.mainScalar,
                                500, 2000
                        )
                        .setDefaultValue(1000)
                        .setTooltip(localize("general", "main_scalar", "tooltip"))
                        .setSaveConsumer(value -> Knowledges.CONFIG.global.mainScalar = value)
                        .setTextGetter(value -> Text.literal(String.format("%.2f", value / 1000.0)))
                        .build()
        );

        category.addEntry(
                entryBuilder.startIntSlider(
                                localize("general", "crosshair_safe_area_scalar"),
                                Knowledges.CONFIG.global.crosshairSafeAreaScalar,
                            500, 2000
                        )
                        .setDefaultValue(1000)
                        .setTooltip(localize("general", "crosshair_safe_area_scalar", "tooltip"))
                        .setSaveConsumer(value -> Knowledges.CONFIG.global.crosshairSafeAreaScalar = value)
                        .setTextGetter(value -> Text.literal(String.format("%.2f", value / 1000.0)))
                        .build()
        );
    }

    private BooleanToggleBuilder componentEntry(Knowledge component, boolean allowsTooltip) {
        return entryBuilder.startBooleanToggle(
                        component.name(),
                        Knowledges.COMPONENTS.isEnabled(component)
                )
                .setDefaultValue(true)
                .setTooltipSupplier(() -> Optional.ofNullable(
                        allowsTooltip && component.providesTooltip() ? new Text[]{component.tooltip()} : null
                ))
                .setSaveConsumer(value -> Knowledges.COMPONENTS.setEnabled(component, value))
                .setYesNoTextSupplier(BooleanSupplier.ENABLED_DISABLED);
    }

    private BooleanToggleBuilder dataEntry(Data<?> data, boolean allowsTooltip) {
        return entryBuilder.startBooleanToggle(
                        data.name(),
                        Knowledges.DATA.isEnabled(data)
                )
                .setDefaultValue(true)
                .setTooltipSupplier(() -> data.dataInvoker().targetKnowledge()
                        .map(knowledge -> new Text[]{
                                (allowsTooltip && data.providesTooltip()) ? data.tooltip() : Text.empty(),
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

                map.forEach((component, data) -> {
                    entries.add(
                            entryBuilder.startTextDescription(Text.translatable(
                                            localizationKey("data", "classifier"),
                                            Helper.Text.withFormatting(component.name(), Formatting.GRAY)
                                    ))
                                    .setTooltipSupplier(() -> !component.providesTooltip() ? Optional.empty() : Optional.of(
                                            new Text[]{component.tooltip()}
                                    ))
                                    .build()
                    );
                    entries.addAll(
                            data.stream()
                                    .map(d -> {
                                        var built = dataEntry(d, true).build();
                                        BooleanListEntrySyncHelper.DATA.register(d, built);

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

    private <T extends WithPath & WithIndependentConfigPage & LocalizableWithName> void initIndependentConfigPages(
            AbstractManager<T> manager,
            Function<T, BooleanToggleBuilder> builder,
            BooleanListEntrySyncHelper helper,
            String path
    ) {
        List<T> list = manager.asMap().values().stream()
                .flatMap(List::stream)
                .filter(WithIndependentConfigPage::requestsConfigPage)
                .toList();

        if (!list.isEmpty()) {
            configBuilder.getOrCreateCategorySeparator(localize("separator", path));
        }

        list.forEach(t -> {
            ConfigCategory category = configBuilder.getOrCreateCategory(t.name());

            var built = builder.apply(t).build();
            helper.register(t, built);

            category.addEntry(built);
            category.addEntry(
                    entryBuilder.startTextDescription(((MutableText) t.tooltip()).append("\n"))
                            .setRequirement(Requirement.isTrue(t::providesTooltip))
                            .build()
            );

            t.buildConfigEntries().apply(entryBuilder).stream()
                    .map(AbstractFieldBuilder::build)
                    .forEach(category::addEntry);
        });
    }
}
