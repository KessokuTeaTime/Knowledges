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
		boolean reset = currentBreakingProgress < AbstractInfoComponent.Animations.Ring.blockBreakingProgress();
		if (reset) System.out.println(currentBreakingProgress + ", " + AbstractInfoComponent.Animations.Ring.blockBreakingProgress());

		AbstractInfoComponent.Animations.Ring.blockBreakingProgress(currentBreakingProgress, reset);
		AbstractInfoComponent.Animations.Ring.ringRadians(Math.PI * 2 * currentBreakingProgress, reset);
	}

	@Inject(method = "cancelBlockBreaking", at = @At("RETURN"))
	private void cancelBlockBreaking(CallbackInfo ci) {
		AbstractInfoComponent.Animations.Ring.blockBreakingProgress(0);
		AbstractInfoComponent.Animations.Ring.ringRadians(0);
	}
}
