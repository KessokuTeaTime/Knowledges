package net.krlite.knowledges.component.info;

import net.krlite.equator.math.geometry.flat.Box;
import net.krlite.equator.math.geometry.flat.Vector;
import net.krlite.equator.render.frame.FrameInfo;
import net.krlite.equator.visual.color.AccurateColor;
import net.krlite.equator.visual.color.Palette;
import net.krlite.equator.visual.color.base.ColorStandard;
import net.krlite.equator.visual.text.Paragraph;
import net.krlite.equator.visual.text.Section;
import net.krlite.knowledges.Knowledges;
import net.krlite.knowledges.api.Knowledge;
import net.krlite.knowledges.component.AbstractInfoComponent;
import net.krlite.knowledges.config.KnowledgesConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public class InfoComponent extends AbstractInfoComponent {
    @Override
    public void render(@NotNull DrawContext context, @NotNull MinecraftClient client, @NotNull PlayerEntity player, @NotNull ClientWorld world) {
        if (!Info.hasCrosshairTarget()) reset();

        final Box textsRight = FrameInfo.scaled()
                .leftCenter(Knowledge.crosshairSafeArea().rightCenter())
                .shift(5 + 3 * Knowledge.scalar(), 2 * Knowledge.scalar());
        final Box textsLeft = FrameInfo.scaled()
                .rightCenter(Knowledge.crosshairSafeArea().leftCenter())
                .shift(-5 - 3 * Knowledge.scalar(), 2 * Knowledge.scalar());

        final AccurateColor informativeTint = Palette.Minecraft.WHITE
                .mix(Animation.Ring.ovalColor(), 0.8, ColorStandard.MixMode.PIGMENT)
                .mix(Animation.Ring.ringColor(), Animation.Ring.ringArc() / (Math.PI * 2), ColorStandard.MixMode.PIGMENT);

        // Titles
        titles: {
            // Right
            if (Knowledges.CONFIG.components.crosshair.textsRightEnabled) {
                renderText(
                        context,
                        textsRight,
                        Animation.Text.titleRight(),
                        Paragraph.Alignment.LEFT,
                        informativeTint.opacity(0.6)
                );
            }

            // Left
            if (Knowledges.CONFIG.components.crosshair.textsLeftEnabled) {
                renderText(
                        context,
                        textsLeft,
                        Animation.Text.titleLeft(),
                        Paragraph.Alignment.RIGHT,
                        Palette.Minecraft.WHITE.opacity(0.6)
                );
            }
        }

        // Subtitles
        if (Knowledges.CONFIG.components.crosshair.subtitlesEnabled) subtitles: {
            if (Knowledges.CONFIG.components.crosshair.textsRightEnabled) {
                // Right above
                renderText(
                        context,
                        textsRight.shift(-0.25 * Knowledge.scalar(), -8 * Knowledge.scalar()),
                        Animation.Text.subtitleRightAbove(),
                        Paragraph.Alignment.LEFT,
                        informativeTint.opacity(0.4),
                        0.82
                );

                // Right below
                renderText(
                        context,
                        textsRight.shift(-0.25 * Knowledge.scalar(), 10.8 * Knowledge.scalar()),
                        Animation.Text.subtitleRightBelow(),
                        Paragraph.Alignment.LEFT,
                        Palette.Minecraft.WHITE.opacity(0.4),
                        0.82
                );
            }

            if (Knowledges.CONFIG.components.crosshair.textsLeftEnabled) {
                // Left above
                renderText(
                        context,
                        textsLeft.shift(0.25 * Knowledge.scalar(), -8 * Knowledge.scalar()),
                        Animation.Text.subtitleLeftAbove(),
                        Paragraph.Alignment.RIGHT,
                        Palette.Minecraft.WHITE.opacity(0.4),
                        0.82
                );

                // Left below
                renderText(
                        context,
                        textsLeft.shift(0.25 * Knowledge.scalar(), 10.8 * Knowledge.scalar()),
                        Animation.Text.subtitleLeftBelow(),
                        Paragraph.Alignment.RIGHT,
                        Palette.Minecraft.WHITE.opacity(0.4),
                        0.82
                );
            }
        }

        // Numeric health
        if (Knowledges.CONFIG.components.infoEntity.showNumericHealth) {
            FrameInfo.scaled()
                    .center(Vector.ZERO)
                    .alignBottom(Knowledge.crosshairSafeArea().top())
                    .shift(0, -2 * Knowledge.scalar())
                    .render(context, flat -> flat.new Text(section -> section.fontSize(0.9 * 0.82 * Knowledge.scalar()).append(Animation.Text.numericHealth()))
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

    private void renderText(DrawContext context, Box box, Text text, Paragraph.Alignment alignment, AccurateColor color, double fontSizeMultiplier) {
        box
                .translateTop(0.5)
                .shiftTop(-MinecraftClient.getInstance().textRenderer.fontHeight / 2.0 * fontSizeMultiplier)
                .render(context, flat -> flat.new Text(section -> section.fontSize(0.9 * fontSizeMultiplier * Knowledge.scalar()).append(text))
                        .verticalAlignment(Section.Alignment.TOP)
                        .horizontalAlignment(alignment)
                        .color(color)
                );
    }

    private void renderText(DrawContext context, Box box, Text text, Paragraph.Alignment alignment, AccurateColor color) {
        renderText(context, box, text, alignment, color, 1);
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
