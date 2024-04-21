package band.kessokuteatime.knowledges.mixin.client;

import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(InGameHud.class)
public interface InGameHudInvoker {
	@Invoker("shouldRenderSpectatorCrosshair")
	boolean invokeShouldRenderSpectatorCrosshair(HitResult hitResult);
}
