package net.krlite.knowledges.mixin.client;

import net.krlite.knowledges.component.AbstractInfoComponent;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {
	@Shadow private float currentBreakingProgress;

	@Inject(method = "updateBlockBreakingProgress", at = @At("RETURN"))
	private void updateBlockBreakingProgress(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
		AbstractInfoComponent.Animations.Ring.blockBreakingProgress(currentBreakingProgress);
	}

	@Inject(method = "cancelBlockBreaking", at = @At("RETURN"))
	private void cancelBlockBreaking(CallbackInfo ci) {
		AbstractInfoComponent.Animations.Ring.blockBreakingProgress(0);
	}
}
