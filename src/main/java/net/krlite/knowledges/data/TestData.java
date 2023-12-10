package net.krlite.knowledges.data;

import net.krlite.knowledges.api.Data;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class TestData implements Data<Integer, String> {
    @Override
    public String get(Integer param) {
        return String.valueOf(param);
    }

    @Override
    public @NotNull Identifier target() {
        return new Identifier("abc");
    }

    @Override
    public @NotNull String path() {
        return "test";
    }
}
