package net.krlite.knowledges.component.info;

import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.AbstractFieldBuilder;
import net.krlite.equator.visual.color.Palette;
import net.krlite.knowledges.Knowledges;
import net.krlite.knowledges.component.AbstractInfoComponent;
import net.krlite.knowledges.config.KnowledgesConfig;
import net.krlite.knowledges.config.modmenu.KnowledgesConfigScreen;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;

public class FluidInfoComponent extends AbstractInfoComponent {
    @Override
    public void render(@NotNull DrawContext context, @NotNull MinecraftClient client, @NotNull PlayerEntity player, @NotNull ClientWorld world) {
        Info.crosshairFluidState().ifPresent(fluidState -> {
            BlockState blockState = fluidState.getBlockState();
            MutableText fluidName = blockState.getBlock().getName();

            String
                    namespace = Registries.FLUID.getId(fluidState.getFluid()).getNamespace(),
                    path = Registries.FLUID.getId(fluidState.getFluid()).getPath();

            Animations.Ring.ovalColor(Palette.Minecraft.WHITE);
            Animations.Ring.ringColor(Palette.Minecraft.WHITE);

            // Titles
            titles:
            {
                Animations.Texts.titleRight(fluidName);
                Animations.Texts.titleLeft(Util.modName(namespace));
            }

            // Right Above
            if (client.options.advancedItemTooltips) subtitleRightAbove:{
                Animations.Texts.subtitleRightAbove(Text.literal(path));
            }
            else {
                Animations.Texts.subtitleRightAbove(Text.empty());
            }

            // Right Below
            subtitleRightBelow:
            {
                Animations.Texts.subtitleRightBelow(Text.empty());
            }

            // Left Above
            if (client.options.advancedItemTooltips) subtitleLeftAbove:{
                Animations.Texts.subtitleLeftAbove(Text.literal(namespace));
            }
            else {
                Animations.Texts.subtitleLeftAbove(Text.empty());
            }

            // Left Below
            subtitleLeftBelow:
            {
                int level = fluidState.getLevel();
                Animations.Texts.subtitleLeftBelow(Text.translatable(localizationKey("level"), level));
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
    public Function<ConfigEntryBuilder, List<AbstractFieldBuilder<?, ?, ?>>> buildConfigEntries() {
        return entryBuilder -> List.of(
                entryBuilder.startBooleanToggle(
                                localize("config", "ignores_water"),
                                KnowledgesConfig.InfoFluid.IGNORES_WATER.get()
                        )
                        .setDefaultValue(KnowledgesConfig.InfoFluid.IGNORES_WATER.defaultValue())
                        .setSaveConsumer(KnowledgesConfig.InfoFluid.IGNORES_WATER::set)
                        .setYesNoTextSupplier(KnowledgesConfigScreen.BooleanSupplier.ENABLED_DISABLED),

                entryBuilder.startBooleanToggle(
                                localize("config", "ignores_lava"),
                                KnowledgesConfig.InfoFluid.IGNORES_LAVA.get()
                        )
                        .setDefaultValue(KnowledgesConfig.InfoFluid.IGNORES_LAVA.defaultValue())
                        .setSaveConsumer(KnowledgesConfig.InfoFluid.IGNORES_LAVA::set)
                        .setYesNoTextSupplier(KnowledgesConfigScreen.BooleanSupplier.ENABLED_DISABLED),

                entryBuilder.startBooleanToggle(
                                localize("config", "ignores_other_fluids"),
                                KnowledgesConfig.InfoFluid.IGNORES_OTHER_FLUIDS.get()
                        )
                        .setDefaultValue(KnowledgesConfig.InfoFluid.IGNORES_OTHER_FLUIDS.defaultValue())
                        .setSaveConsumer(KnowledgesConfig.InfoFluid.IGNORES_OTHER_FLUIDS::set)
                        .setYesNoTextSupplier(KnowledgesConfigScreen.BooleanSupplier.ENABLED_DISABLED)
        );
    }
}
