package net.krlite.knowledges.api.entrypoint;

import net.krlite.knowledges.api.tag.AdditionalTag;

public interface AdditionalTagProvider<T extends AdditionalTag<?, ?>> extends Provider<T> {
    interface General extends AdditionalTagProvider<AdditionalTag<?, ?>> {

    }
}
