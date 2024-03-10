package net.krlite.knowledges.api.tag;

import net.krlite.knowledges.api.tag.caster.NbtCaster;

public interface TagProtocol<T extends AdditionalTag<?, ?>, E extends Enum<E> & TagProtocol<T, E>> {
    NbtCaster<?> caster();
}
