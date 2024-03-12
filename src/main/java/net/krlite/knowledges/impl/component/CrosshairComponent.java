package net.krlite.knowledges.impl.component;

import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.AbstractFieldBuilder;
import me.shedaniel.clothconfig2.impl.builders.FieldBuilder;
import net.krlite.equator.math.algebra.Curves;
import net.krlite.equator.math.algebra.Theory;
import net.krlite.equator.math.geometry.flat.Box;
import net.krlite.equator.math.geometry.flat.Vector;
import net.krlite.equator.render.renderer.Flat;
import net.krlite.equator.visual.color.AccurateColor;
import net.krlite.equator.visual.color.Palette;
import net.krlite.equator.visual.color.base.ColorStandard;
import net.krlite.knowledges.KnowledgesClient;
import net.krlite.knowledges.api.component.Knowledge;
import net.krlite.knowledges.api.proxy.LayoutProxy;
import net.krlite.knowledges.api.proxy.RenderProxy;
import net.krlite.knowledges.api.representable.base.Representable;
import net.krlite.knowledges.config.modmenu.KnowledgesConfigScreen;
import net.krlite.knowledges.api.core.localization.EnumLocalizable;
import net.krlite.knowledges.impl.component.base.InfoComponent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.RotationAxis;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class CrosshairComponent implements Knowledge {
    @Override
    public void render(RenderProxy renderProxy, @NotNull Representable<?> representable) {
        Box box = LayoutProxy.crosshairSafeArea()
                .scaleCenter(InfoComponent.Animation.Ring.focusingBlock())
                .scaleCenter(1 + 0.3 * InfoComponent.Animation.Ring.mouseHolding());

        // Shadow
        renderProxy.draw(box, flat -> flat.new Rectangle()
                .colors(Palette.Minecraft.BLACK)
                .opacityMultiplier(0.08 * InfoComponent.Animation.Ring.ovalOpacity())
                .new Outlined(box.size())
                .style(Flat.Rectangle.Outlined.OutliningStyle.EDGE_FADED)
        );

        switch (KnowledgesClient.CONFIG.get().components.crosshair.ringShape) {
            case OVAL -> {
                // Oval
                renderProxy.draw(box, flat -> flat.new Oval()
                        .colorCenter(InfoComponent.Animation.Ring.ovalColor())
                        .mode(Flat.Oval.OvalMode.FILL)
                );

                // Ring
                if (Theory.looseGreater(InfoComponent.Animation.Ring.ringArc(), 0)) {
                    renderProxy.draw(box, flat -> flat.new Oval()
                            .arc(InfoComponent.Animation.Ring.ringArc())
                            .mode(Flat.Oval.OvalMode.FILL_GRADIANT_OUT)
                            .opacityMultiplier(InfoComponent.Animation.Ring.ovalOpacity())

                            .colorCenter(InfoComponent.Animation.Ring.ringColor().opacity(0.4))

                            .addColor(0, Palette.TRANSPARENT)
                            .addColor(
                                    InfoComponent.Animation.Ring.ringArc(),
                                    InfoComponent.Animation.Ring.ringColor()
                            )
                    );
                }

                // Outline
                if (KnowledgesClient.CONFIG.get().components.crosshair.cursorRingOutlineEnabled) {
                    AccurateColor
                            ovalColor = InfoComponent.Animation.Ring.ovalColor().opacity(0.5),
                            ringColor = InfoComponent.Animation.Ring.ringColor().opacity(1);

                    renderProxy.draw(box, flat -> flat.new Oval()
                            .mode(Flat.Oval.OvalMode.GRADIANT)
                            .outlineDynamic(Flat.Oval.VertexProvider.OUTER, 0.1 + 0.05 * InfoComponent.Animation.Ring.blockBreakingProgress())
                            .opacityMultiplier(InfoComponent.Animation.Ring.ovalOpacity() * (0.4 + 0.6 * InfoComponent.Animation.Ring.blockBreakingProgress()))

                            .addColor(-Math.PI / 2, ovalColor)
                            .addColor(Math.PI / 2, ovalColor)
                            .addColor(0, ovalColor.mix(
                                    ringColor,
                                    Curves.Circular.OUT.apply(0, 1, InfoComponent.Animation.Ring.blockBreakingProgress()),
                                    ColorStandard.MixMode.PIGMENT
                            ))

                            .offset(InfoComponent.Animation.Ring.ringArc())
                    );
                }
            }
            case DIAMOND -> {
                renderProxy.pushMatrix();
                renderProxy.withMatrix(matrixStack -> {
                    matrixStack.translate(-0.5, 0, 0); // Refine position
                    matrixStack.scale(0.72F, 0.72F, 0.72F);
                    matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(45));
                });

                // Diamond
                renderProxy.draw(box, flat -> flat.new Rectangle()
                        .colors(InfoComponent.Animation.Ring.ovalColor())
                );

                // Ring
                if (Theory.looseGreater(InfoComponent.Animation.Ring.ringArc(), 0)) {
                    renderProxy.draw(box.scaleCenter(InfoComponent.Animation.Ring.ringArc() / (Math.PI * 2)), flat -> flat.new Rectangle()
                            .opacityMultiplier(InfoComponent.Animation.Ring.ovalOpacity())
                            .colors(InfoComponent.Animation.Ring.ringColor())
                    );
                }

                // Outline
                if (KnowledgesClient.CONFIG.get().components.crosshair.cursorRingOutlineEnabled) {
                    AccurateColor
                            ovalColor = InfoComponent.Animation.Ring.ovalColor().opacity(0.5),
                            ringColor = InfoComponent.Animation.Ring.ringColor().opacity(1);

                    renderProxy.draw(box, flat -> flat.new Rectangle()
                            .opacityMultiplier(InfoComponent.Animation.Ring.ovalOpacity() * (0.4 + 0.6 * InfoComponent.Animation.Ring.blockBreakingProgress()))
                            .colors(ovalColor.mix(
                                    ringColor,
                                    Curves.Circular.OUT.apply(0, 1, InfoComponent.Animation.Ring.blockBreakingProgress()),
                                    ColorStandard.MixMode.PIGMENT
                            ))
                            .new Outlined(Vector.UNIT.scale(1.75 + 1.25 * InfoComponent.Animation.Ring.blockBreakingProgress()))
                            .mode(Flat.Rectangle.Outlined.OutliningMode.SCISSORED)
                    );
                }

                renderProxy.popMatrix();
            }
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
    public Function<ConfigEntryBuilder, List<FieldBuilder<?, ?, ?>>> buildConfigEntries() {
        return entryBuilder -> List.of(
                entryBuilder.startEnumSelector(
                                localizeForConfig("ring_shape"),
                                RingShape.class,
                                KnowledgesClient.CONFIG.get().components.crosshair.ringShape
                        )
                        .setDefaultValue(KnowledgesClient.DEFAULT_CONFIG.components.crosshair.ringShape)
                        .setTooltip(localizeTooltipForConfig("ring_shape"))
                        .setEnumNameProvider(e -> ((EnumLocalizable.WithName) e).localization())
                        .setTooltipSupplier(ringShape -> Optional.of(new Text[]{
                                ringShape.toolip()
                        }))
                        .setSaveConsumer(value -> KnowledgesClient.CONFIG.get().components.crosshair.ringShape = value),

                entryBuilder.startBooleanToggle(
                                localizeForConfig("cursor_ring_outline"),
                                KnowledgesClient.CONFIG.get().components.crosshair.cursorRingOutlineEnabled
                        )
                        .setDefaultValue(KnowledgesClient.DEFAULT_CONFIG.components.crosshair.cursorRingOutlineEnabled)
                        .setTooltip(localizeTooltipForConfig("cursor_ring_outline"))
                        .setSaveConsumer(value -> KnowledgesClient.CONFIG.get().components.crosshair.cursorRingOutlineEnabled = value)
                        .setYesNoTextSupplier(KnowledgesConfigScreen.BooleanSupplier.DISPLAYED_HIDDEN),

                KnowledgesConfigScreen.separatorBuilder(),

                entryBuilder.startBooleanToggle(
                                localizeForConfig("texts_right"),
                                KnowledgesClient.CONFIG.get().components.crosshair.textsRightEnabled
                        )
                        .setDefaultValue(KnowledgesClient.DEFAULT_CONFIG.components.crosshair.textsRightEnabled)
                        .setTooltip(localizeTooltipForConfig("texts_right"))
                        .setSaveConsumer(value -> KnowledgesClient.CONFIG.get().components.crosshair.textsRightEnabled = value)
                        .setYesNoTextSupplier(KnowledgesConfigScreen.BooleanSupplier.DISPLAYED_HIDDEN),

                entryBuilder.startBooleanToggle(
                                localizeForConfig("texts_left"),
                                KnowledgesClient.CONFIG.get().components.crosshair.textsLeftEnabled
                        )
                        .setDefaultValue(KnowledgesClient.DEFAULT_CONFIG.components.crosshair.textsLeftEnabled)
                        .setTooltip(localizeTooltipForConfig("texts_left"))
                        .setSaveConsumer(value -> KnowledgesClient.CONFIG.get().components.crosshair.textsLeftEnabled = value)
                        .setYesNoTextSupplier(KnowledgesConfigScreen.BooleanSupplier.DISPLAYED_HIDDEN),

                entryBuilder.startBooleanToggle(
                                localizeForConfig("subtitles"),
                                KnowledgesClient.CONFIG.get().components.crosshair.subtitlesEnabled
                        )
                        .setDefaultValue(KnowledgesClient.DEFAULT_CONFIG.components.crosshair.subtitlesEnabled)
                        .setTooltip(localizeTooltipForConfig("subtitles"))
                        .setSaveConsumer(value -> KnowledgesClient.CONFIG.get().components.crosshair.subtitlesEnabled = value)
                        .setYesNoTextSupplier(KnowledgesConfigScreen.BooleanSupplier.DISPLAYED_HIDDEN),

                KnowledgesConfigScreen.separatorBuilder(),

                entryBuilder.startIntSlider(
                                localizeForConfig("primary_opacity"),
                                KnowledgesClient.CONFIG.get().components.crosshair.primaryOpacityAsInt(),
                                100, 1000
                        )
                        .setDefaultValue(KnowledgesClient.DEFAULT_CONFIG.components.crosshair.primaryOpacityAsInt())
                        .setTooltip(localizeTooltipForConfig("primary_opacity"))
                        .setSaveConsumer(KnowledgesClient.CONFIG.get().components.crosshair::primaryOpacityAsInt)
                        .setTextGetter(value -> Text.literal(String.format("%.2f", value / 1000.0))),

                entryBuilder.startIntSlider(
                                localizeForConfig("secondary_opacity"),
                                KnowledgesClient.CONFIG.get().components.crosshair.secondaryOpacityAsInt(),
                                100, 1000
                        )
                        .setDefaultValue(KnowledgesClient.DEFAULT_CONFIG.components.crosshair.secondaryOpacityAsInt())
                        .setTooltip(localizeTooltipForConfig("secondary_opacity"))
                        .setSaveConsumer(KnowledgesClient.CONFIG.get().components.crosshair::secondaryOpacityAsInt)
                        .setTextGetter(value -> Text.literal(String.format("%.2f", value / 1000.0)))
        );
    }

    public enum RingShape implements EnumLocalizable.WithName, EnumLocalizable.WithTooltip {
        OVAL("oval"), DIAMOND("diamond");

        private final String path;

        RingShape(String path) {
            this.path = path;
        }

        @Override
        public String path() {
            return path;
        }

        @Override
        public MutableText localization() {
            return KnowledgesClient.localize("ring_shape", path());
        }

        @Override
        public MutableText toolip() {
            return KnowledgesClient.localize("ring_shape", path(), "tooltip");
        }
    }
}
