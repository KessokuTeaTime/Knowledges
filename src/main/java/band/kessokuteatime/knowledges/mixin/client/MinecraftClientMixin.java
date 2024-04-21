package band.kessokuteatime.knowledges.mixin.client;

import band.kessokuteatime.knowledges.KnowledgesClient;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Inject(
            method = "reloadResources(ZLnet/minecraft/client/MinecraftClient$LoadingContext;)Ljava/util/concurrent/CompletableFuture;",
            at = @At("HEAD")
    )
    private void reloadConfigs(CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        KnowledgesClient.CONFIG.load();
    }
}
