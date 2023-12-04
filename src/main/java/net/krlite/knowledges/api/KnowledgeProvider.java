package net.krlite.knowledges.api;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface KnowledgeProvider {
	@NotNull List<Class<? extends Knowledge>> provide();
}
