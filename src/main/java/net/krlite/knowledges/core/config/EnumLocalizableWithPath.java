package net.krlite.knowledges.core.config;

import net.minecraft.text.MutableText;

public interface EnumLocalizableWithPath {
    String path();

    MutableText localization();
}
