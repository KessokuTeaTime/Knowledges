package band.kessokuteatime.knowledges.mixin.common;

import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ItemStack.class)
public interface ItemStackInvoker {
    @Invoker
    static boolean invokeIsSectionVisible(int flags, @NotNull ItemStack.TooltipSection tooltipSection) {
        throw new AssertionError();
    }
}
