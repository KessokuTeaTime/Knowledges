package net.krlite.knowledges.api.data.transfer;

import net.krlite.knowledges.api.component.Knowledge;

public interface DataProtocol<K extends Knowledge> {
    DataInvoker<K, ?> dataInvoker();
}
