package net.krlite.knowledges.api.data.transfer;

import net.krlite.knowledges.KnowledgesClient;
import net.krlite.knowledges.api.component.Knowledge;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * An invoker to provide data restrictions and invoking methods.
 * @param <K>   the target {@link Knowledge} class.
 * @param <P>   the target {@link DataProtocol} class.
 */
public interface DataInvoker<K extends Knowledge, P extends DataProtocol<K>> {
    /**
     * @return  the target {@link Knowledge} class.
     */
    @NotNull Class<K> targetKnowledgeClass();

    default Optional<Knowledge> targetKnowledge() {
        return KnowledgesClient.COMPONENTS.byClass(targetKnowledgeClass());
    }

    @NotNull Function<List<P>, P> protocolStream();

    default @NotNull P invoker(List<P> protocols) {
        return protocolStream().apply(protocols);
    }

    default @NotNull P invoker() {
        return invoker(KnowledgesClient.DATA.availableProtocols(this));
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
