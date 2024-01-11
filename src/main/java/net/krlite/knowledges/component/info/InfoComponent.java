package net.krlite.knowledges.component.info;

import net.krlite.knowledges.component.AbstractInfoComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.NotNull;

public class InfoComponent extends AbstractInfoComponent {
    @Override
    public void render(@NotNull DrawContext context, @NotNull MinecraftClient client, @NotNull PlayerEntity player, @NotNull ClientWorld world) {
        super.render(context, client, player, world);
    }

    @Override
    public @NotNull String partialPath() {
        return "info";
    }

    @Override
    public boolean providesTooltip() {
        return true;
    }
}
