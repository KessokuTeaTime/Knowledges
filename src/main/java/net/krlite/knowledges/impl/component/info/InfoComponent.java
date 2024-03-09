package net.krlite.knowledges.impl.component.info;

import net.krlite.equator.math.geometry.flat.Box;
import net.krlite.equator.math.geometry.flat.Vector;
import net.krlite.equator.render.frame.FrameInfo;
import net.krlite.equator.visual.color.AccurateColor;
import net.krlite.equator.visual.color.Palette;
import net.krlite.equator.visual.color.base.ColorStandard;
import net.krlite.equator.visual.text.Paragraph;
import net.krlite.equator.visual.text.Section;
import net.krlite.knowledges.KnowledgesClient;
import net.krlite.knowledges.api.proxy.KnowledgeProxy;
import net.krlite.knowledges.api.proxy.LayoutProxy;
import net.krlite.knowledges.api.proxy.RenderProxy;
import net.krlite.knowledges.api.representable.Representable;
import net.krlite.knowledges.impl.component.AbstractInfoComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public class InfoComponent extends AbstractInfoComponent {
    @Override
    public void render(RenderProxy renderProxy, @NotNull Representable<?> representable) {
        if (!KnowledgeProxy.hitResultNotAir(representable.hitResult())) reset();

        Box textsRight = FrameInfo.scaled()
                .leftCenter(LayoutProxy.crosshairSafeArea().rightCenter())
                .shift(5 + 3 * LayoutProxy.scalar(), 2 * LayoutProxy.scalar());
        Box textsLeft = FrameInfo.scaled()
                .rightCenter(LayoutProxy.crosshairSafeArea().leftCenter())
                .shift(-5 - 3 * LayoutProxy.scalar(), 2 * LayoutProxy.scalar());

        AccurateColor informativeTint = Palette.Minecraft.WHITE
                .mix(Animation.Ring.ovalColor(), 0.8, ColorStandard.MixMode.PIGMENT)
                .mix(Animation.Ring.ringColor(), Animation.Ring.ringArc() / (Math.PI * 2), ColorStandard.MixMode.PIGMENT);

        // Titles
        titles: {
            // Right
            if (KnowledgesClient.CONFIG.components.crosshair.textsRightEnabled) {
                renderText(
                        renderProxy,
                        textsRight,
                        Animation.Text.titleRight(),
                        Paragraph.Alignment.LEFT,
                        informativeTint.opacity(0.6)
                );
            }

            // Left
            if (KnowledgesClient.CONFIG.components.crosshair.textsLeftEnabled) {
                renderText(
                        renderProxy,
                        textsLeft,
                        Animation.Text.titleLeft(),
                        Paragraph.Alignment.RIGHT,
                        Palette.Minecraft.WHITE.opacity(0.6)
                );
            }
        }

        // Subtitles
        if (KnowledgesClient.CONFIG.components.crosshair.subtitlesEnabled) subtitles: {
            if (KnowledgesClient.CONFIG.components.crosshair.textsRightEnabled) {
                // Right above
                renderText(
                        renderProxy,
                        textsRight.shift(-0.25 * LayoutProxy.scalar(), -8 * LayoutProxy.scalar()),
                        Animation.Text.subtitleRightAbove(),
                        Paragraph.Alignment.LEFT,
                        informativeTint.opacity(0.4),
                        0.82
                );

                // Right below
                renderText(
                        renderProxy,
                        textsRight.shift(-0.25 * LayoutProxy.scalar(), 10.8 * LayoutProxy.scalar()),
                        Animation.Text.subtitleRightBelow(),
                        Paragraph.Alignment.LEFT,
                        Palette.Minecraft.WHITE.opacity(0.4),
                        0.82
                );
            }

            if (KnowledgesClient.CONFIG.components.crosshair.textsLeftEnabled) {
                // Left above
                renderText(
                        renderProxy,
                        textsLeft.shift(0.25 * LayoutProxy.scalar(), -8 * LayoutProxy.scalar()),
                        Animation.Text.subtitleLeftAbove(),
                        Paragraph.Alignment.RIGHT,
                        Palette.Minecraft.WHITE.opacity(0.4),
                        0.82
                );

                // Left below
                renderText(
                        renderProxy,
                        textsLeft.shift(0.25 * LayoutProxy.scalar(), 10.8 * LayoutProxy.scalar()),
                        Animation.Text.subtitleLeftBelow(),
                        Paragraph.Alignment.RIGHT,
                        Palette.Minecraft.WHITE.opacity(0.4),
                        0.82
                );
            }
        }

        // Numeric health
        if (KnowledgesClient.CONFIG.components.infoEntity.showNumericHealth) {
            renderProxy.draw(
                    FrameInfo.scaled()
                            .center(Vector.ZERO)
                            .alignBottom(LayoutProxy.crosshairSafeArea().top())
                            .shift(0, -2 * LayoutProxy.scalar()),
                    flat -> flat.new Text(section -> section.fontSize(0.9 * 0.82 * LayoutProxy.scalar()).append(Animation.Text.numericHealth()))
                            .horizontalAlignment(Paragraph.Alignment.CENTER)
                            .verticalAlignment(Section.Alignment.BOTTOM)
                            .color(informativeTint.opacity(0.6))
            );
        }
    }

    protected void reset() {
        Animation.Text.titleRight(net.minecraft.text.Text.empty());
        Animation.Text.titleLeft(net.minecraft.text.Text.empty());

        Animation.Text.subtitleRightAbove(net.minecraft.text.Text.empty());
        Animation.Text.subtitleRightBelow(net.minecraft.text.Text.empty());
        Animation.Text.subtitleLeftAbove(net.minecraft.text.Text.empty());
        Animation.Text.subtitleLeftBelow(net.minecraft.text.Text.empty());

        Animation.Ring.ringColor(Palette.Minecraft.WHITE);
        Animation.Ring.ovalColor(Palette.Minecraft.WHITE);
    }

    private void renderText(RenderProxy renderProxy, Box box, Text text, Paragraph.Alignment alignment, AccurateColor color, double fontSizeMultiplier) {
        renderProxy.draw(
                box
                        .translateTop(0.5)
                        .shiftTop(-MinecraftClient.getInstance().textRenderer.fontHeight / 2.0 * fontSizeMultiplier),
                flat -> flat.new Text(section -> section.fontSize(0.9 * fontSizeMultiplier * LayoutProxy.scalar()).append(text))
                        .verticalAlignment(Section.Alignment.TOP)
                        .horizontalAlignment(alignment)
                        .color(color)
        );
    }

    private void renderText(RenderProxy renderProxy, Box box, Text text, Paragraph.Alignment alignment, AccurateColor color) {
        renderText(renderProxy, box, text, alignment, color, 1);
    }

    @Override
    public @NotNull String partialPath() {
        return "info";
    }

    @Override
    public @NotNull String path() {
        return partialPath();
    }

    @Override
    public boolean providesTooltip() {
        return true;
    }
}
