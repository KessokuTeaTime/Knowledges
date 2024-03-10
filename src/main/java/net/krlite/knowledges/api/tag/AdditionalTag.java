package net.krlite.knowledges.api.tag;

import net.krlite.knowledges.api.core.path.WithPath;
import net.krlite.knowledges.api.representable.Representable;
import net.minecraft.nbt.NbtCompound;

public interface AdditionalTag<R extends Representable<?>, T> extends WithPath {
    boolean isApplicableTo(T t);

    Class<R> targetRepresentableClass();

    void append(NbtCompound data, R representable);
}
