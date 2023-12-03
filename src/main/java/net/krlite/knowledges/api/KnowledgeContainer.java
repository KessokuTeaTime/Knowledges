package net.krlite.knowledges.api;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public interface KnowledgeContainer {
	@NotNull List<? extends Knowledge> register();
}
