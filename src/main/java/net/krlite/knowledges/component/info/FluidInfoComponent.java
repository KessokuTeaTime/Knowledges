package net.krlite.knowledges.component.info;

import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.AbstractFieldBuilder;
import net.krlite.equator.visual.color.Palette;
import net.krlite.knowledges.KnowledgesClient;
import net.krlite.knowledges.component.AbstractInfoComponent;
import net.krlite.knowledges.config.modmenu.KnowledgesConfigScreen;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
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

            Animation.Ring.ovalColor(Palette.Minecraft.WHITE);
            Animation.Ring.ringColor(Palette.Minecraft.WHITE);

            // Titles
            titles:
            {
                Animation.Text.titleRight(fluidName);
                Animation.Text.titleLeft(Util.modName(namespace));
            }

            // Right Above
            if (client.options.advancedItemTooltips) subtitleRightAbove:{
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
            if (client.options.advancedItemTooltips) subtitleLeftAbove:{
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
    public Function<ConfigEntryBuilder, List<AbstractFieldBuilder<?, ?, ?>>> buildConfigEntries() {
        return entryBuilder -> List.of(
                entryBuilder.startBooleanToggle(
                                localize("config", "ignores_water"),
                                KnowledgesClient.CONFIG.components.infoFluid.ignoresWater
                        )
                        .setDefaultValue(KnowledgesClient.DEFAULT_CONFIG.components.infoFluid.ignoresWater)
                        .setSaveConsumer(value -> KnowledgesClient.CONFIG.components.infoFluid.ignoresWater = value)
                        .setYesNoTextSupplier(KnowledgesConfigScreen.BooleanSupplier.ENABLED_DISABLED),

                entryBuilder.startBooleanToggle(
                                localize("config", "ignores_lava"),
                                KnowledgesClient.CONFIG.components.infoFluid.ignoresLava
                        )
                        .setDefaultValue(KnowledgesClient.DEFAULT_CONFIG.components.infoFluid.ignoresLava)
                        .setSaveConsumer(value -> KnowledgesClient.CONFIG.components.infoFluid.ignoresLava = value)
                        .setYesNoTextSupplier(KnowledgesConfigScreen.BooleanSupplier.ENABLED_DISABLED),

                entryBuilder.startBooleanToggle(
                                localize("config", "ignores_other_fluids"),
                                KnowledgesClient.CONFIG.components.infoFluid.ignoresOtherFluids
                        )
                        .setDefaultValue(KnowledgesClient.DEFAULT_CONFIG.components.infoFluid.ignoresOtherFluids)
                        .setSaveConsumer(value -> KnowledgesClient.CONFIG.components.infoFluid.ignoresOtherFluids = value)
                        .setYesNoTextSupplier(KnowledgesConfigScreen.BooleanSupplier.ENABLED_DISABLED)
        );
    }
}
