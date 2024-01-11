package net.krlite.knowledges.core.config;

import net.krlite.pierced.core.EnumLocalizable;
import net.minecraft.text.MutableText;

public interface EnumLocalizableWithPath extends EnumLocalizable {
    String path();

    MutableText localization();

    @Override
    default String getLocalizedName() {
        return path();
    }
}
