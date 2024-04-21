package band.kessokuteatime.knowledges.api.data.transfer;

import band.kessokuteatime.knowledges.KnowledgesClient;
import band.kessokuteatime.knowledges.api.component.Knowledge;
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

    /**
     * @return  (optional) the target {@link Knowledge} instance.
     */
    default Optional<Knowledge> targetKnowledge() {
        return KnowledgesClient.COMPONENTS.byClass(targetKnowledgeClass());
    }

    /**
     * @return  the streaming function to process all children data to a single invoker.
     */
    @NotNull Function<List<P>, P> protocolStream();

    /**
     * Calls the invoker with children data specified.
     * @param protocols the children data.
     * @return  the streamed data, as a protocol.
     */
    default @NotNull P invoker(List<P> protocols) {
        return protocolStream().apply(protocols);
    }

    /**
     * Calls the invoker.
     * @return  the streamed data, as a protocol.
     */
    default @NotNull P invoker() {
        return invoker(KnowledgesClient.DATA.availableProtocols(this));
    }

    /**
     * @return  the formal name. For example, <code>"#MyProtocolClass"</code>.
     */
    default @NotNull String name() {
        String name = Arrays.stream(getClass().getInterfaces())
                .filter(DataInvoker.class::isAssignableFrom)
                .map(Class::getSimpleName)
                .findAny()
                .orElse("");

        return "#" + name;
    }
}
