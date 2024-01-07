package net.krlite.knowledges.mixin.client.clothconfig;

import me.shedaniel.clothconfig2.gui.ClothConfigScreen;
import me.shedaniel.clothconfig2.gui.ClothConfigTabButton;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClothConfigTabButton.class)
public interface ClothConfigTabButtonAccessor {
    @Accessor
    ClothConfigScreen getScreen();
}
