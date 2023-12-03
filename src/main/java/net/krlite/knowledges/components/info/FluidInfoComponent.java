package net.krlite.knowledges.components.info;

import net.krlite.equator.visual.color.Palette;
import net.krlite.equator.visual.color.base.ColorStandard;
import net.krlite.knowledges.components.InfoComponent;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public class FluidInfoComponent extends InfoComponent {
    @Override
    public void render(@NotNull DrawContext context, @NotNull MinecraftClient client, @NotNull PlayerEntity player, @NotNull ClientWorld world) {
        super.render(context, client, player, world);

        Info.crosshairFluidState().ifPresent(fluidState -> {
            BlockState blockState = fluidState.getBlockState();
            MutableText fluidName = blockState.getBlock().getName();

            String
                    namespace = Registries.FLUID.getId(fluidState.getFluid()).getNamespace(),
                    path = Registries.FLUID.getId(fluidState.getFluid()).getPath();

            Animations.Ring.ovalColor(Palette.Minecraft.WHITE);

            Animations.Ring.ringRadians(Math.PI * 2);
            Animations.Ring.ringColor(Palette.Minecraft.WHITE);

            // Titles
            titles: {
                Animations.Texts.titleRight(fluidName);
                Animations.Texts.titleLeft(Util.getModName(namespace));
            }

            // Right Above
            if (client.options.advancedItemTooltips) subtitleRightAbove: {
                Animations.Texts.subtitleRightAbove(Text.literal(path));
            } else {
                Animations.Texts.subtitleRightAbove(Text.empty());
            }

            // Right Below
            subtitleRightBelow: {
                Animations.Texts.subtitleRightBelow(Text.empty());
            }

            // Left Above
            if (client.options.advancedItemTooltips) subtitleLeftAbove: {
                Animations.Texts.subtitleLeftAbove(Text.literal(namespace));
            } else {
                Animations.Texts.subtitleLeftAbove(Text.empty());
            }

            // Left Below
            subtitleLeftBelow: {
                Animations.Texts.subtitleLeftBelow(Text.empty());
            }
        });
    }

    @Override
    public @NotNull String infoId() {
        return "fluid";
    }

    @Override
    public boolean provideTooltip() {
        return true;
    }
}
