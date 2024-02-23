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
import net.krlite.knowledges.Knowledges;
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
                .scaleCenter(AbstractInfoComponent.Animation.Ring.focusingBlock())
                .scaleCenter(1 + 0.3 * AbstractInfoComponent.Animation.Ring.mouseHolding());

        // Shadow
        box.render(context, flat -> flat.new Rectangle()
                .colors(Palette.Minecraft.BLACK)
                .opacityMultiplier(0.08 * AbstractInfoComponent.Animation.Ring.ovalOpacity())
                .new Outlined(box.size())
                .style(Flat.Rectangle.Outlined.OutliningStyle.EDGE_FADED)
        );

        // Oval
        box.render(context, flat -> flat.new Oval()
                .colorCenter(AbstractInfoComponent.Animation.Ring.ovalColor())
                .mode(Flat.Oval.OvalMode.FILL)
        );

        // Ring
        if (Theory.looseGreater(AbstractInfoComponent.Animation.Ring.ringArc(), 0)) {
            box.render(context, flat -> flat.new Oval()
                    .arc(AbstractInfoComponent.Animation.Ring.ringArc())
                    .mode(Flat.Oval.OvalMode.FILL_GRADIANT_OUT)
                    .opacityMultiplier(AbstractInfoComponent.Animation.Ring.ovalOpacity())

                    .colorCenter(AbstractInfoComponent.Animation.Ring.ringColor().opacity(0.4))

                    .addColor(0, Palette.TRANSPARENT)
                    .addColor(
                            AbstractInfoComponent.Animation.Ring.ringArc(),
                            AbstractInfoComponent.Animation.Ring.ringColor()
                    )
            );
        }

        // Outline
        if (Knowledges.CONFIG.components.crosshair.cursorRingOutlineEnabled) {
            AccurateColor
                    ovalColor = AbstractInfoComponent.Animation.Ring.ovalColor().opacity(0.5),
                    ringColor = AbstractInfoComponent.Animation.Ring.ringColor().opacity(1);

            box.render(context, flat -> flat.new Oval()
                    .mode(Flat.Oval.OvalMode.GRADIANT)
                    .outlineDynamic(Flat.Oval.VertexProvider.OUTER, 0.1 + 0.1 * AbstractInfoComponent.Animation.Ring.blockBreakingProgress())
                    .opacityMultiplier(AbstractInfoComponent.Animation.Ring.ovalOpacity() * (0.4 + 0.6 * AbstractInfoComponent.Animation.Ring.blockBreakingProgress()))

                    .addColor(-Math.PI / 2, ovalColor)
                    .addColor(Math.PI / 2, ovalColor)
                    .addColor(0, ovalColor.mix(
                            ringColor,
                            Curves.Circular.OUT.apply(0, 1, AbstractInfoComponent.Animation.Ring.blockBreakingProgress()),
                            ColorStandard.MixMode.PIGMENT
                    ))

                    .offset(AbstractInfoComponent.Animation.Ring.ringArc())
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
                                Knowledges.CONFIG.components.crosshair.cursorRingOutlineEnabled
                        )
                        .setDefaultValue(false)
                        .setTooltip(localize("config", "cursor_ring_outline", "tooltip"))
                        .setSaveConsumer(value -> Knowledges.CONFIG.components.crosshair.cursorRingOutlineEnabled = value)
                        .setYesNoTextSupplier(KnowledgesConfigScreen.BooleanSupplier.DISPLAYED_HIDDEN),

                entryBuilder.startBooleanToggle(
                                localize("config", "texts_right"),
                                Knowledges.CONFIG.components.crosshair.textsRightEnabled
                        )
                        .setDefaultValue(true)
                        .setTooltip(localize("config", "texts_right", "tooltip"))
                        .setSaveConsumer(value -> Knowledges.CONFIG.components.crosshair.textsRightEnabled = value)
                        .setYesNoTextSupplier(KnowledgesConfigScreen.BooleanSupplier.DISPLAYED_HIDDEN),

                entryBuilder.startBooleanToggle(
                                localize("config", "texts_left"),
                                Knowledges.CONFIG.components.crosshair.textsLeftEnabled
                        )
                        .setDefaultValue(true)
                        .setTooltip(localize("config", "texts_left", "tooltip"))
                        .setSaveConsumer(value -> Knowledges.CONFIG.components.crosshair.textsLeftEnabled = value)
                        .setYesNoTextSupplier(KnowledgesConfigScreen.BooleanSupplier.DISPLAYED_HIDDEN),

                entryBuilder.startBooleanToggle(
                                localize("config", "subtitles"),
                                Knowledges.CONFIG.components.crosshair.subtitlesEnabled
                        )
                        .setDefaultValue(true)
                        .setTooltip(localize("config", "subtitles", "tooltip"))
                        .setSaveConsumer(value -> Knowledges.CONFIG.components.crosshair.subtitlesEnabled = value)
                        .setYesNoTextSupplier(KnowledgesConfigScreen.BooleanSupplier.DISPLAYED_HIDDEN)
        );
    }
}
