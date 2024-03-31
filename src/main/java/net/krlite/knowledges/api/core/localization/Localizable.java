package net.krlite.knowledges.api.core.localization;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

public interface Localizable {
    enum Separator {
        WORD("_"), KEY("."), RANK("/"), REALM(":");

        private final String string;

        Separator(String string) {
            this.string = string;
        }

        @Override
        public String toString() {
            return string;
        }
    }

    String localizationKey(String... paths);

    default MutableText localize(String... paths) {
        return Text.translatable(localizationKey(paths));
    }

    default String localizationKeyForConfig(String... paths) {
        paths[0] = "config" + Separator.RANK + paths[0];
        return localizationKey(paths);
    }

    default MutableText localizeForConfig(String... paths) {
        return Text.translatable(localizationKeyForConfig(paths));
    }

    default String localizationKeyForConfigTooltip(String... paths) {
        return localizationKeyForConfig(ArrayUtils.add(paths, "tooltip"));
    }

    default MutableText localizeTooltipForConfig(String... paths) {
        return Text.translatable(localizationKeyForConfigTooltip(paths));
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
