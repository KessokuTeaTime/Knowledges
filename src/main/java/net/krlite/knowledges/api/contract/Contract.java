package net.krlite.knowledges.api.contract;

import net.krlite.knowledges.api.core.path.WithPath;
import net.krlite.knowledges.api.representable.base.Representable;
import net.minecraft.nbt.NbtCompound;

public interface Contract<R extends Representable<?>, T> extends WithPath {
    boolean isApplicableTo(T t);

    Class<R> targetRepresentableClass();

    void append(NbtCompound data, R representable);
}
