package band.kessokuteatime.knowledges.impl.component.info;

import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.AbstractFieldBuilder;
import me.shedaniel.clothconfig2.impl.builders.FieldBuilder;
import net.krlite.equator.visual.color.Palette;
import band.kessokuteatime.knowledges.KnowledgesClient;
import band.kessokuteatime.knowledges.api.proxy.ModProxy;
import band.kessokuteatime.knowledges.api.proxy.RenderProxy;
import band.kessokuteatime.knowledges.api.representable.base.Representable;
import band.kessokuteatime.knowledges.impl.component.base.InfoComponent;
import band.kessokuteatime.knowledges.config.modmenu.KnowledgesConfigScreen;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;

public class FluidInfoComponent extends InfoComponent {
    @Override
    public void render(RenderProxy renderProxy, @NotNull Representable<?> representable) {
        ModProxy.getFluidStateNonEmpty(representable.hitResult()).ifPresent(fluidState -> {
            BlockState blockState = fluidState.getBlockState();
            MutableText fluidName = blockState.getBlock().getName();

            String
                    namespace = Registries.FLUID.getId(fluidState.getFluid()).getNamespace(),
                    path = Registries.FLUID.getId(fluidState.getFluid()).getPath();

            Animation.Ring.ovalColor(Palette.Minecraft.WHITE);
            Animation.Ring.ringColor(Palette.Minecraft.WHITE);

            // Titles
            titles:
            {
                Animation.Text.titleRight(fluidName);
                Animation.Text.titleLeft(ModProxy.getModName(namespace));
            }

            // Right Above
            if (MinecraftClient.getInstance().options.advancedItemTooltips) subtitleRightAbove:{
                Animation.Text.subtitleRightAbove(net.minecraft.text.Text.literal(path));
            }
            else {
                Animation.Text.subtitleRightAbove(net.minecraft.text.Text.empty());
            }

            // Right Below
            subtitleRightBelow:
            {
                Animation.Text.subtitleRightBelow(net.minecraft.text.Text.empty());
            }

            // Left Above
            if (MinecraftClient.getInstance().options.advancedItemTooltips) subtitleLeftAbove:{
                Animation.Text.subtitleLeftAbove(net.minecraft.text.Text.literal(namespace));
            }
            else {
                Animation.Text.subtitleLeftAbove(net.minecraft.text.Text.empty());
            }

            // Left Below
            subtitleLeftBelow:
            {
                int level = fluidState.getLevel();
                Animation.Text.subtitleLeftBelow(net.minecraft.text.Text.translatable(localizationKey("level"), level));
            }
        });
    }

    @Override
    public @NotNull String partialPath() {
        return "fluid";
    }

    @Override
    public boolean providesTooltip() {
        return true;
    }

    @Override
    public boolean requestsConfigPage() {
        return true;
    }

    @Override
    public Function<ConfigEntryBuilder, List<FieldBuilder<?, ?, ?>>> buildConfigEntries() {
        return entryBuilder -> List.of(
                entryBuilder.startBooleanToggle(
                                localizeForConfig("ignores_water"),
                                KnowledgesClient.CONFIG.get().components.infoFluid.ignoresWater
                        )
                        .setDefaultValue(KnowledgesClient.DEFAULT_CONFIG.components.infoFluid.ignoresWater)
                        .setSaveConsumer(value -> KnowledgesClient.CONFIG.get().components.infoFluid.ignoresWater = value)
                        .setYesNoTextSupplier(KnowledgesConfigScreen.BooleanSupplier.ENABLED_DISABLED),

                entryBuilder.startBooleanToggle(
                                localizeForConfig("ignores_lava"),
                                KnowledgesClient.CONFIG.get().components.infoFluid.ignoresLava
                        )
                        .setDefaultValue(KnowledgesClient.DEFAULT_CONFIG.components.infoFluid.ignoresLava)
                        .setSaveConsumer(value -> KnowledgesClient.CONFIG.get().components.infoFluid.ignoresLava = value)
                        .setYesNoTextSupplier(KnowledgesConfigScreen.BooleanSupplier.ENABLED_DISABLED),

                entryBuilder.startBooleanToggle(
                                localizeForConfig("ignores_other_fluids"),
                                KnowledgesClient.CONFIG.get().components.infoFluid.ignoresOtherFluids
                        )
                        .setDefaultValue(KnowledgesClient.DEFAULT_CONFIG.components.infoFluid.ignoresOtherFluids)
                        .setSaveConsumer(value -> KnowledgesClient.CONFIG.get().components.infoFluid.ignoresOtherFluids = value)
                        .setYesNoTextSupplier(KnowledgesConfigScreen.BooleanSupplier.ENABLED_DISABLED)
        );
    }
}
