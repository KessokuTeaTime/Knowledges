package net.krlite.knowledges.components;

import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.AbstractFieldBuilder;
import net.krlite.equator.math.algebra.Theory;
import net.krlite.equator.math.geometry.flat.Box;
import net.krlite.equator.render.renderer.Flat;
import net.krlite.equator.visual.color.Palette;
import net.krlite.knowledges.api.Knowledge;
import net.krlite.knowledges.config.KnowledgesConfig;
import net.krlite.knowledges.config.modmenu.KnowledgesConfigScreen;
import net.krlite.knowledges.core.Target;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static net.krlite.knowledges.Knowledges.CONFIG;

public class CrosshairComponent implements Knowledge<CrosshairComponent.CrosshairTarget> {
    public enum CrosshairTarget implements Target {
    }

    @Override
    public Class<CrosshairTarget> targets() {
        return CrosshairTarget.class;
    }

    @Override
    public void render(@NotNull DrawContext context, @NotNull MinecraftClient client, @NotNull PlayerEntity player, @NotNull ClientWorld world) {
        Box box = crosshairSafeArea()
                .scaleCenter(InfoComponent.Animations.Ring.focusingBlock())
                .scaleCenter(1 + 0.3 * InfoComponent.Animations.Ring.mouseHolding());

        // Shadow
        box.render(context, flat ->
                flat.new Rectangle()
                        .colors(Palette.Minecraft.BLACK)
                        .opacityMultiplier(0.08 * InfoComponent.Animations.Ring.ovalOpacity())
                        .new Outlined(box.size())
                        .style(Flat.Rectangle.Outlined.OutliningStyle.EDGE_FADED)
        );

        // Oval
        box.render(context, flat ->
                flat.new Oval()
                        .colorCenter(InfoComponent.Animations.Ring.ovalColor())
                        .mode(Flat.Oval.OvalMode.FILL)
        );

        // Ring
        if (Theory.looseGreater(InfoComponent.Animations.Ring.ringRadians(), 0)) {
            box.render(context, flat ->
                    flat.new Oval()
                            .radians(InfoComponent.Animations.Ring.ringRadians())
                            .mode(Flat.Oval.OvalMode.FILL_GRADIANT_OUT)
                            .opacityMultiplier(InfoComponent.Animations.Ring.ovalOpacity())

                            .colorCenter(InfoComponent.Animations.Ring.ringColor().opacity(0.4))

                            .addColor(0, Palette.TRANSPARENT)
                            .addColor(
                                    InfoComponent.Animations.Ring.ringRadians(),
                                    InfoComponent.Animations.Ring.ringColor()
                            )
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
                                localize("config", "texts_right_enabled"),
                                CONFIG.crosshairTextsRightEnabled()
                        )
                        .setDefaultValue(KnowledgesConfig.Default.CROSSHAIR_TEXTS_RIGHT_ENABLED)
                        .setTooltip(localize("config", "texts_right_enabled", "tooltip"))
                        .setSaveConsumer(CONFIG::crosshairTextsRightEnabled)
                        .setYesNoTextSupplier(KnowledgesConfigScreen.ENABLED_DISABLED_SUPPLIER),

                entryBuilder.startBooleanToggle(
                                localize("config", "texts_left_enabled"),
                                CONFIG.crosshairTextsLeftEnabled()
                        )
                        .setDefaultValue(KnowledgesConfig.Default.CROSSHAIR_TEXTS_LEFT_ENABLED)
                        .setTooltip(localize("config", "texts_left_enabled", "tooltip"))
                        .setSaveConsumer(CONFIG::crosshairTextsLeftEnabled)
                        .setYesNoTextSupplier(KnowledgesConfigScreen.ENABLED_DISABLED_SUPPLIER),

                entryBuilder.startBooleanToggle(
                                localize("config", "subtitles_enabled"),
                                CONFIG.crosshairSubtitlesEnabled()
                        )
                        .setDefaultValue(KnowledgesConfig.Default.CROSSHAIR_SUBTITLES_ENABLED)
                        .setTooltip(localize("config", "subtitles_enabled", "tooltip"))
                        .setSaveConsumer(CONFIG::crosshairSubtitlesEnabled)
                        .setYesNoTextSupplier(KnowledgesConfigScreen.ENABLED_DISABLED_SUPPLIER)
        );
    }
}
