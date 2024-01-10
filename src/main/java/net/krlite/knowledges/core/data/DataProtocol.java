package net.krlite.knowledges.core.data;

import net.krlite.knowledges.api.Knowledge;

public interface DataProtocol<K extends Knowledge> {
    DataInvoker<K, ?> dataInvoker();
}
