package net.krlite.knowledges;

import com.mojang.blaze3d.systems.RenderSystem;
import net.krlite.equator.render.frame.FrameInfo;
import net.krlite.knowledges.api.proxy.RenderProxy;
import net.krlite.knowledges.api.representable.BlockRepresentable;
import net.krlite.knowledges.api.representable.Representable;
import net.krlite.knowledges.impl.representable.EmptyRepresentable;
import net.krlite.knowledges.impl.representable.KnowledgesBlockRepresentable;
import net.krlite.knowledges.impl.representable.KnowledgesEntityRepresentable;
import net.krlite.knowledges.impl.representable.KnowledgesRepresentable;
import net.krlite.knowledges.mixin.client.InGameHudInvoker;
import net.krlite.knowledges.networking.KnowledgesNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.BiConsumer;

public class KnowledgesHud {
    private NbtCompound data = new NbtCompound();

    @Nullable
    private Representable<?> representable;

    private boolean hasServer = false;

    public void onReceiveData(NbtCompound data) {
        this.data = data;
    }

    public void clearData() {
        data = new NbtCompound();
    }

    public void setConnectionStatus(boolean hasServer) {
        this.hasServer = hasServer;
    }

    public NbtCompound data() {
        return data;
    }

    public boolean hasServer() {
        return hasServer;
    }

    public void render(DrawContext context, BiConsumer<RenderProxy, Representable<?>> renderConsumer) {
        Optional.ofNullable(representable).ifPresent(rep -> {

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
                    renderConsumer.accept(new RenderProxy(context), rep);
                }
            }

            context.getMatrices().pop();
        });
    }

    public void tick(MinecraftClient client) {
        World world = client.world;
        PlayerEntity player = client.player;
        HitResult hitResult = client.crosshairTarget;
        if (world == null || player == null || hitResult == null) return;

        Representable<?> representable = null;
        if (hitResult.getType() == HitResult.Type.MISS) {
            representable = fillBuilder(client, EmptyRepresentable.Builder.create())
                    .hitResult(hitResult)
                    .build();
        } else if (hitResult instanceof BlockHitResult blockHitResult) {
            BlockPos blockPos = blockHitResult.getBlockPos();
            representable = fillBuilder(client, KnowledgesBlockRepresentable.Builder.create())
                    .hitResult(blockHitResult)
                    .blockState(world.getBlockState(blockPos))
                    .blockEntity(world.getBlockEntity(blockPos))
                    .build();
        } else if (hitResult instanceof EntityHitResult entityHitResult) {
            representable = fillBuilder(client, KnowledgesEntityRepresentable.Builder.create())
                    .hitResult(entityHitResult)
                    .entity(entityHitResult.getEntity())
                    .build();
        }
        
        this.representable = representable;

        if (representable != null && representable.hasServer()) {
            Identifier channel = null;
            switch (representable.type()) {
                case BLOCK -> channel = KnowledgesNetworking.PACKET_PEEK_BLOCK;
                case ENTITY -> channel = KnowledgesNetworking.PACKET_PEEK_ENTITY;
            }

            if (channel != null) {
                KnowledgesClient.requestDataFor(representable, channel);
            }
        }
    }

    protected <B extends Representable.Builder<?, ?, B>> B fillBuilder(MinecraftClient client, B instance) {
        return instance
                .world(client.world)
                .player(client.player)
                .hasServer(hasServer())
                .data(data());
    }
}
