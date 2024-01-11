package net.krlite.knowledges.component;

import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.AbstractFieldBuilder;
import net.krlite.equator.math.algebra.Curves;
import net.krlite.equator.math.algebra.Theory;
import net.krlite.equator.math.geometry.flat.Box;
import net.krlite.equator.render.renderer.Flat;
import net.krlite.equator.visual.color.AccurateColor;
import net.krlite.equator.visual.color.Palette;
import net.krlite.equator.visual.color.base.ColorStandard;
import net.krlite.knowledges.api.Knowledge;
import net.krlite.knowledges.config.KnowledgesConfig;
import net.krlite.knowledges.config.modmenu.KnowledgesConfigScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;

public class CrosshairComponent implements Knowledge {
    @Override
    public void render(@NotNull DrawContext context, @NotNull MinecraftClient client, @NotNull PlayerEntity player, @NotNull ClientWorld world) {
        Box box = crosshairSafeArea()
                .scaleCenter(AbstractInfoComponent.Animations.Ring.focusingBlock())
                .scaleCenter(1 + 0.3 * AbstractInfoComponent.Animations.Ring.mouseHolding());

        // Shadow
        box.render(context, flat -> flat.new Rectangle()
                .colors(Palette.Minecraft.BLACK)
                .opacityMultiplier(0.08 * AbstractInfoComponent.Animations.Ring.ovalOpacity())
                .new Outlined(box.size())
                .style(Flat.Rectangle.Outlined.OutliningStyle.EDGE_FADED)
        );

        // Oval
        box.render(context, flat -> flat.new Oval()
                .colorCenter(AbstractInfoComponent.Animations.Ring.ovalColor())
                .mode(Flat.Oval.OvalMode.FILL)
        );

        // Ring
        if (Theory.looseGreater(AbstractInfoComponent.Animations.Ring.ringRadians(), 0)) {
            box.render(context, flat -> flat.new Oval()
                    .arc(AbstractInfoComponent.Animations.Ring.ringRadians())
                    .mode(Flat.Oval.OvalMode.FILL_GRADIANT_OUT)
                    .opacityMultiplier(AbstractInfoComponent.Animations.Ring.ovalOpacity())

                    .colorCenter(AbstractInfoComponent.Animations.Ring.ringColor().opacity(0.4))

                    .addColor(0, Palette.TRANSPARENT)
                    .addColor(
                            AbstractInfoComponent.Animations.Ring.ringRadians(),
                            AbstractInfoComponent.Animations.Ring.ringColor()
                    )
            );
        }

        // Outline
        if (KnowledgesConfig.Component.Crosshair.CURSOR_RING_OUTLINE.get()) {
            AccurateColor
                    ovalColor = AbstractInfoComponent.Animations.Ring.ovalColor().opacity(0.5),
                    ringColor = AbstractInfoComponent.Animations.Ring.ringColor().opacity(1);

            box.render(context, flat -> flat.new Oval()
                    .mode(Flat.Oval.OvalMode.GRADIANT)
                    .outlineDynamic(Flat.Oval.VertexProvider.OUTER, 0.1 + 0.1 * AbstractInfoComponent.Animations.Ring.blockBreakingProgress())
                    .opacityMultiplier(AbstractInfoComponent.Animations.Ring.ovalOpacity() * (0.4 + 0.6 * AbstractInfoComponent.Animations.Ring.blockBreakingProgress()))

                    .addColor(-Math.PI / 2, ovalColor)
                    .addColor(Math.PI / 2, ovalColor)
                    .addColor(0, ovalColor.mix(
                            ringColor,
                            Curves.Circular.OUT.apply(0, 1, AbstractInfoComponent.Animations.Ring.blockBreakingProgress()),
                            ColorStandard.MixMode.PIGMENT
                    ))

                    .offset(AbstractInfoComponent.Animations.Ring.ringRadians())
            );
        }
    }

    @Override
    public @NotNull String path() {
        return "crosshair";
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
                                localize("config", "cursor_ring_outline"),
                                KnowledgesConfig.Component.Crosshair.CURSOR_RING_OUTLINE.get()
                        )
                        .setDefaultValue(KnowledgesConfig.Component.Crosshair.CURSOR_RING_OUTLINE.defaultValue())
                        .setTooltip(localize("config", "cursor_ring_outline", "tooltip"))
                        .setSaveConsumer(KnowledgesConfig.Component.Crosshair.CURSOR_RING_OUTLINE::set)
                        .setYesNoTextSupplier(KnowledgesConfigScreen.BooleanSupplier.DISPLAYED_HIDDEN),

                entryBuilder.startBooleanToggle(
                                localize("config", "texts_right"),
                                KnowledgesConfig.Component.Crosshair.TEXTS_RIGHT.get()
                        )
                        .setDefaultValue(KnowledgesConfig.Component.Crosshair.TEXTS_RIGHT.defaultValue())
                        .setTooltip(localize("config", "texts_right", "tooltip"))
                        .setSaveConsumer(KnowledgesConfig.Component.Crosshair.TEXTS_RIGHT::set)
                        .setYesNoTextSupplier(KnowledgesConfigScreen.BooleanSupplier.DISPLAYED_HIDDEN),

                entryBuilder.startBooleanToggle(
                                localize("config", "texts_left"),
                                KnowledgesConfig.Component.Crosshair.TEXTS_LEFT.get()
                        )
                        .setDefaultValue(KnowledgesConfig.Component.Crosshair.TEXTS_LEFT.defaultValue())
                        .setTooltip(localize("config", "texts_left", "tooltip"))
                        .setSaveConsumer(KnowledgesConfig.Component.Crosshair.TEXTS_LEFT::set)
                        .setYesNoTextSupplier(KnowledgesConfigScreen.BooleanSupplier.DISPLAYED_HIDDEN),

                entryBuilder.startBooleanToggle(
                                localize("config", "subtitles"),
                                KnowledgesConfig.Component.Crosshair.SUBTITLES.get()
                        )
                        .setDefaultValue(KnowledgesConfig.Component.Crosshair.SUBTITLES.defaultValue())
                        .setTooltip(localize("config", "subtitles", "tooltip"))
                        .setSaveConsumer(KnowledgesConfig.Component.Crosshair.SUBTITLES::set)
                        .setYesNoTextSupplier(KnowledgesConfigScreen.BooleanSupplier.DISPLAYED_HIDDEN)
        );
    }
}
