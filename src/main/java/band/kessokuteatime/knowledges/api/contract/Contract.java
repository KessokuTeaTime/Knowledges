package band.kessokuteatime.knowledges.api.contract;

import band.kessokuteatime.knowledges.api.core.path.WithPath;
import band.kessokuteatime.knowledges.api.representable.base.Representable;
import net.minecraft.nbt.NbtCompound;

public interface Contract<R extends Representable<?>, T> extends WithPath {
    boolean isApplicableTo(T t);

    Class<R> targetRepresentableClass();

    void append(NbtCompound data, R representable);
}
