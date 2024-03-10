package net.krlite.knowledges.api.entrypoint;

import org.jetbrains.annotations.NotNull;

import java.util.List;

interface Provider<E> {
    @NotNull List<Class<? extends E>> provide();
}
