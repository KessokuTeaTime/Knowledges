package net.krlite.knowledges.core.localization;

import net.minecraft.text.MutableText;

public interface EnumLocalizable {
    String path();

    interface WithName extends EnumLocalizable {
        MutableText localization();
    }

    interface WithTooltip extends EnumLocalizable {
        MutableText toolip();
    }
}
