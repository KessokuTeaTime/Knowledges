package net.krlite.knowledges.core.localization;

import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public interface LocalizableWithName extends Localizable {
    default boolean providesTooltip() {
        return false;
    }

    default @NotNull Text name() {
        return localize("name");
    }

    default @NotNull Text tooltip() {
        return localize("tooltip");
    }
}
