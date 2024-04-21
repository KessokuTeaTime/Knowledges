package band.kessokuteatime.knowledges.mixin.client;

import band.kessokuteatime.knowledges.impl.component.base.InfoComponent;
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
		InfoComponent.Animation.Contextual.rawBlockBreakingProgress(currentBreakingProgress);
		InfoComponent.Animation.Contextual.cancelledBlockBreaking(false);
	}

	@Inject(method = "cancelBlockBreaking", at = @At("RETURN"))
	private void cancelBlockBreaking(CallbackInfo ci) {
		InfoComponent.Animation.Contextual.rawBlockBreakingProgress(0);
		InfoComponent.Animation.Contextual.cancelledBlockBreaking(true);
	}
}
