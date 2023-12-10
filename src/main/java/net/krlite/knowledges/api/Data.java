package net.krlite.knowledges.api;

import net.krlite.knowledges.Knowledges;
import net.krlite.knowledges.core.WithPath;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public interface Data<P, R> extends WithPath {
    default boolean block(P param) {
        return false;
    }

    R get(P param);

    @NotNull Identifier target();

    default boolean providesTooltip() {
        return false;
    }

    default @NotNull Text name() {
        return localize("name");
    }

    default @NotNull Text tooltip() {
        return localize("tooltip");
    }

    default String localizationKey(String... paths) {
        List<String> fullPaths = new ArrayList<>(List.of(path()));
        fullPaths.addAll(List.of(paths));

        return Knowledges.DATA.localizationKey(this, fullPaths.toArray(String[]::new));
    }

    default MutableText localize(String... paths) {
        return Text.translatable(localizationKey(paths));
    }
}
