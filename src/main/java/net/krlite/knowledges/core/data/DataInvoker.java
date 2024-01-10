package net.krlite.knowledges.core.data;

import net.krlite.knowledges.Knowledges;
import net.krlite.knowledges.api.Knowledge;

import java.util.List;
import java.util.function.Function;

public abstract class DataInvoker<K extends Knowledge, P extends DataProtocol<K>> {
    public abstract Class<K> targetKnowledge();

    protected abstract Function<List<P>, P> protocolStream();

    protected abstract P invoker(List<P> protocols);

    public final P invoker() {
        return invoker(Knowledges.DATA.availableProtocols(this));
    }
}
