package net.krlite.knowledges;

import com.mojang.blaze3d.systems.RenderSystem;
import net.krlite.equator.render.frame.FrameInfo;
import net.krlite.knowledges.api.component.Knowledge;
import net.krlite.knowledges.api.representable.Representable;
import net.krlite.knowledges.mixin.client.InGameHudInvoker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;

public class KnowledgesHud {
    @Nullable
    private Representable<?> representable;

    public void setRepresentable(@NotNull Representable<?> representable) {
        this.representable = representable;
    }

    public void clearRepresentable() {
        this.representable = null;
    }

    public Optional<Representable<?>> getRepresentable() {
        return Optional.ofNullable(representable);
    }

    public void render(DrawContext context, Consumer<Representable<?>> renderConsumer) {
        getRepresentable().ifPresent(rep -> {

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShader(GameRenderer::getPositionTexProgram);
            RenderSystem.enableBlend();

            context.getMatrices().push();
            context.getMatrices().translate(FrameInfo.scaled().w() / 2, FrameInfo.scaled().h() / 2, 0);

            render: {
                MinecraftClient client = MinecraftClient.getInstance();
                if (client.world == null || client.player == null) break render;

                boolean isFirstPerson = client.options.getPerspective().isFirstPerson();
                boolean isSpectator = client.interactionManager != null && client.interactionManager.getCurrentGameMode() == GameMode.SPECTATOR;
                boolean shouldRenderSpectatorCrosshair = ((InGameHudInvoker) client.inGameHud).invokeShouldRenderSpectatorCrosshair(rep.hitResult());
                boolean isInDebugHud = client.getDebugHud().shouldShowDebugHud()
                        && !client.options.hudHidden
                        && !client.player.hasReducedDebugInfo()
                        && !client.options.getReducedDebugInfo().getValue();

                if (isFirstPerson
                        && (!isSpectator || shouldRenderSpectatorCrosshair)
                        && (KnowledgesClient.CONFIG.global.visibleInDebugHud || !isInDebugHud)
                ) {
                    renderConsumer.accept(rep);
                }
            }

            context.getMatrices().pop();
        });
    }
}
