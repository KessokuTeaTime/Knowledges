package net.krlite.knowledges;

import net.krlite.knowledges.api.Data;
import net.minecraft.util.Identifier;

public interface LinkedDataEnum {
    Identifier target();

    <P, R> Data<P, R> build()
}
