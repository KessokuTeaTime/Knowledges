package net.krlite.knowledges.core.localization;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public interface Localizable {
    String localizationKey(String... paths);

    default MutableText localize(String... paths) {
        return Text.translatable(localizationKey(paths));
    }
}
