package net.krlite.knowledges.core.data;

import net.krlite.knowledges.Knowledges;
import net.krlite.knowledges.api.Knowledge;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public interface DataInvoker<K extends Knowledge, P extends DataProtocol<K>> {
    @NotNull Class<K> targetKnowledgeClass();

    default Optional<Knowledge> targetKnowledge() {
        return Knowledges.COMPONENTS.byClass(targetKnowledgeClass());
    }

    @NotNull Function<List<P>, P> protocolStream();

    default @NotNull P invoker(List<P> protocols) {
        return protocolStream().apply(protocols);
    }

    default @NotNull P invoker() {
        return invoker(Knowledges.DATA.availableProtocols(this));
    }

    default @NotNull String name() {
        String name = Arrays.stream(getClass().getInterfaces())
                .filter(DataInvoker.class::isAssignableFrom)
                .map(Class::getSimpleName)
                .findAny()
                .orElse("");

        return "#" + name;
    }
}
