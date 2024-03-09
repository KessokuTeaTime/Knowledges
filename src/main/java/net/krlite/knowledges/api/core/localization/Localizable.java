package net.krlite.knowledges.api.core.localization;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public interface Localizable {
    String localizationKey(String... paths);

    default MutableText localize(String... paths) {
        return Text.translatable(localizationKey(paths));
    }

    interface WithName extends Localizable {
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
}
