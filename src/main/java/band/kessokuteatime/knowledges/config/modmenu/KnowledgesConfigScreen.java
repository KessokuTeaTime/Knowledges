package band.kessokuteatime.knowledges.config.modmenu;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.api.Requirement;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import me.shedaniel.clothconfig2.impl.builders.BooleanToggleBuilder;
import me.shedaniel.clothconfig2.impl.builders.FieldBuilder;
import band.kessokuteatime.knowledges.KnowledgesClient;
import band.kessokuteatime.knowledges.KnowledgesCommon;
import band.kessokuteatime.knowledges.api.data.Data;
import band.kessokuteatime.knowledges.api.component.Knowledge;
import band.kessokuteatime.knowledges.api.data.transfer.DataInvoker;
import band.kessokuteatime.knowledges.api.data.transfer.DataProtocol;
import band.kessokuteatime.knowledges.api.proxy.ModProxy;
import band.kessokuteatime.knowledges.config.modmenu.impl.EmptyEntryBuilder;
import band.kessokuteatime.knowledges.config.modmenu.impl.KnowledgesConfigBuilder;
import band.kessokuteatime.knowledges.api.core.config.WithIndependentConfigPage;
import band.kessokuteatime.knowledges.api.core.localization.Localizable;
import band.kessokuteatime.knowledges.api.core.path.WithPath;
import band.kessokuteatime.knowledges.Util;
import band.kessokuteatime.knowledges.manager.base.Manager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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
            Util.Map.fastMerge(OBJECT_INDEXED, object, entry);
        }
    }

    private final KnowledgesConfigBuilder configBuilder = (KnowledgesConfigBuilder) new KnowledgesConfigBuilder()
            .setTitle(KnowledgesCommon.localize("screen", "config", "title"))
            .transparentBackground()
            .setShouldTabsSmoothScroll(true)
            .setShouldListSmoothScroll(true)
            .setSavingRunnable(() -> {
                KnowledgesCommon.tidyUpConfig();
                KnowledgesCommon.CONFIG.save();

                KnowledgesClient.tidyUpConfig();
                KnowledgesClient.CONFIG.save();
            });
    private final ConfigEntryBuilder entryBuilder = configBuilder.entryBuilder();

    public KnowledgesConfigScreen(Screen parent) {
        configBuilder.setParentScreen(parent);
        BooleanListEntrySyncHelper.clearAll();

        initGeneralEntries();
        initComponentEntries();
        initDataEntries();

        initIndependentConfigPages(KnowledgesClient.COMPONENTS, component -> componentEntry(component, false), BooleanListEntrySyncHelper.COMPONENTS, "components");
        initIndependentConfigPages(KnowledgesClient.DATA, data -> dataEntry(data, false), BooleanListEntrySyncHelper.DATA, "data");
    }

    public static String localizationKey(String... paths) {
        return KnowledgesCommon.localizationKey("config", paths);
    }

    public static MutableText localize(String... paths) {
        return Text.translatable(localizationKey(paths));
    }

    public static MutableText localizeTooltip(String... paths) {
        return localize(ArrayUtils.add(paths, "tooltip"));
    }

    public static EmptyEntryBuilder emptyEntryBuilder(int height) {
        return new EmptyEntryBuilder(height);
    }

    public static EmptyEntryBuilder emptyEntryBuilder() {
        return new EmptyEntryBuilder();
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
                                KnowledgesClient.CONFIG.get().general.mainScalarAsInt(),
                                500, 2000
                        )
                        .setDefaultValue(KnowledgesClient.DEFAULT_CONFIG.general.mainScalarAsInt())
                        .setTooltip(localizeTooltip("general", "main_scalar"))
                        .setSaveConsumer(KnowledgesClient.CONFIG.get().general::mainScalarAsInt)
                        .setTextGetter(value -> Text.literal(String.format("%.2f", value / 1000.0)))
                        .build()
        );

        category.addEntry(
                entryBuilder.startIntSlider(
                                localize("general", "crosshair_safe_area_scalar"),
                                KnowledgesClient.CONFIG.get().general.crosshairSafeAreaScalarAsInt(),
                                500, 2000
                        )
                        .setDefaultValue(KnowledgesClient.DEFAULT_CONFIG.general.crosshairSafeAreaScalarAsInt())
                        .setTooltip(localizeTooltip("general", "crosshair_safe_area_scalar"))
                        .setSaveConsumer(KnowledgesClient.CONFIG.get().general::crosshairSafeAreaScalarAsInt)
                        .setTextGetter(value -> Text.literal(String.format("%.2f", value / 1000.0)))
                        .build()
        );

        category.addEntry(emptyEntryBuilder().build());

        category.addEntry(
                entryBuilder.startBooleanToggle(
                                localize("general", "visible_in_debug_hud"),
                                KnowledgesClient.CONFIG.get().general.visibleInDebugHud
                        )
                        .setDefaultValue(KnowledgesClient.DEFAULT_CONFIG.general.visibleInDebugHud)
                        .setTooltip(localizeTooltip("general", "visible_in_debug_hud"))
                        .setSaveConsumer(value -> KnowledgesClient.CONFIG.get().general.visibleInDebugHud = value)
                        .build()
        );
    }

    private BooleanToggleBuilder componentEntry(Knowledge component, boolean allowsTooltip) {
        return entryBuilder.startBooleanToggle(
                        component.name(),
                        KnowledgesClient.COMPONENTS.isEnabled(component)
                )
                .setDefaultValue(true)
                .setTooltipSupplier(() -> Optional.ofNullable(
                        allowsTooltip && component.providesTooltip() ? new Text[]{component.tooltip()} : null
                ))
                .setSaveConsumer(value -> KnowledgesClient.COMPONENTS.setEnabled(component, value))
                .setYesNoTextSupplier(BooleanSupplier.ENABLED_DISABLED);
    }

    private BooleanToggleBuilder dataEntry(Data<?> data, boolean allowsTooltip) {
        return entryBuilder.startBooleanToggle(
                        data.name(),
                        KnowledgesClient.DATA.isEnabled(data)
                )
                .setDefaultValue(true)
                .setTooltipSupplier(() -> data.dataInvoker().targetKnowledge()
                        .map(knowledge -> new Text[]{
                                (allowsTooltip && data.providesTooltip()) ? data.tooltip() : Text.empty(),
                                Text.translatable(
                                        localizationKey("data", "footnote"),
                                        Util.Text.withFormatting(knowledge.name(), Formatting.GRAY),
                                        Util.Text.withFormatting(data.dataInvoker().name(), Formatting.GRAY)
                                )
                        }))
                .setSaveConsumer(value -> KnowledgesClient.DATA.setEnabled(data, value))
                .setYesNoTextSupplier(BooleanSupplier.ENABLED_DISABLED);
    }

    private void initComponentEntries() {
        ConfigCategory category = configBuilder.getOrCreateCategory(localize("category", "components"));

        if (!KnowledgesClient.COMPONENTS.asMap().isEmpty()) {
            KnowledgesClient.COMPONENTS.asMap().forEach((namespace, components) -> {
                MutableText name = ModProxy.getModName(namespace);
                boolean isInDefaultNamespace = namespace.equals(KnowledgesCommon.ID);
                if (isInDefaultNamespace) name = localize("components", "default");

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

        if (!KnowledgesClient.DATA.asNamespaceKnowledgeClassifiedMap().isEmpty()) {
            KnowledgesClient.DATA.asNamespaceKnowledgeClassifiedMap().forEach((namespace, map) -> {
                MutableText name = ModProxy.getModName(namespace);
                boolean isInDefaultNamespace = namespace.equals(KnowledgesCommon.ID);
                if (isInDefaultNamespace) name = localize("data", "default");

                ArrayList<AbstractConfigListEntry> entries = new ArrayList<>();

                map.forEach((component, dataList) -> {
                    entries.add(
                            entryBuilder.startTextDescription(Text.translatable(
                                            localizationKey("data", "classifier"),
                                            Util.Text.withFormatting(component.name(), Formatting.GRAY)
                                    ))
                                    .setTooltipSupplier(() -> !component.providesTooltip() ? Optional.empty() : Optional.of(
                                            new Text[]{component.tooltip()}
                                    ))
                                    .build()
                    );

                    Map<DataInvoker<?, ?>, List<Data<?>>> dataInvokerClassified = dataList.stream()
                                    .collect(Collectors.groupingBy(DataProtocol::dataInvoker));
                    var iterator = dataInvokerClassified.values().iterator();

                    while (iterator.hasNext()) {
                        var dataListSegment = iterator.next();
                        entries.addAll(
                                dataListSegment.stream()
                                        .map(data -> {
                                            var built = dataEntry(data, true).build();
                                            BooleanListEntrySyncHelper.DATA.register(data, built);

                                            return (AbstractConfigListEntry) built;
                                        })
                                        .toList()
                        );

                        if (iterator.hasNext()) {
                            entries.add(emptyEntryBuilder().build());
                        }
                    }
                });

                category.addEntry(entryBuilder.startSubCategory(name, entries)
                        .setExpanded(isInDefaultNamespace).build());
            });
        }
    }

    private <T extends WithPath & WithIndependentConfigPage & Localizable.WithName> void initIndependentConfigPages(
            Manager<T> manager,
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
                    entryBuilder.startTextDescription(t.tooltip())
                            .setRequirement(Requirement.isTrue(t::providesTooltip))
                            .build()
            );
            category.addEntry(emptyEntryBuilder(16).build());

            t.buildConfigEntries().apply(entryBuilder).stream()
                    .map(FieldBuilder::build)
                    .forEach(category::addEntry);
        });
    }
}
