package net.krlite.knowledges.core.data;

import net.krlite.knowledges.Knowledges;
import net.krlite.knowledges.api.Knowledge;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;

public interface DataInvoker<K extends Knowledge, P extends DataProtocol<K>> {
    @NotNull Class<K> targetKnowledge();

    @NotNull Function<List<P>, P> protocolStream();

    @NotNull P invoker(List<P> protocols);

    default @NotNull P invoker() {
        return invoker(Knowledges.DATA.availableProtocols(this));
    }
}
