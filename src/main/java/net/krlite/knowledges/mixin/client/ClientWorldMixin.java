package net.krlite.knowledges.mixin.client;

import net.krlite.knowledges.KnowledgesCommon;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientWorld.class)
public class ClientWorldMixin {
    @Inject(method = "addEntity", at = @At("HEAD"))
    private void addPlayer(int i, Entity entity, CallbackInfo ci) {
        if (entity instanceof PlayerEntity player)
            KnowledgesCommon.CACHE_USERNAME.put(player);
    }
}
